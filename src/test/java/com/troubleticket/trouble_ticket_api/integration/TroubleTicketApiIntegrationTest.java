package com.troubleticket.trouble_ticket_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.troubleticket.generated.model.*;
import com.troubleticket.trouble_ticket_api.infrastructure.persistance.entity.TroubleTicketEntity;
import com.troubleticket.trouble_ticket_api.infrastructure.persistance.repository.SpringDataTroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import; // Added import
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TroubleTicketApiIntegrationTest.TestJwtConfig.class) // FIX: Explicitly injects the test token decoder configuration into the Spring Boot test context
class TroubleTicketApiIntegrationTest {

    private static final String MOCK_TENANT = "tenant-demo";

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("ticket")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SpringDataTroubleTicketRepository springDataRepository;

    @TestConfiguration
    static class TestJwtConfig {
        @Bean
        public JwtDecoder jwtDecoder() {
            String fakeSecret = "v3ryS3cur3AndLongSharedSecr3tKeyForTroubleTicketApi2026";
            SecretKeySpec secretKey = new SecretKeySpec(fakeSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            return NimbusJwtDecoder.withSecretKey(secretKey).build();
        }
    }

    @BeforeEach
    void cleanDatabase() {
        // Ensures database cleanliness between integration flow lifecycle evaluations
        springDataRepository.deleteAll();
    }

    @Test
    void fullFlow() throws Exception {

        TroubleTicketCreateRequest create = new TroubleTicketCreateRequest();
        create.setExternalId("EXT-100");
        create.setServiceId(100L);
        create.setDescription("Internet problem");
        create.setStatus(TroubleTicketCreateStatus.NEW);
        create.setNote("Initial note");

        // 1. Create a trouble ticket
        String response = mvc.perform(
                        post("/troubleTicket")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", MOCK_TENANT).subject("integration-api-user")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(create))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        // 2. Fetch all tickets
        mvc.perform(
                        get("/troubleTicket")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", MOCK_TENANT)))
                )
                .andExpect(status().isOk());

        // 3. Fetch single ticket
        mvc.perform(
                        get("/troubleTicket/" + id)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", MOCK_TENANT)))
                )
                .andExpect(status().isOk());

        // 4. Append note
        NoteCreateRequest note = new NoteCreateRequest();
        note.setText("Second note");

        mvc.perform(
                        post("/troubleTicket/" + id + "/note")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", MOCK_TENANT)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(note))
                )
                .andExpect(status().isCreated());

        // 5. Advance aggregate state machine
        TroubleTicketEntity entity = springDataRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AssertionError("Ticket entity must be saved in database"));
        entity.setStatus(TroubleTicketStatus.ACKNOWLEDGED);
        springDataRepository.save(entity);

        // 6. Close the ticket
        TroubleTicketCloseStatusRequest close = new TroubleTicketCloseStatusRequest();
        close.setStatus(TroubleTicketCloseStatus.CLOSED);

        mvc.perform(
                        patch("/troubleTicket/" + id)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", MOCK_TENANT)))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(close))
                )
                .andExpect(status().isOk());
    }
}

package com.troubleticket.trouble_ticket_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.troubleticket.generated.model.*;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class TroubleTicketApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("ticket")
                    .withUsername("postgres")
                    .withPassword("postgres");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {

        registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
        );

        registry.add(
                "spring.datasource.username",
                postgres::getUsername
        );

        registry.add(
                "spring.datasource.password",
                postgres::getPassword
        );
    }

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void fullFlow() throws Exception {

        TroubleTicketCreateRequest create =
                new TroubleTicketCreateRequest();

        create.setExternalId("EXT-100");

        create.setServiceId(100L);

        create.setDescription("Internet problem");

        create.setStatus(
                TroubleTicketCreateStatus.NEW
        );

        create.setNote("Initial note");


        String response =
                mvc.perform(
                                post("/troubleTicket")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(
                                                objectMapper.writeValueAsString(create)
                                        )
                        )
                        .andExpect(status().isCreated())
                        .andReturn()
                        .getResponse()
                        .getContentAsString();


        String id =
                objectMapper.readTree(response)
                        .get("id")
                        .asText();


        mvc.perform(
                        get("/troubleTicket")
                )
                .andExpect(status().isOk());


        mvc.perform(
                        get("/troubleTicket/" + id)
                )
                .andExpect(status().isOk());


        NoteCreateRequest note =
                new NoteCreateRequest();

        note.setText("Second note");


        mvc.perform(
                        post("/troubleTicket/" + id + "/note")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(note)
                                )
                )
                .andExpect(status().isCreated());


        TroubleTicketCloseStatusRequest close =
                new TroubleTicketCloseStatusRequest();

        close.setStatus(
                TroubleTicketCloseStatus.CLOSED
        );


        mvc.perform(
                        patch("/troubleTicket/" + id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(close)
                                )
                )
                .andExpect(status().isOk());
    }

}
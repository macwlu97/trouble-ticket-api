package com.troubleticket.trouble_ticket_api.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.troubleticket.generated.model.TroubleTicketCreateRequest;
import com.troubleticket.trouble_ticket_api.application.port.in.CloseTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.CreateTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.GetTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicketStatus;
import com.troubleticket.trouble_ticket_api.domain.model.value.ExternalId;
import com.troubleticket.trouble_ticket_api.domain.model.value.ServiceId;
import com.troubleticket.trouble_ticket_api.domain.model.value.TenantId;
import com.troubleticket.trouble_ticket_api.domain.model.value.TroubleTicketId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TroubleTicketController.class)
@Import(com.troubleticket.trouble_ticket_api.security.SecurityConfiguration.class)
class TroubleTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreateTroubleTicketUseCase createUseCase;

    @MockitoBean
    private GetTroubleTicketUseCase getUseCase;

    @MockitoBean
    private CloseTroubleTicketUseCase closeUseCase;

    @MockitoBean
    private com.troubleticket.trouble_ticket_api.api.mapper.TroubleTicketMapper mapper;

    @Test
    void shouldReturnCreated() throws Exception {
        TroubleTicketCreateRequest request = new TroubleTicketCreateRequest();
        request.setExternalId("EXT-1");
        request.setServiceId(10L);
        request.setDescription("Problem");
        request.setNote("Initial note for the ticket");
        request.setStatus(com.troubleticket.generated.model.TroubleTicketCreateStatus.NEW);

        TroubleTicket domain = new TroubleTicket(
                new TroubleTicketId("TT-2026-C70A230E"),
                new TenantId("tenant-demo"),
                new ExternalId("EXT-1"),
                new ServiceId(10L),
                "Problem",
                new TroubleTicketStatus.New(),
                OffsetDateTime.now(),
                List.of()
        );

        com.troubleticket.generated.model.TroubleTicket apiTicket =
                new com.troubleticket.generated.model.TroubleTicket();
        apiTicket.setId("TT-2026-C70A230E");
        apiTicket.setExternalId("EXT-1");
        apiTicket.setServiceId(10L);
        apiTicket.setDescription("Problem");

        when(createUseCase.create(any()))
                .thenReturn(domain);

        when(mapper.toApi(any(TroubleTicket.class)))
                .thenReturn(apiTicket);

        mockMvc.perform(
                        post("/troubleTicket")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", "tenant-demo").subject("partner-api-user")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnList() throws Exception {

        when(getUseCase.getAll())
                .thenReturn(List.of());

        when(mapper.toSummaryList(any()))
                .thenReturn(List.of());

        mockMvc.perform(
                        get("/troubleTicket")
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", "tenant-demo")))
                )
                .andExpect(status().isOk());
    }
}

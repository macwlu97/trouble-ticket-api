package com.troubleticket.trouble_ticket_api.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.troubleticket.generated.model.TroubleTicketCreateRequest;
import com.troubleticket.generated.model.TroubleTicketCloseStatusRequest;
import com.troubleticket.generated.model.NoteCreateRequest;
import com.troubleticket.trouble_ticket_api.application.port.in.CloseTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.CreateTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.GetTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.AddNoteUseCase;
import com.troubleticket.trouble_ticket_api.domain.exception.ServiceNotFoundException;
import com.troubleticket.trouble_ticket_api.domain.exception.TroubleTicketNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({TroubleTicketController.class, TroubleTicketNoteController.class})
@Import(com.troubleticket.trouble_ticket_api.security.SecurityConfiguration.class)
class TroubleTicketControllerNegativeTest {

    private static final String CONTEXT_PATH = "/api/v1";
    private static final String SAMPLE_TICKET_ID = "TT-2026-C70A230E";

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
    private AddNoteUseCase addNoteUseCase;

    @MockitoBean
    private com.troubleticket.trouble_ticket_api.api.mapper.TroubleTicketMapper mapper;

    @Test
    void shouldReturn403WhenNoToken() throws Exception {
        TroubleTicketCreateRequest request = new TroubleTicketCreateRequest();
        request.setExternalId("EXT-1");
        request.setServiceId(10L);
        request.setDescription("Problem");
        request.setNote("Initial note");
        request.setStatus(com.troubleticket.generated.model.TroubleTicketCreateStatus.NEW);

        mockMvc.perform(
                        post(CONTEXT_PATH + "/troubleTicket")
                                .contextPath(CONTEXT_PATH)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404WhenInvalidServiceId() throws Exception {
        TroubleTicketCreateRequest request = new TroubleTicketCreateRequest();
        request.setExternalId("EXT-1");
        request.setServiceId(999999L);
        request.setDescription("Problem");
        request.setNote("Initial note");
        request.setStatus(com.troubleticket.generated.model.TroubleTicketCreateStatus.NEW);

        when(createUseCase.create(any()))
                .thenThrow(new ServiceNotFoundException(999999L));

        mockMvc.perform(
                        post(CONTEXT_PATH + "/troubleTicket")
                                .contextPath(CONTEXT_PATH)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", "tenant-demo")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("SERVICE_NOT_FOUND"));
    }

    @Test
    void shouldReturn404WhenTicketNotFound() throws Exception {
        when(getUseCase.getById(SAMPLE_TICKET_ID))
                .thenThrow(new TroubleTicketNotFoundException(SAMPLE_TICKET_ID));

        mockMvc.perform(
                        get(CONTEXT_PATH + "/troubleTicket/" + SAMPLE_TICKET_ID)
                                .contextPath(CONTEXT_PATH)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", "tenant-demo")))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TROUBLE_TICKET_NOT_FOUND"));
    }

    @Test
    void shouldReturn400WhenMissingRequiredFields() throws Exception {
        TroubleTicketCreateRequest request = new TroubleTicketCreateRequest();

        mockMvc.perform(
                        post(CONTEXT_PATH + "/troubleTicket")
                                .contextPath(CONTEXT_PATH)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", "tenant-demo")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenClosingNonExistentTicket() throws Exception {
        TroubleTicketCloseStatusRequest request = new TroubleTicketCloseStatusRequest();
        request.setStatus(com.troubleticket.generated.model.TroubleTicketCloseStatus.CLOSED);

        when(closeUseCase.close(SAMPLE_TICKET_ID))
                .thenThrow(new TroubleTicketNotFoundException(SAMPLE_TICKET_ID));

        mockMvc.perform(
                        patch(CONTEXT_PATH + "/troubleTicket/" + SAMPLE_TICKET_ID)
                                .contextPath(CONTEXT_PATH)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", "tenant-demo")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TROUBLE_TICKET_NOT_FOUND"));
    }

    @Test
    void shouldReturn404WhenAddingNoteToNonExistentTicket() throws Exception {
        NoteCreateRequest request = new NoteCreateRequest();
        request.setText("Test note");

        when(addNoteUseCase.addNote(any(), any()))
                .thenThrow(new TroubleTicketNotFoundException(SAMPLE_TICKET_ID));

        mockMvc.perform(
                        post(CONTEXT_PATH + "/troubleTicket/" + SAMPLE_TICKET_ID + "/note")
                                .contextPath(CONTEXT_PATH)
                                .with(SecurityMockMvcRequestPostProcessors.jwt()
                                        .jwt(jwt -> jwt.claim("tenant_id", "tenant-demo")))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TROUBLE_TICKET_NOT_FOUND"));
    }
}
package com.troubleticket.trouble_ticket_api.application.service;

import com.troubleticket.generated.model.NoteCreateRequest;
import com.troubleticket.generated.model.TroubleTicketCreateRequest;
import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.model.Note;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicketStatus;
import com.troubleticket.trouble_ticket_api.domain.model.value.*;
import com.troubleticket.trouble_ticket_api.security.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TroubleTicketServiceTest {

    private static final String MOCK_TENANT = "tenant-demo";

    private TroubleTicketRepository repository;
    private TroubleTicketService service;

    @BeforeEach
    void setUp() {
        repository = mock(TroubleTicketRepository.class);
        service = new TroubleTicketService(repository);
        TenantContext.setTenantId(MOCK_TENANT);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    private TroubleTicket createTestTicket(String id) {
        return new TroubleTicket(
                new TroubleTicketId(id),
                new TenantId(MOCK_TENANT),
                new ExternalId("EXT"),
                new ServiceId(1L),
                "Description",
                new TroubleTicketStatus.New(),
                OffsetDateTime.now(),
                new ArrayList<>()
        );
    }

    @Test
    void shouldCreateTicket() {
        TroubleTicketCreateRequest request = new TroubleTicketCreateRequest();
        request.setExternalId("EXT-100");
        request.setServiceId(123L);
        request.setDescription("Internet problem");
        request.setNote("Initial note");
        request.setStatus(com.troubleticket.generated.model.TroubleTicketCreateStatus.NEW);

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TroubleTicket ticket = service.create(request);

        assertNotNull(ticket);
        assertEquals(MOCK_TENANT, ticket.getTenantId().value());
        assertEquals("EXT-100", ticket.getExternalId().value());
        assertEquals(123L, ticket.getServiceId().value());
        assertEquals("Internet problem", ticket.getDescription());
        assertTrue(ticket.getStatus() instanceof TroubleTicketStatus.New);
        assertEquals(1, ticket.getNotes().size());

        verify(repository).save(any(TroubleTicket.class));
    }

    @Test
    void shouldFindTicketById() {
        String idValue = "TT-2026-TEST";
        TroubleTicket ticket = createTestTicket(idValue);
        TroubleTicketId ticketId = new TroubleTicketId(idValue);

        when(repository.findByIdAndTenantId(ticketId, new TenantId(MOCK_TENANT)))
                .thenReturn(Optional.of(ticket));

        TroubleTicket result = service.getById(idValue);

        assertEquals(ticket, result);
    }

    @Test
    void shouldAddNote() {
        TroubleTicket ticket = createTestTicket("TT-2026-123");
        ticket.acknowledge();

        when(repository.findByIdAndTenantId(any(), any())).thenReturn(Optional.of(ticket));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        NoteCreateRequest request = new NoteCreateRequest();
        request.setText("New note");

        Note note = service.addNote(ticket.getId().value(), request);

        assertEquals("New note", note.text());
        assertEquals(1, ticket.getNotes().size());
        verify(repository).save(ticket);
    }

    @Test
    void shouldCloseTicket() {
        TroubleTicket ticket = createTestTicket("TT-2026-123");
        ticket.acknowledge();

        when(repository.findByIdAndTenantId(any(), any())).thenReturn(Optional.of(ticket));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TroubleTicket closed = service.close(ticket.getId().value());

        assertTrue(closed.getStatus() instanceof TroubleTicketStatus.Closed);
        verify(repository).save(ticket);
    }
}
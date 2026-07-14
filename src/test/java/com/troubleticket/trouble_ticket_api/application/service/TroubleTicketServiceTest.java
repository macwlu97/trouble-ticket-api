package com.troubleticket.trouble_ticket_api.application.service;

import com.troubleticket.generated.model.NoteCreateRequest;
import com.troubleticket.generated.model.TroubleTicketCreateRequest;
import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.exception.TroubleTicketNotFoundException;
import com.troubleticket.trouble_ticket_api.domain.model.Note;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicketStatus;
import com.troubleticket.trouble_ticket_api.security.TenantContext; // Using the active context container
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        // FIX: Replaced instance initialization with native static thread execution context binding
        TenantContext.setTenantId(MOCK_TENANT);
    }

    @AfterEach
    void tearDown() {
        // FIX: Replaced obsolete holder reference invocation with standard context tear down sequence
        TenantContext.clear();
    }

    @Test
    void shouldCreateTicket() {

        TroubleTicketCreateRequest request = new TroubleTicketCreateRequest();
        request.setExternalId("EXT-100");
        request.setServiceId(123L);
        request.setDescription("Internet problem");
        request.setNote("Initial note");
        request.setStatus(com.troubleticket.generated.model.TroubleTicketCreateStatus.NEW);

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TroubleTicket ticket = service.create(request);

        assertNotNull(ticket);
        assertEquals(MOCK_TENANT, ticket.getTenantId());
        assertEquals("EXT-100", ticket.getExternalId());
        assertEquals(123L, ticket.getServiceId());
        assertEquals("Internet problem", ticket.getDescription());
        assertEquals(TroubleTicketStatus.NEW, ticket.getStatus());
        assertEquals(1, ticket.getNotes().size());

        verify(repository).save(any(TroubleTicket.class));
    }

    @Test
    void shouldFindTicketById() {

        UUID id = UUID.randomUUID();
        TroubleTicket ticket = new TroubleTicket(
                id,
                MOCK_TENANT,
                "EXT",
                1L,
                "Description"
        );

        when(repository.findByIdAndTenantId(id, MOCK_TENANT))
                .thenReturn(Optional.of(ticket));

        TroubleTicket result = service.getById(id);

        assertEquals(ticket, result);
        verify(repository).findByIdAndTenantId(id, MOCK_TENANT);
    }

    @Test
    void shouldThrowWhenTicketDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(repository.findByIdAndTenantId(id, MOCK_TENANT))
                .thenReturn(Optional.empty());

        assertThrows(
                TroubleTicketNotFoundException.class,
                () -> service.getById(id)
        );
    }

    @Test
    void shouldReturnAllTickets() {

        List<TroubleTicket> tickets = List.of(
                new TroubleTicket(UUID.randomUUID(), MOCK_TENANT, "A", 1L, "One"),
                new TroubleTicket(UUID.randomUUID(), MOCK_TENANT, "B", 2L, "Two")
        );

        when(repository.findAllByTenantId(MOCK_TENANT))
                .thenReturn(tickets);

        List<TroubleTicket> result = service.getAll();

        assertEquals(2, result.size());
        verify(repository).findAllByTenantId(MOCK_TENANT);
    }

    @Test
    void shouldAddNote() {

        UUID id = UUID.randomUUID();
        TroubleTicket ticket = new TroubleTicket(
                id,
                MOCK_TENANT,
                "EXT",
                1L,
                "Description"
        );

        ticket.acknowledge();

        when(repository.findByIdAndTenantId(id, MOCK_TENANT))
                .thenReturn(Optional.of(ticket));

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        NoteCreateRequest request = new NoteCreateRequest();
        request.setText("New note");

        Note note = service.addNote(id, request);

        assertEquals("New note", note.getText());
        assertEquals(1, ticket.getNotes().size());
        verify(repository).save(ticket);
    }

    @Test
    void shouldCloseTicket() {

        UUID id = UUID.randomUUID();
        TroubleTicket ticket = new TroubleTicket(
                id,
                MOCK_TENANT,
                "EXT",
                1L,
                "Description"
        );

        ticket.acknowledge();

        when(repository.findByIdAndTenantId(id, MOCK_TENANT))
                .thenReturn(Optional.of(ticket));

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TroubleTicket closed = service.close(id);

        assertEquals(TroubleTicketStatus.CLOSED, closed.getStatus());
        verify(repository).save(ticket);
    }

    @Test
    void shouldSaveModifiedAggregate() {

        TroubleTicketCreateRequest request = new TroubleTicketCreateRequest();
        request.setExternalId("ABC");
        request.setServiceId(999L);
        request.setDescription("Problem");
        request.setStatus(com.troubleticket.generated.model.TroubleTicketCreateStatus.NEW);

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.create(request);

        ArgumentCaptor<TroubleTicket> captor = ArgumentCaptor.forClass(TroubleTicket.class);
        verify(repository).save(captor.capture());

        TroubleTicket saved = captor.getValue();

        assertEquals(MOCK_TENANT, saved.getTenantId());
        assertEquals("ABC", saved.getExternalId());
        assertEquals(999L, saved.getServiceId());
    }
}

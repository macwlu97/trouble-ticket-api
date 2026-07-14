package com.troubleticket.trouble_ticket_api.application.service;

import com.troubleticket.generated.model.NoteCreateRequest;
import com.troubleticket.generated.model.TroubleTicketCreateRequest;
import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.exception.TroubleTicketNotFoundException;
import com.troubleticket.trouble_ticket_api.domain.model.Note;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicketStatus;
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

    private TroubleTicketRepository repository;

    private TroubleTicketService service;

    @BeforeEach
    void setUp() {
        repository = mock(TroubleTicketRepository.class);
        service = new TroubleTicketService(repository);
    }

    @Test
    void shouldCreateTicket() {

        TroubleTicketCreateRequest request = new TroubleTicketCreateRequest();

        request.setExternalId("EXT-100");
        request.setServiceId(123L);
        request.setDescription("Internet problem");
        request.setNote("Initial note");

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TroubleTicket ticket = service.create(request);

        assertNotNull(ticket);

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
                "EXT",
                1L,
                "Description"
        );

        when(repository.findById(id))
                .thenReturn(Optional.of(ticket));

        TroubleTicket result = service.getById(id);

        assertEquals(ticket, result);

        verify(repository).findById(id);
    }

    @Test
    void shouldThrowWhenTicketDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                TroubleTicketNotFoundException.class,
                () -> service.getById(id)
        );
    }

    @Test
    void shouldReturnAllTickets() {

        List<TroubleTicket> tickets = List.of(
                new TroubleTicket(
                        UUID.randomUUID(),
                        "A",
                        1L,
                        "One"
                ),
                new TroubleTicket(
                        UUID.randomUUID(),
                        "B",
                        2L,
                        "Two"
                )
        );

        when(repository.findAll())
                .thenReturn(tickets);

        List<TroubleTicket> result = service.getAll();

        assertEquals(2, result.size());

        verify(repository).findAll();
    }

    @Test
    void shouldAddNote() {

        UUID id = UUID.randomUUID();

        TroubleTicket ticket = new TroubleTicket(
                id,
                "EXT",
                1L,
                "Description"
        );

        when(repository.findById(id))
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
                "EXT",
                1L,
                "Description"
        );

        when(repository.findById(id))
                .thenReturn(Optional.of(ticket));

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TroubleTicket closed = service.close(id);

        assertEquals(
                TroubleTicketStatus.CLOSED,
                closed.getStatus()
        );

        verify(repository).save(ticket);
    }

    @Test
    void shouldSaveModifiedAggregate() {

        TroubleTicketCreateRequest request =
                new TroubleTicketCreateRequest();

        request.setExternalId("ABC");
        request.setServiceId(999L);
        request.setDescription("Problem");

        when(repository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.create(request);

        ArgumentCaptor<TroubleTicket> captor =
                ArgumentCaptor.forClass(TroubleTicket.class);

        verify(repository).save(captor.capture());

        TroubleTicket saved = captor.getValue();

        assertEquals("ABC", saved.getExternalId());

        assertEquals(999L, saved.getServiceId());
    }

}
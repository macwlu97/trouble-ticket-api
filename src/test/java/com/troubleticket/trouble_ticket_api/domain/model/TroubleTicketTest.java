package com.troubleticket.trouble_ticket_api.domain.model;

import com.troubleticket.trouble_ticket_api.domain.exception.InvalidStatusTransitionException;
import com.troubleticket.trouble_ticket_api.domain.model.value.*;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TroubleTicketTest {

    private static final String MOCK_TENANT = "tenant-demo";

    private TroubleTicket createTestTicket(String extId) {
        return new TroubleTicket(
                new TroubleTicketId("TT-2026-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()),
                new TenantId(MOCK_TENANT),
                new ExternalId(extId),
                new ServiceId(10L),
                "Description",
                new TroubleTicketStatus.New(),
                OffsetDateTime.now(),
                new ArrayList<>()
        );
    }

    @Test
    void shouldCreateTicketWithNewStatus() {
        TroubleTicket ticket = createTestTicket("EXT-1");

        assertTrue(ticket.getStatus() instanceof TroubleTicketStatus.New);
        assertEquals(MOCK_TENANT, ticket.getTenantId().value());
        assertEquals("EXT-1", ticket.getExternalId().value());
        assertEquals(10L, ticket.getServiceId().value());
        assertEquals("Description", ticket.getDescription());
        assertNotNull(ticket.getCreatedAt());
        assertTrue(ticket.getNotes().isEmpty());
    }

    @Test
    void shouldAddNote() {
        TroubleTicket ticket = createTestTicket("EXT-2");

        Note note = ticket.addNote("First note");

        assertEquals(1, ticket.getNotes().size());
        assertEquals(note, ticket.getNotes().get(0));
        assertEquals("First note", note.text()); // Zakładając, że Note to record
        assertNotNull(note.id());
        assertNotNull(note.createdAt());
    }

    @Test
    void shouldAcknowledgeTicket() {
        TroubleTicket ticket = createTestTicket("EXT-3");
        ticket.acknowledge();

        assertTrue(ticket.getStatus() instanceof TroubleTicketStatus.Acknowledged);
    }

    @Test
    void shouldResolveTicket() {
        TroubleTicket ticket = createTestTicket("EXT-4");
        ticket.resolve();

        assertTrue(ticket.getStatus() instanceof TroubleTicketStatus.Resolved);
    }

    @Test
    void shouldCloseTicket() {
        TroubleTicket ticket = createTestTicket("EXT-5");
        ticket.acknowledge();
        ticket.close();

        assertTrue(ticket.getStatus() instanceof TroubleTicketStatus.Closed);
    }

    @Test
    void closingClosedTicketShouldBeIdempotent() {
        TroubleTicket ticket = createTestTicket("EXT-6");
        ticket.acknowledge();
        ticket.close();
        ticket.close();

        assertTrue(ticket.getStatus() instanceof TroubleTicketStatus.Closed);
    }

//    @Test
//    void shouldThrowExceptionWhenTransitioningDirectlyFromNewToClosed() {
//        TroubleTicket ticket = createTestTicket("EXT-99");
//
//        assertThrows(InvalidStatusTransitionException.class, ticket::close);
//    }

    @Test
    void notesCollectionShouldBeImmutable() {
        TroubleTicket ticket = createTestTicket("EXT-7");

        assertThrows(UnsupportedOperationException.class, () ->
                ticket.getNotes().add(new Note(new NoteId(UUID.randomUUID()), "Hack", OffsetDateTime.now()))
        );
    }
}
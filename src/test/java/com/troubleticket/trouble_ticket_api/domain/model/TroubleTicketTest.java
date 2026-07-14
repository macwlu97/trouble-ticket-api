package com.troubleticket.trouble_ticket_api.domain.model;

import com.troubleticket.trouble_ticket_api.domain.exception.InvalidStatusTransitionException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TroubleTicketTest {

    private static final String MOCK_TENANT = "tenant-demo";

    @Test
    void shouldCreateTicketWithNewStatus() {

        // FIX: Injected MOCK_TENANT as the second argument to match the updated 5-parameter model creation constructor
        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                MOCK_TENANT,
                "EXT-1",
                100L,
                "Internet not working"
        );

        assertEquals(TroubleTicketStatus.NEW, ticket.getStatus());
        assertEquals(MOCK_TENANT, ticket.getTenantId());
        assertEquals("EXT-1", ticket.getExternalId());
        assertEquals(100L, ticket.getServiceId());
        assertEquals("Internet not working", ticket.getDescription());

        assertNotNull(ticket.getCreatedAt());
        assertTrue(ticket.getNotes().isEmpty());
    }

    @Test
    void shouldAddNote() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                MOCK_TENANT,
                "EXT-2",
                101L,
                "Description"
        );

        Note note = ticket.addNote("First note");

        assertEquals(1, ticket.getNotes().size());
        assertEquals(note, ticket.getNotes().getFirst());
        assertEquals("First note", note.getText());
        assertNotNull(note.getId());
        assertNotNull(note.getCreatedAt());
    }

    @Test
    void shouldAcknowledgeTicket() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                MOCK_TENANT,
                "EXT-3",
                1L,
                "Description"
        );

        ticket.acknowledge();

        assertEquals(TroubleTicketStatus.ACKNOWLEDGED, ticket.getStatus());
    }

    @Test
    void shouldResolveTicket() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                MOCK_TENANT,
                "EXT-4",
                1L,
                "Description"
        );

        ticket.resolve();

        assertEquals(TroubleTicketStatus.RESOLVED, ticket.getStatus());
    }

    @Test
    void shouldCloseTicket() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                MOCK_TENANT,
                "EXT-5",
                1L,
                "Description"
        );

        // FIX: Moved aggregate away from NEW to satisfy state machine transition rules before closing
        ticket.acknowledge();
        ticket.close();

        assertEquals(TroubleTicketStatus.CLOSED, ticket.getStatus());
    }

    @Test
    void closingClosedTicketShouldBeIdempotent() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                MOCK_TENANT,
                "EXT-6",
                1L,
                "Description"
        );

        // FIX: Progressed state out of NEW before driving multi-step closure idempotency assertions
        ticket.acknowledge();
        ticket.close();
        ticket.close();

        assertEquals(TroubleTicketStatus.CLOSED, ticket.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenTransitioningDirectlyFromNewToClosed() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                MOCK_TENANT,
                "EXT-99",
                1L,
                "Description"
        );

        // NEW: Validates that state machine rules actively reject illegal direct closures with an explicit contract exception
        assertThrows(
                InvalidStatusTransitionException.class,
                ticket::close
        );
    }

    @Test
    void notesCollectionShouldBeImmutable() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                MOCK_TENANT,
                "EXT-7",
                1L,
                "Description"
        );

        assertThrows(
                UnsupportedOperationException.class,
                () -> ticket.getNotes().add(
                        new Note(
                                UUID.randomUUID(),
                                "Hack",
                                java.time.OffsetDateTime.now()
                        )
                )
        );
    }
}

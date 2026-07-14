package com.troubleticket.trouble_ticket_api.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TroubleTicketTest {

    @Test
    void shouldCreateTicketWithNewStatus() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                "EXT-1",
                100L,
                "Internet not working"
        );

        assertEquals(TroubleTicketStatus.NEW, ticket.getStatus());
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
                "EXT-3",
                1L,
                "Description"
        );

        ticket.acknowledge();

        assertEquals(
                TroubleTicketStatus.ACKNOWLEDGED,
                ticket.getStatus()
        );
    }

    @Test
    void shouldResolveTicket() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                "EXT-4",
                1L,
                "Description"
        );

        ticket.resolve();

        assertEquals(
                TroubleTicketStatus.RESOLVED,
                ticket.getStatus()
        );
    }

    @Test
    void shouldCloseTicket() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                "EXT-5",
                1L,
                "Description"
        );

        ticket.close();

        assertEquals(
                TroubleTicketStatus.CLOSED,
                ticket.getStatus()
        );
    }

    @Test
    void closingClosedTicketShouldBeIdempotent() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                "EXT-6",
                1L,
                "Description"
        );

        ticket.close();
        ticket.close();

        assertEquals(
                TroubleTicketStatus.CLOSED,
                ticket.getStatus()
        );
    }

    @Test
    void notesCollectionShouldBeImmutable() {

        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
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
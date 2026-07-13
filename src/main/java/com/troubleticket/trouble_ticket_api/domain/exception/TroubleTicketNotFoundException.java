package com.troubleticket.trouble_ticket_api.domain.exception;

import java.util.UUID;

public class TroubleTicketNotFoundException extends RuntimeException {

    public TroubleTicketNotFoundException(UUID id) {
        super("Trouble Ticket not found: " + id);
    }
}
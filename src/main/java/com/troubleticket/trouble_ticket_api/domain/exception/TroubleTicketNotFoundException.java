package com.troubleticket.trouble_ticket_api.domain.exception;

public class TroubleTicketNotFoundException extends RuntimeException {

    public TroubleTicketNotFoundException(String id) {
        super("Trouble Ticket not found: " + id);
    }
}
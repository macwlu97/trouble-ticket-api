package com.troubleticket.trouble_ticket_api.domain.model;

import com.troubleticket.trouble_ticket_api.domain.exception.InvalidStatusTransitionException;

public sealed interface TroubleTicketStatus permits
        TroubleTicketStatus.New,
        TroubleTicketStatus.Acknowledged,
        TroubleTicketStatus.InProgress,
        TroubleTicketStatus.Resolved,
        TroubleTicketStatus.Closed,
        TroubleTicketStatus.Rejected {

    String value();

    default TroubleTicketStatus close() {
        throw new InvalidStatusTransitionException("Cannot close ticket from status: " + value());
    }

    record New() implements TroubleTicketStatus {
        @Override public String value() { return "new"; }
    }
    record Acknowledged() implements TroubleTicketStatus {
        @Override public String value() { return "acknowledged"; }
        @Override public TroubleTicketStatus close() { return new Closed(); }
    }
    record InProgress() implements TroubleTicketStatus {
        @Override public String value() { return "inProgress"; }
        @Override public TroubleTicketStatus close() { return new Closed(); }
    }
    record Resolved() implements TroubleTicketStatus {
        @Override public String value() { return "resolved"; }
        @Override public TroubleTicketStatus close() { return new Closed(); }
    }
    record Closed() implements TroubleTicketStatus {
        @Override public String value() { return "closed"; }

        @Override
        public TroubleTicketStatus close() {
            return this; // Idempotency
        }
    }
    record Rejected() implements TroubleTicketStatus {
        @Override public String value() { return "rejected"; }

        @Override
        public TroubleTicketStatus close() {
            return this;
        }
    }

    static TroubleTicketStatus fromString(String status) {
        return switch (status.toLowerCase()) {
            case "new" -> new New();
            case "acknowledged" -> new Acknowledged();
            case "inprogress" -> new InProgress();
            case "resolved" -> new Resolved();
            case "closed" -> new Closed();
            case "rejected" -> new Rejected();
            default -> throw new IllegalArgumentException("Unknown status: " + status);
        };
    }
}
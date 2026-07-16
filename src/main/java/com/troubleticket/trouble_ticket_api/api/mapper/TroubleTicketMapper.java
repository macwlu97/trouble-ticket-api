package com.troubleticket.trouble_ticket_api.api.mapper;

import com.troubleticket.generated.model.TroubleTicketSummary;
import com.troubleticket.trouble_ticket_api.domain.model.Note;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicketStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TroubleTicketMapper {

    public com.troubleticket.generated.model.TroubleTicket toApi(TroubleTicket domain) {
        var api = new com.troubleticket.generated.model.TroubleTicket();

        // Unwrap raw values from domain Value Objects
        api.setId(domain.getId().value());
        api.setExternalId(domain.getExternalId().value());
        api.setServiceId(domain.getServiceId().value());
        api.setDescription(domain.getDescription());
        api.setStatus(toApiStatus(domain.getStatus()));

        api.setNotes(
                domain.getNotes().stream()
                        .map(this::toApi)
                        .toList()
        );

        return api;
    }

    public com.troubleticket.generated.model.Note toApi(Note note) {
        var api = new com.troubleticket.generated.model.Note();

        // Extract values from the Note domain record
        api.setId(note.id().value().toString());
        api.setText(note.text());
        api.setDate(note.createdAt());

        return api;
    }

    public com.troubleticket.generated.model.TroubleTicketSummary toSummary(TroubleTicket domain) {
        var summary = new com.troubleticket.generated.model.TroubleTicketSummary();

        // Map domain fields to API summary using Value Objects
        summary.setExternalId(domain.getExternalId().value());
        summary.setServiceId(domain.getServiceId().value());
        summary.setDescription(domain.getDescription());
        summary.setStatus(toApiStatus(domain.getStatus()));

        return summary;
    }

    public List<TroubleTicketSummary> toSummaryList(List<TroubleTicket> tickets) {
        return tickets.stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * Maps the polymorphic domain status (sealed interface) to the API-generated Enum.
     * Uses Java 21 pattern matching switch for compile-time exhaustiveness.
     */
    private com.troubleticket.generated.model.TroubleTicketStatus toApiStatus(TroubleTicketStatus status) {
        return switch (status) {
            case TroubleTicketStatus.New n -> com.troubleticket.generated.model.TroubleTicketStatus.NEW;
            case TroubleTicketStatus.Acknowledged a -> com.troubleticket.generated.model.TroubleTicketStatus.ACKNOWLEDGED;
            case TroubleTicketStatus.InProgress i -> com.troubleticket.generated.model.TroubleTicketStatus.IN_PROGRESS;
            case TroubleTicketStatus.Resolved r -> com.troubleticket.generated.model.TroubleTicketStatus.RESOLVED;
            case TroubleTicketStatus.Closed c -> com.troubleticket.generated.model.TroubleTicketStatus.CLOSED;
            case TroubleTicketStatus.Rejected rj -> com.troubleticket.generated.model.TroubleTicketStatus.REJECTED;
        };
    }
}
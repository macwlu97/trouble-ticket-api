package com.troubleticket.trouble_ticket_api.api.mapper;

import com.troubleticket.generated.model.TroubleTicketSummary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TroubleTicketMapper {

    public com.troubleticket.generated.model.TroubleTicket toApi(
            com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket domain
    ) {

        var api = new com.troubleticket.generated.model.TroubleTicket();

        api.setId(domain.getId().toString());

        api.setExternalId(domain.getExternalId());

        api.setServiceId(domain.getServiceId());

        api.setDescription(domain.getDescription());

        api.setStatus(toApiStatus(domain.getStatus()));

        api.setNotes(
                domain.getNotes().stream()
                        .map(this::toApi)
                        .toList()
        );

        return api;
    }

    public com.troubleticket.generated.model.Note toApi(
            com.troubleticket.trouble_ticket_api.domain.model.Note note
    ) {

        var api = new com.troubleticket.generated.model.Note();

        api.setId(note.getId().toString());
        api.setText(note.getText());
        api.setDate(note.getCreatedAt());

        return api;
    }

    public com.troubleticket.generated.model.TroubleTicketSummary toSummary(
            com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket domain
    ) {

        var summary =
                new com.troubleticket.generated.model.TroubleTicketSummary();

        summary.setExternalId(domain.getExternalId());

        summary.setServiceId(domain.getServiceId());

        summary.setDescription(domain.getDescription());

        summary.setStatus(
                com.troubleticket.generated.model.TroubleTicketStatus.valueOf(
                        domain.getStatus().name()
                )
        );

        return summary;
    }

    public List<TroubleTicketSummary> toSummaryList(
            List<com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket> tickets
    ) {

        return tickets.stream()
                .map(this::toSummary)
                .toList();
    }

    private com.troubleticket.generated.model.TroubleTicketStatus toApiStatus(
            com.troubleticket.trouble_ticket_api.domain.model.TroubleTicketStatus status
    ) {
        return com.troubleticket.generated.model.TroubleTicketStatus.valueOf(status.name());
    }
}
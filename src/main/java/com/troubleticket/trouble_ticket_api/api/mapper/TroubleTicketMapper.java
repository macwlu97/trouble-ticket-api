package com.troubleticket.trouble_ticket_api.api.mapper;

import org.springframework.stereotype.Component;

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

        api.setStatus(
                com.troubleticket.generated.model.TroubleTicketStatus.valueOf(
                        domain.getStatus().name()
                )
        );

        api.setNotes(
                domain.getNotes().stream()
                        .map(this::toApi)
                        .toList()
        );

        return api;
    }

    private com.troubleticket.generated.model.Note toApi(
            com.troubleticket.trouble_ticket_api.domain.model.Note note
    ) {

        var api = new com.troubleticket.generated.model.Note();

        api.setId(note.getId().toString());
        api.setText(note.getText());
        api.setDate(note.getCreatedAt());

        return api;
    }
}
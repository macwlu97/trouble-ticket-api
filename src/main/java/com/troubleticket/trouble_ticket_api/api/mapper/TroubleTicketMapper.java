package com.troubleticket.trouble_ticket_api.api.mapper;

import org.springframework.stereotype.Component;

@Component
public class TroubleTicketMapper {

    public com.troubleticket.generated.model.TroubleTicket toApi(
            com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket domain
    ) {

        var api = new com.troubleticket.generated.model.TroubleTicket();

        api.setId(domain.getId().toString());

        api.setDescription(domain.getDescription());

        api.setStatus(
                com.troubleticket.generated.model.TroubleTicketStatus.valueOf(
                        domain.getStatus().name()
                )
        );

        return api;
    }
}
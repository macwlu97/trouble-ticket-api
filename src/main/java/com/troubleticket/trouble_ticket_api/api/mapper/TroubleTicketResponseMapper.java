package com.troubleticket.trouble_ticket_api.api.mapper;

import org.springframework.stereotype.Component;

@Component
public class TroubleTicketResponseMapper {


    public com.troubleticket.generated.model.TroubleTicket toApi(
            com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket domain
    ) {

        var response =
                new com.troubleticket.generated.model.TroubleTicket();

        response.setId(
                domain.getId().toString()
        );

        response.setDescription(
                domain.getDescription()
        );

        response.setStatus(
                mapStatus(domain.getStatus())
        );

        return response;
    }


    private com.troubleticket.generated.model.TroubleTicketStatus mapStatus(
            com.troubleticket.trouble_ticket_api.domain.model.TroubleTicketStatus status
    ) {

        return switch (status) {

            case NEW ->
                    com.troubleticket.generated.model.TroubleTicketStatus.NEW;

            case ACKNOWLEDGED ->
                    com.troubleticket.generated.model.TroubleTicketStatus.ACKNOWLEDGED;

            case IN_PROGRESS ->
                    com.troubleticket.generated.model.TroubleTicketStatus.IN_PROGRESS;

            case RESOLVED ->
                    com.troubleticket.generated.model.TroubleTicketStatus.RESOLVED;

            case CLOSED ->
                    com.troubleticket.generated.model.TroubleTicketStatus.CLOSED;

            case REJECTED ->
                    com.troubleticket.generated.model.TroubleTicketStatus.REJECTED;
        };
    }
}
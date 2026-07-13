package com.troubleticket.trouble_ticket_api.api.controller;

import com.troubleticket.trouble_ticket_api.api.mapper.TroubleTicketMapper;
import com.troubleticket.trouble_ticket_api.application.port.in.CreateTroubleTicketUseCase;
import com.troubleticket.generated.api.TroubleTicketApi;
import com.troubleticket.generated.model.TroubleTicket;
import com.troubleticket.generated.model.TroubleTicketCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TroubleTicketController implements TroubleTicketApi {

    private final CreateTroubleTicketUseCase createTroubleTicketUseCase;
    private final TroubleTicketMapper mapper;

    @Override
    public ResponseEntity<TroubleTicket> createTroubleTicket(
            @Valid @RequestBody TroubleTicketCreateRequest troubleTicketCreateRequest
    ) {

        var ticket = createTroubleTicketUseCase.create(troubleTicketCreateRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(mapper.toApi(ticket));
    }
}

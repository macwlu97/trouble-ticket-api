package com.troubleticket.trouble_ticket_api.api.controller;

import com.troubleticket.generated.api.TroubleTicketApi;
import com.troubleticket.generated.model.*;
import com.troubleticket.trouble_ticket_api.api.mapper.TroubleTicketMapper;
import com.troubleticket.trouble_ticket_api.application.port.in.CloseTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.CreateTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.GetTroubleTicketUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TroubleTicketController implements TroubleTicketApi {

    private final CreateTroubleTicketUseCase createTroubleTicketUseCase;
    private final GetTroubleTicketUseCase getTroubleTicketUseCase;
    private final CloseTroubleTicketUseCase closeTroubleTicketUseCase;
    private final TroubleTicketMapper mapper;

    @Override
    public ResponseEntity<TroubleTicket> createTroubleTicket(
            @Valid @RequestBody TroubleTicketCreateRequest request
    ) {
        var ticket = createTroubleTicketUseCase.create(request);
        var apiTicket = mapper.toApi(ticket);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/troubleTicket/" + ticket.getId())
                .body(apiTicket);
    }

    @Override
    public ResponseEntity<TroubleTicket> getTroubleTicketById(String id) {
        return ResponseEntity.ok(
                mapper.toApi(
                        getTroubleTicketUseCase.getById(id)
                )
        );
    }

    @Override
    public ResponseEntity<List<TroubleTicketSummary>> listTroubleTickets() {
        return ResponseEntity.ok(
                mapper.toSummaryList(
                        getTroubleTicketUseCase.getAll()
                )
        );
    }

    @Override
    public ResponseEntity<TroubleTicket> closeTroubleTicket(
            String id,
            TroubleTicketCloseStatusRequest request
    ) {
        var ticket = closeTroubleTicketUseCase.close(id);

        return ResponseEntity.ok(
                mapper.toApi(ticket)
        );
    }
}
package com.troubleticket.trouble_ticket_api.application.service;

import com.troubleticket.generated.model.NoteCreateRequest;
import com.troubleticket.generated.model.TroubleTicketCreateRequest;
import com.troubleticket.trouble_ticket_api.application.port.in.AddNoteUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.CloseTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.CreateTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.GetTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.exception.TroubleTicketNotFoundException;
import com.troubleticket.trouble_ticket_api.domain.exception.ServiceNotFoundException;
import com.troubleticket.trouble_ticket_api.domain.model.Note;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import com.troubleticket.trouble_ticket_api.domain.model.value.*;
import com.troubleticket.trouble_ticket_api.security.TenantContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class TroubleTicketService implements
        CreateTroubleTicketUseCase,
        GetTroubleTicketUseCase,
        CloseTroubleTicketUseCase,
        AddNoteUseCase {

    private final TroubleTicketRepository repository;

    public TroubleTicketService(TroubleTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public TroubleTicket create(TroubleTicketCreateRequest request) {

        TenantId tenantId = new TenantId(getAuthenticatedTenantId());

        if (request.getServiceId() == null || request.getServiceId() <= 0) {
            throw new ServiceNotFoundException(
                    request.getServiceId() != null ? request.getServiceId() : 0L
            );
        }
        ServiceId serviceId = new ServiceId(request.getServiceId());
        ExternalId externalId = new ExternalId(request.getExternalId());

        Optional<TroubleTicket> existing =
                repository.findByTenantIdAndExternalId(tenantId, externalId);

        if (existing.isPresent()) {
            return existing.get();
        }

        TroubleTicket ticket = TroubleTicket.createNew(
                tenantId,
                externalId,
                serviceId,
                request.getDescription(),
                request.getNote()
        );

        return repository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public TroubleTicket getById(String id) {
        String authTenant = getAuthenticatedTenantId();

        System.out.println("DEBUG: Szukam ID: [" + id + "], Tenant: [" + authTenant + "]");

        TroubleTicketId ticketId = new TroubleTicketId(id);
        TenantId tenantId = new TenantId(authTenant);

        var result = repository.findByIdAndTenantId(ticketId, tenantId);

        if (result.isEmpty()) {
            System.out.println("DEBUG: BAZA ZWRÓCIŁA PUSTY WYNIK!");
        }

        return result.orElseThrow(() -> new TroubleTicketNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TroubleTicket> getAll() {
        TenantId tenantId = new TenantId(getAuthenticatedTenantId());
        return repository.findAllByTenantId(tenantId);
    }

    @Override
    @Transactional
    public Note addNote(String id, NoteCreateRequest request) {
        TenantId tenantId = new TenantId(getAuthenticatedTenantId());
        TroubleTicketId ticketId = new TroubleTicketId(id);

        TroubleTicket ticket = repository.findByIdAndTenantId(ticketId, tenantId)
                .orElseThrow(() -> new TroubleTicketNotFoundException(id));

        Note note = ticket.addNote(request.getText());

        repository.save(ticket);
        return note;
    }

    @Override
    @Transactional
    public TroubleTicket close(String id) {
        TenantId tenantId = new TenantId(getAuthenticatedTenantId());
        TroubleTicketId ticketId = new TroubleTicketId(id);

        TroubleTicket ticket = repository.findByIdAndTenantId(ticketId, tenantId)
                .orElseThrow(() -> new TroubleTicketNotFoundException(id));

        ticket.close();

        return repository.save(ticket);
    }

    private String getAuthenticatedTenantId() {
        return TenantContext.getTenantId();
    }
}
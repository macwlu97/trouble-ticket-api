package com.troubleticket.trouble_ticket_api.application.service;

import com.troubleticket.generated.model.NoteCreateRequest;
import com.troubleticket.generated.model.TroubleTicketCreateRequest;
import com.troubleticket.trouble_ticket_api.application.port.in.AddNoteUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.CloseTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.CreateTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.in.GetTroubleTicketUseCase;
import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.exception.TroubleTicketNotFoundException;
import com.troubleticket.trouble_ticket_api.domain.model.Note;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import com.troubleticket.trouble_ticket_api.security.TenantContext; // FIX: Updated import to use the Phase 9 context container
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        String tenantId = getAuthenticatedTenantId();

        // Business idempotency scoped strictly to the current authenticated tenant
        Optional<TroubleTicket> existing =
                repository.findByTenantIdAndExternalId(tenantId, request.getExternalId());

        if (existing.isPresent()) {
            return existing.get();
        }

        // Aggregate instantiation with explicit tenant isolation anchoring
        TroubleTicket ticket = new TroubleTicket(
                UUID.randomUUID(),
                tenantId,
                request.getExternalId(),
                request.getServiceId(),
                request.getDescription()
        );

        if (request.getNote() != null && !request.getNote().isBlank()) {
            ticket.addNote(request.getNote());
        }

        return repository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public TroubleTicket getById(UUID id) {
        String tenantId = getAuthenticatedTenantId();

        // Prevents cross-tenant data leaks by returning 404 instead of exposing resource existence
        return repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new TroubleTicketNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TroubleTicket> getAll() {
        String tenantId = getAuthenticatedTenantId();

        // Enforces dataset containment limited exclusively to the authenticated tenant
        return repository.findAllByTenantId(tenantId);
    }

    @Override
    @Transactional
    public Note addNote(UUID ticketId, NoteCreateRequest request) {
        String tenantId = getAuthenticatedTenantId();

        // Subresource mutation guarded by multi-tenant access verification rules
        TroubleTicket ticket = repository.findByIdAndTenantId(ticketId, tenantId)
                .orElseThrow(() -> new TroubleTicketNotFoundException(ticketId));

        ticket.addNote(request.getText());

        repository.save(ticket);

        List<Note> notes = ticket.getNotes();
        return notes.get(notes.size() - 1);
    }

    @Override
    @Transactional
    public TroubleTicket close(UUID id) {
        String tenantId = getAuthenticatedTenantId();

        // Ticket state mutation secured under tenant domain context isolation boundaries
        TroubleTicket ticket = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new TroubleTicketNotFoundException(id));

        // Domain rule enforcement engine triggers state validation and might throw InvalidStatusTransitionException
        ticket.close();

        return repository.save(ticket);
    }

    private String getAuthenticatedTenantId() {
        // FIX: Swapped TenantContextHolder.tenantId() with TenantContext.getTenantId() to align with Spring Security configurations
        return TenantContext.getTenantId();
    }
}

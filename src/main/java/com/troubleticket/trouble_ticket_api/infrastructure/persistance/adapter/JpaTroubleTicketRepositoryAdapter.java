package com.troubleticket.trouble_ticket_api.infrastructure.persistance.adapter;

import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.model.Note;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicketStatus;
import com.troubleticket.trouble_ticket_api.domain.model.value.*;
import com.troubleticket.trouble_ticket_api.infrastructure.persistance.entity.NoteEntity;
import com.troubleticket.trouble_ticket_api.infrastructure.persistance.entity.TroubleTicketEntity;
import com.troubleticket.trouble_ticket_api.infrastructure.persistance.repository.SpringDataTroubleTicketRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Primary
@Repository
@Profile({"dev", "prod"})
public class JpaTroubleTicketRepositoryAdapter
        implements TroubleTicketRepository {

    private final SpringDataTroubleTicketRepository repository;

    public JpaTroubleTicketRepositoryAdapter(SpringDataTroubleTicketRepository repository) {
        this.repository = repository;
    }

    @Override
    public TroubleTicket save(TroubleTicket ticket) {
        // Fetch existing entity to safely merge changes and avoid duplicate key / detached entity issues
        Optional<TroubleTicketEntity> existingEntityOpt = repository.findById(ticket.getId().value());

        TroubleTicketEntity entityToSave;
        if (existingEntityOpt.isPresent()) {
            entityToSave = existingEntityOpt.get();
            updateEntityProperties(entityToSave, ticket);
            syncNotes(entityToSave, ticket.getNotes());
        } else {
            entityToSave = createNewEntity(ticket);
        }

        TroubleTicketEntity saved = repository.save(entityToSave);
        return toDomain(saved);
    }

    @Override
    public Optional<TroubleTicket> findByIdAndTenantId(TroubleTicketId id, TenantId tenantId) {
        return repository.findByIdAndTenantId(id.value(), tenantId.value())
                .map(this::toDomain);
    }

    @Override
    public Optional<TroubleTicket> findByTenantIdAndExternalId(TenantId tenantId, ExternalId externalId) {
        return repository.findByTenantIdAndExternalId(tenantId.value(), externalId.value())
                .map(this::toDomain);
    }

    @Override
    public List<TroubleTicket> findAllByTenantId(TenantId tenantId) {
        return repository.findAllByTenantId(tenantId.value())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private TroubleTicketEntity createNewEntity(TroubleTicket ticket) {
        TroubleTicketEntity entity = new TroubleTicketEntity(
                ticket.getId().value(),
                ticket.getTenantId().value(),
                ticket.getExternalId().value(),
                ticket.getServiceId().value(),
                ticket.getDescription(),
                ticket.getStatus().value(),
                ticket.getCreatedAt()
        );

        for (Note note : ticket.getNotes()) {
            entity.addNote(toEntity(note));
        }
        return entity;
    }

    private void updateEntityProperties(TroubleTicketEntity entity, TroubleTicket ticket) {
        // Update mutable fields only, keeping the technical references intact
        entity.setStatus(ticket.getStatus().value());
    }

    private void syncNotes(TroubleTicketEntity entity, List<Note> domainNotes) {
        // Find existing database note IDs to prevent adding duplicates
        Set<UUID> existingNoteIds = entity.getNotes().stream()
                .map(NoteEntity::getId)
                .collect(Collectors.toSet());

        // Only add notes that do not already exist in the database entity collection
        for (Note domainNote : domainNotes) {
            if (!existingNoteIds.contains(domainNote.id().value())) {
                entity.addNote(toEntity(domainNote));
            }
        }
    }

    private NoteEntity toEntity(Note note) {
        return new NoteEntity(
                note.id().value(),
                note.text(),
                note.createdAt(),
                null
        );
    }

    private TroubleTicket toDomain(TroubleTicketEntity entity) {
        List<Note> notes = entity.getNotes()
                .stream()
                .map(this::toDomain)
                .toList();

        return new TroubleTicket(
                new TroubleTicketId(entity.getId()),
                new TenantId(entity.getTenantId()),
                new ExternalId(entity.getExternalId()),
                new ServiceId(entity.getServiceId()),
                entity.getDescription(),
                TroubleTicketStatus.fromString(entity.getStatus()),
                entity.getCreatedAt(),
                notes
        );
    }

    private Note toDomain(NoteEntity entity) {
        return new Note(
                new NoteId(entity.getId()),
                entity.getText(),
                entity.getCreatedAt()
        );
    }
}
package com.troubleticket.trouble_ticket_api.infrastructure.persistance.adapter;

import com.troubleticket.trouble_ticket_api.application.port.out.TroubleTicketRepository;
import com.troubleticket.trouble_ticket_api.domain.model.Note;
import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import com.troubleticket.trouble_ticket_api.infrastructure.persistance.entity.NoteEntity;
import com.troubleticket.trouble_ticket_api.infrastructure.persistance.entity.TroubleTicketEntity;
import com.troubleticket.trouble_ticket_api.infrastructure.persistance.repository.SpringDataTroubleTicketRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Repository
@Profile({"dev", "prod"})
public class JpaTroubleTicketRepositoryAdapter
        implements TroubleTicketRepository {

    private final SpringDataTroubleTicketRepository repository;

    public JpaTroubleTicketRepositoryAdapter(
            SpringDataTroubleTicketRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public TroubleTicket save(TroubleTicket ticket) {

        TroubleTicketEntity entity = toEntity(ticket);

        TroubleTicketEntity saved = repository.save(entity);

        return toDomain(saved);
    }

    @Override
    public Optional<TroubleTicket> findById(java.util.UUID id) {

        return repository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public Optional<TroubleTicket> findByExternalId(String externalId) {

        return repository.findByExternalId(externalId)
                .map(this::toDomain);
    }

    @Override
    public List<TroubleTicket> findAll() {

        return repository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private TroubleTicketEntity toEntity(TroubleTicket ticket) {

        TroubleTicketEntity entity = new TroubleTicketEntity(
                ticket.getId(),
                ticket.getExternalId(),
                ticket.getServiceId(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getCreatedAt()
        );

        for (Note note : ticket.getNotes()) {
            entity.addNote(toEntity(note));
        }

        return entity;
    }

    private NoteEntity toEntity(Note note) {

        return new NoteEntity(
                note.getId(),
                note.getText(),
                note.getCreatedAt(),
                null
        );
    }

    private TroubleTicket toDomain(TroubleTicketEntity entity) {

        List<Note> notes = entity.getNotes()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());

        return new TroubleTicket(
                entity.getId(),
                entity.getExternalId(),
                entity.getServiceId(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                notes
        );
    }

    private Note toDomain(NoteEntity entity) {

        return new Note(
                entity.getId(),
                entity.getText(),
                entity.getCreatedAt()
        );
    }
}
package com.troubleticket.trouble_ticket_api.infrastructure.persistance.repository;

import com.troubleticket.trouble_ticket_api.infrastructure.persistance.entity.TroubleTicketEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataTroubleTicketRepository
        extends JpaRepository<TroubleTicketEntity, UUID> {

    @EntityGraph(attributePaths = {"notes"})
    List<TroubleTicketEntity> findAllByTenantId(String tenantId);

    @EntityGraph(attributePaths = {"notes"})
    Optional<TroubleTicketEntity> findByIdAndTenantId(UUID id, String tenantId);

    @EntityGraph(attributePaths = {"notes"})
    Optional<TroubleTicketEntity> findByTenantIdAndExternalId(String tenantId, String externalId);
}

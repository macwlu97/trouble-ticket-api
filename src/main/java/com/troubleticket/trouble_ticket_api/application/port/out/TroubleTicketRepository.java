package com.troubleticket.trouble_ticket_api.application.port.out;

import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TroubleTicketRepository {

    TroubleTicket save(TroubleTicket ticket);

    // Context-aware search to prevent cross-tenant data exposure
    Optional<TroubleTicket> findByIdAndTenantId(UUID id, String tenantId);

    // Business identity uniqueness scoped strictly to a single tenant
    Optional<TroubleTicket> findByTenantIdAndExternalId(String tenantId, String externalId);

    // Dataset confinement limited exclusively to the authenticated tenant
    List<TroubleTicket> findAllByTenantId(String tenantId);
}

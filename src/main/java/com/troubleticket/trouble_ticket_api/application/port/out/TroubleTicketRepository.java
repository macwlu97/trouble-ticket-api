package com.troubleticket.trouble_ticket_api.application.port.out;

import com.troubleticket.trouble_ticket_api.domain.model.TroubleTicket;
import com.troubleticket.trouble_ticket_api.domain.model.value.ExternalId;
import com.troubleticket.trouble_ticket_api.domain.model.value.TenantId;
import com.troubleticket.trouble_ticket_api.domain.model.value.TroubleTicketId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TroubleTicketRepository {

    TroubleTicket save(TroubleTicket ticket);

    // Context-aware search to prevent cross-tenant data exposure
    // FIX: Swapped UUID and String with TroubleTicketId and TenantId value objects
    Optional<TroubleTicket> findByIdAndTenantId(TroubleTicketId id, TenantId tenantId);

    // Business identity uniqueness scoped strictly to a single tenant
    // FIX: Swapped String types with TenantId and ExternalId value objects
    Optional<TroubleTicket> findByTenantIdAndExternalId(TenantId tenantId, ExternalId externalId);

    // Dataset confinement limited exclusively to the authenticated tenant
    // FIX: Swapped String with TenantId value object
    List<TroubleTicket> findAllByTenantId(TenantId tenantId);

}

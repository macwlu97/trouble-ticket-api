CREATE TABLE trouble_ticket
(
    id UUID PRIMARY KEY,

    tenant_id VARCHAR(100) NOT NULL, -- Added for multi-tenancy core isolation

    external_id VARCHAR(255) NOT NULL, -- REMOVED: GLOBAL UNIQUE CONSTRAINT

    service_id BIGINT NOT NULL,

    description TEXT NOT NULL,

    status VARCHAR(50) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    -- Composite unique constraint ensuring externalId is unique ONLY within a specific tenant scope
    CONSTRAINT uk_ticket_tenant_external
        UNIQUE (tenant_id, external_id)
);

CREATE TABLE note
(
    id UUID PRIMARY KEY,

    trouble_ticket_id UUID NOT NULL,

    text TEXT NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_note_ticket
        FOREIGN KEY (trouble_ticket_id)
        REFERENCES trouble_ticket(id)
        ON DELETE CASCADE
);

-- Index for optimized queries filtered by tenantId and externalId combinations
CREATE UNIQUE INDEX idx_ticket_tenant_external
ON trouble_ticket(tenant_id, external_id);

-- Performance index for high-throughput tenant dashboard listings
CREATE INDEX idx_ticket_tenant
ON trouble_ticket(tenant_id);

CREATE INDEX idx_note_ticket
ON note(trouble_ticket_id);

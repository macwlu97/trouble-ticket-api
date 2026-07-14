CREATE TABLE trouble_ticket
(
    id UUID PRIMARY KEY,

    external_id VARCHAR(255) NOT NULL UNIQUE,

    service_id BIGINT NOT NULL,

    description TEXT NOT NULL,

    status VARCHAR(50) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL
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

CREATE INDEX idx_ticket_external_id
ON trouble_ticket(external_id);

CREATE INDEX idx_note_ticket
ON note(trouble_ticket_id);
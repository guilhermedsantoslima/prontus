CREATE TABLE patients (
    id              BIGSERIAL PRIMARY KEY,
    full_name       VARCHAR(150)  NOT NULL,
    birth_date      DATE          NOT NULL,
    comorbidities   TEXT,                          -- pre-existing conditions (comma-separated)
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE triages (
    id              BIGSERIAL PRIMARY KEY,
    patient_id      BIGINT        NOT NULL REFERENCES patients(id),
    symptoms        TEXT          NOT NULL,
    severity        VARCHAR(10)   NOT NULL,        -- RED, ORANGE, YELLOW, GREEN, BLUE
    risk_score      INT           NOT NULL,        -- 1000/500/200/50/10 (Manchester)
    ticket_code     VARCHAR(10)   NOT NULL UNIQUE, -- e.g. "R-001", "Y-014"
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW()
);

CREATE TABLE queue_entries (
    id              BIGSERIAL PRIMARY KEY,
    triage_id       BIGINT        NOT NULL REFERENCES triages(id),
    patient_id      BIGINT        NOT NULL REFERENCES patients(id),
    base_score      INT           NOT NULL,        -- fixed risk_score from triage
    time_weight     NUMERIC(6,3)  NOT NULL,        -- w: acceleration factor per category
    effective_score NUMERIC(10,3) NOT NULL,        -- base_score + (w * elapsed minutes)
    status          VARCHAR(15)   NOT NULL DEFAULT 'WAITING', -- WAITING, CALLED, DONE
    enqueued_at     TIMESTAMP     NOT NULL DEFAULT NOW(),
    called_at       TIMESTAMP
);

CREATE INDEX idx_queue_status_score ON queue_entries(status, effective_score DESC);
CREATE INDEX idx_triages_patient    ON triages(patient_id);
CREATE TABLE ss3_files (
    id          BIGSERIAL   PRIMARY KEY,
    size        BIGINT      NOT NULL,
    extension   VARCHAR(20) NOT NULL,
    created_at  TIMESTAMP   NOT NULL,
    uploaded_at TIMESTAMP,
    updated_at  TIMESTAMP
);

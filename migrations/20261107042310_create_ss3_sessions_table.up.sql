CREATE TABLE ss3_sessions (
    id         BIGSERIAL   PRIMARY KEY,
    file_id    BIGINT      NOT NULL REFERENCES ss3_files(id),
    operation  VARCHAR(8)  NOT NULL CHECK (operation in ('UPLOAD', 'DOWNLOAD')),
    key        VARCHAR(43) NOT NULL,
    is_active  BOOLEAN     NOT NULL DEFAULT TRUE,
    expires_at TIMESTAMP   NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    updated_at TIMESTAMP
);

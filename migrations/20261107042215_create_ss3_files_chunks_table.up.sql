CREATE TABLE ss3_files_chunks (
    id         BIGSERIAL    PRIMARY KEY,
    file_id    BIGINT       NOT NULL REFERENCES ss3_files(id),
    index      INT          NOT NULL,
    size       BIGINT       NOT NULL,
    edek       VARCHAR(255),
    created_at TIMESTAMP    NOT NULL,

    CONSTRAINT unique_file_to_chunk_index UNIQUE (file_id, index)
);

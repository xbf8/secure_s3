package com.xbf8.secure_s3.file_chunk.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xbf8.secure_s3.file_chunk.FileChunkEntity;

public record FileChunkResponse(
    long id,

    @JsonProperty("file_id")
    long fileId,

    long size,
    short index,

    @JsonProperty("created_at")
    Instant createdAt
) {
    public static FileChunkResponse from(FileChunkEntity entity) {
        return new FileChunkResponse(
            entity.getId(),
            entity.getFileId(),
            entity.getSize(),
            entity.getIndex(),
            entity.getCreatedAt()
        );
    }
}

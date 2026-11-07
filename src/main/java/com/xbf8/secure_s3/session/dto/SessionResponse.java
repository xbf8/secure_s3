package com.xbf8.secure_s3.session.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xbf8.secure_s3.session.SessionEntity;
import com.xbf8.secure_s3.session.enums.SessionOperation;

public record SessionResponse(
    long id,

    @JsonProperty("file_id")
    long fileId,

    SessionOperation operation,
    String key,

    @JsonProperty("is_active")
    boolean isActive,

    @JsonProperty("expires_at")
    Instant expiresAt,

    @JsonProperty("created_at")
    Instant createdAt,

    @JsonProperty("updated_at")
    Instant updatedAt
) {
    public static SessionResponse from(SessionEntity entity) {
        return new SessionResponse(
            entity.getId(),
            entity.getFileId(),
            entity.getOperation(),
            entity.getKey(),
            entity.getIsActive(),
            entity.getExpiresAt(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}

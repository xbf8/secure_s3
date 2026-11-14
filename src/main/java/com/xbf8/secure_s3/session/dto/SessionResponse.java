package com.xbf8.secure_s3.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.xbf8.secure_s3.session.enums.SessionOperation;

public record SessionResponse(
    @JsonProperty("file_id")
    long fileId,

    SessionOperation operation,
    String key
) {}

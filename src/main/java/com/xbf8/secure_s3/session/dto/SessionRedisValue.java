package com.xbf8.secure_s3.session.dto;

import com.xbf8.secure_s3.session.enums.SessionOperation;

public record SessionRedisValue(long fileId, SessionOperation operation) {}

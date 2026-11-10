package com.xbf8.secure_s3.openbao.dto;

public record OpenbaoGenerateDataKey(byte[] plaintext, String ciphertext) {}

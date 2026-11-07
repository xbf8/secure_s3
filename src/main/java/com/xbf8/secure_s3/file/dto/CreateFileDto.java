package com.xbf8.secure_s3.file.dto;

import com.xbf8.secure_s3.file.validate.SS3FileExtension;
import com.xbf8.secure_s3.file.validate.SS3FileSize;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateFileDto(
    @NotNull
    @SS3FileSize
    long size,

    @NotBlank
    @SS3FileExtension
    String extension
) {}

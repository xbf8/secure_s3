package com.xbf8.secure_s3;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Validated
@ConfigurationProperties(prefix = "ss3")
public record SS3Properties(
    @NotNull(message = "ss3.session configuration section is missing.")
    @Valid
    Session session,

    @NotNull(message = "ss3.validate configuration section is missing.")
    @Valid
    Validate validate,

    @NotNull(message = "ss3.openbao configuration section is missing.")
    @Valid
    Openbao openbao,

    @NotNull(message = "ss3.rustfs configuration section is missing.")
    @Valid
    Rustfs rustfs
) {
    public record Session(
        @NotNull(message = "ss3.session.ttl-minutes config is missing.")
        @Min(value = 1, message = "ss3.session.ttl-minutes must be at least 1.")
        int ttlMinutes
    ) {}

    public record Validate(
        @NotNull(message = "ss3.validate.file configuration section is missing.")
        @Valid
        File file
    ) {
        public record File(
            @NotNull(message = "ss3.validate.file.min-size-bytes config is missing.")
            @Min(value = 1, message = "ss3.validate.file.min-size-bytes must be at least 1 byte.")
            long minSizeBytes,

            @NotNull(message = "ss3.validate.file.max-size-bytes config is missing.")
            @Min(value = 1, message = "ss3.validate.file.max-size-bytes must be at least 1 byte.")
            long maxSizeBytes,

            @NotBlank(message = "ss3.validate.file.extensions must not be blank.")
            @Size(min = 1, message = "ss3.validate.file.extensions must have at least one extension.")
            String extensions,

            @NotNull(message = "ss3.validate.file.chunk configuration section is missing.")
            @Valid
            Chunk chunk
        ) {
            @AssertTrue(message = "ss3.validate.file.max-size-bytes must be >= ss3.validate.file.min-size-bytes.")
            public boolean isFileSizesValid() {
                return this.maxSizeBytes >= this.minSizeBytes;
            }

            @AssertTrue(message = "ss3.validate.file.extensions must have valid file extensions.")
            public boolean isFileExtensionsValid() {
                for (String s : this.extensions.split(",")) {
                    if (!s.matches("^[a-zA-Z0-9]{1,10}$")){
                        return false;
                    }
                }
                return true;
            }

            public List<String> getExtensionsList() {
                return Arrays.asList(this.extensions.split(","));
            }

            public record Chunk(
                @NotNull(message = "ss3.validate.file.chunk.max-size-bytes config is missing.")
                @Min(value = 1, message = "ss3.validate.file.chunk.max-size-bytes must be at least 1 byte.")
                long maxSizeBytes
            ) {}
        }
    }

    public record Openbao(
        @NotBlank(message = "ss3.openbao.url must not be blank.")
        String url,

        @NotBlank(message = "ss3.openbao.token must not be blank.")
        String token,

        @NotBlank(message = "ss3.openbao.key-name must not be blank.")
        String keyName
    ) {}

    public record Rustfs(
        @NotBlank(message = "ss3.rustfs.url must not be blank.")
        String url,

        @NotBlank(message = "ss3.rustfs.access-key must not be blank.")
        String accessKey,

        @NotBlank(message = "ss3.rustfs.secret-key must not be blank.")
        String secretKey,

        @NotBlank(message = "ss3.rustfs.bucket-name must not be blank.")
        String bucketName,

        @NotBlank(message = "ss3.rustfs.region must not be blank.")
        String region
    ) {}
}

package com.xbf8.secure_s3.session;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import com.xbf8.secure_s3.utils.Hasher;


public class SessionKey {
    /**
     * Generate a Session key.
     *
     * @param id
     *      Any unique ID.
     * @return
     *      Optional.empty if we can not hash the key, otherwise Optional.of(key).
     */
    public static Optional<String> generate(long id) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[64];
        random.nextBytes(salt);
        String saltB64 = Base64.getEncoder().encodeToString(salt);
        Optional<String> key = Hasher.sha256ToBase64URL(
            String.format("%d.%s", id, saltB64)
        );
        return key;
    }
}

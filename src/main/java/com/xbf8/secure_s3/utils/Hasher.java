package com.xbf8.secure_s3.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

public class Hasher {
    /**
     * Hash a string using SHA-256 function and encode the result to Base64URL string.
     *
     * @param value
     *      The string you want to hash.
     * @return
     *      Optional.empty if the SHA-256 function not found, otherwise Optional.of(result).
     */
    public static Optional<String> sha256ToBase64URL(String value) {
        MessageDigest digest;
        try {
            // SAFETY: The MessageDigest.getInstance() throw an error of type
            //          NoSuchAlgorithmException if the algorithm not found in the java runtime.
            digest = MessageDigest.getInstance("SHA-256");
        } catch(NoSuchAlgorithmException _) {
            return Optional.empty();
        }
        byte[] encodedHash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        String result = Base64.getUrlEncoder().withoutPadding().encodeToString(encodedHash);
        return Optional.of(result);
    }

    /**
     * Compare a string with Base64URL hashed result.
     *
     * @param value
     *      The string you want to compare.
     * @param hashed
     *      The string you want to compare with.
     * @return
     *      Optional.empty if the SHA-256 function not found, otherwise Optional.of(equals).
     */
    public static Optional<Boolean> compare(String value, String hashed) {
        Optional<String> hashedValue = sha256ToBase64URL(value);
        if (hashedValue.isEmpty()) {
            return Optional.empty();
        } else if (hashedValue.get().equals(hashed)) {
            return Optional.of(true);
        }
        return Optional.of(false);
    }
}

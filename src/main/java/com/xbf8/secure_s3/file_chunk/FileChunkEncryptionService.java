package com.xbf8.secure_s3.file_chunk;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FileChunkEncryptionService {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final String KEY_TYPE = "AES";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private SecureRandom secureRandom;

    public FileChunkEncryptionService() {
        this.secureRandom = new SecureRandom();
    }

    public long calcCipherLenght(long originLengthByte) {
        return originLengthByte + IV_LENGTH_BYTE + (TAG_LENGTH_BIT/8);
    }

    public InputStream encrypt(InputStream requestInputStream, byte[] dekBytes) throws Exception {
        SecretKeySpec dek = new SecretKeySpec(dekBytes, KEY_TYPE);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        this.secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, dek, gcmParameterSpec);

        CipherInputStream cipherInputStream = new CipherInputStream(requestInputStream, cipher);
        return new SequenceInputStream(new ByteArrayInputStream(iv), cipherInputStream);
    }

    public InputStream decrypt(
        InputStream encryptFileInputStream,
        byte[] dekBytes
    ) throws Exception {
        SecretKeySpec dek = new SecretKeySpec(dekBytes, KEY_TYPE);
        byte[] iv = new byte[IV_LENGTH_BYTE];

        int off = 0;
        while (off < IV_LENGTH_BYTE) {
            int n = encryptFileInputStream.read(iv, off, IV_LENGTH_BYTE - off);
            if (n == -1) {
                throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Stream too short to contain IV."
                );
            }
            off += n;
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, dek, gcmParameterSpec);

        return new CipherInputStream(encryptFileInputStream, cipher);
    }
}

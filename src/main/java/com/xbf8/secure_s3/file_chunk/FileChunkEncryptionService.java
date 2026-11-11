package com.xbf8.secure_s3.file_chunk;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

@Service
public class FileChunkEncryptionService {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
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
        SecretKeySpec dek = new SecretKeySpec(dekBytes, "AES");
        byte[] iv = new byte[IV_LENGTH_BYTE];
        this.secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, dek, gcmParameterSpec);

        CipherInputStream cipherInputStream = new CipherInputStream(requestInputStream, cipher);
        return new SequenceInputStream(new ByteArrayInputStream(iv), cipherInputStream);
    }
}

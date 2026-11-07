package com.xbf8.secure_s3.session;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.xbf8.secure_s3.session.enums.SessionOperation;
import com.xbf8.secure_s3.utils.Hasher;

@Service
public class SessionService {
    @Value("${secure_s3.session.ttl}")
    private Integer sessionTTLMinutes;

    private final SessionRepository sessionRepository;
    public SessionService(final SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public SessionEntity create(long fileId, SessionOperation operation) {
        Optional<String> key = SessionKey.generate(fileId);
        if (key.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<String> hashedKey = Hasher.sha256ToBase64URL(key.get());
        if (hashedKey.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Instant creationTime = Instant.now();
        SessionEntity entity = new SessionEntity();
        entity.setFileId(fileId);
        entity.setOperation(operation);
        entity.setKey(hashedKey.get());
        entity.setActive(true);
        entity.setExpiresAt(creationTime.plus(Duration.ofMinutes(sessionTTLMinutes)));
        entity.setCreatedAt(creationTime);

        try {
            SessionEntity savedEntity = this.sessionRepository.save(entity);
            savedEntity.setKey(key.get());
            return savedEntity;
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

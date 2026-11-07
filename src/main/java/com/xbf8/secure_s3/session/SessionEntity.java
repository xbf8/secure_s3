package com.xbf8.secure_s3.session;

import java.time.Instant;

import com.xbf8.secure_s3.session.enums.SessionOperation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ss3_sessions")
public class SessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "file_id", nullable = false)
    private long fileId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionOperation operation;

    @Column(nullable = false)
    private String key;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public SessionEntity() {}

	public void setFileId(long fileId) { this.fileId = fileId; }
	public void setOperation(SessionOperation operation) { this.operation = operation; }
	public void setKey(String key) { this.key = key; }
	public void setActive(boolean isActive) { this.isActive = isActive; }
	public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
	public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
	public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

	public long getId() { return id; }
	public long getFileId() { return fileId; }
	public SessionOperation getOperation() { return operation; }
	public String getKey() { return key; }
	public boolean getIsActive() { return isActive; }
	public Instant getExpiresAt() { return expiresAt; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}

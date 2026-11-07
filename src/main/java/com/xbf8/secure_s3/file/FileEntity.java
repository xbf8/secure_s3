package com.xbf8.secure_s3.file;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ss3_files")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private String extension;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public FileEntity() {}

	public void setSize(long size) { this.size = size; }
	public void setExtension(String extension) { this.extension = extension; }
	public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
	public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
	public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

	public long getId() { return id; }
	public long getSize() { return size; }
	public String getExtension() { return extension; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUploadedAt() { return uploadedAt; }
	public Instant getUpdatedAt() { return updatedAt; }
}

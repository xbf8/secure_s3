package com.xbf8.secure_s3.file_chunk;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ss3_files_chunks")
public class FileChunkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "file_id", nullable = false)
    private long fileId;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private Short index;

    @Column
    private String edek;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public FileChunkEntity() {}

	public void setFileId(long fileId) { this.fileId = fileId; }
	public void setSize(long size) { this.size = size; }
	public void setIndex(Short index) { this.index = index; }
	public void setEdek(String edek) { this.edek = edek; }
	public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

	public long getId() { return id; }
	public long getFileId() { return fileId; }
	public long getSize() { return size; }
	public Short getIndex() { return index; }
	public String getEdek() { return edek; }
	public Instant getCreatedAt() { return createdAt; }
}

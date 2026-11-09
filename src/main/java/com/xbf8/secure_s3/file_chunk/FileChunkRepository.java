package com.xbf8.secure_s3.file_chunk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileChunkRepository extends JpaRepository<FileChunkEntity, Long> {}

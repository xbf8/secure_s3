package com.xbf8.secure_s3.file_chunk;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

@Service
public class FileChunkService {
    @Value("${secure_s3.file.chunk.max-size}")
    private long chunkMaxSize;

    private final FileChunkRepository fileChunkRepository;
    public FileChunkService(FileChunkRepository fileChunkRepository) {
        this.fileChunkRepository = fileChunkRepository;
    }

    @PostConstruct
    public void validateConfig() {
        if (chunkMaxSize < 1) {
            throw new IllegalStateException("chunkMaxSize must be >= 1");
        }
    }

    private int calcNumOfChunks(long fileSize) {
        return (int) Math.ceil((double)fileSize/chunkMaxSize);
    }

    @Transactional
    public List<FileChunkEntity> create(long fileId, long fileSize) {
        int numOfChunks = calcNumOfChunks(fileSize);
        List<FileChunkEntity> chunks = new ArrayList<>();
        for (short i = 0; i < numOfChunks; i++) {
            FileChunkEntity chunk = new FileChunkEntity();
            chunk.setFileId(fileId);
            chunk.setSize((fileSize > 0 && fileSize - chunkMaxSize < 1) ? fileSize : chunkMaxSize);
            chunk.setIndex(i);
            chunk.setCreatedAt(Instant.now());
            chunks.add(chunk);
            fileSize -= chunkMaxSize;
        }
        return this.fileChunkRepository.saveAll(chunks);
    }
}

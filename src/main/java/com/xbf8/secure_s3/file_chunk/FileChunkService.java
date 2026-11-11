package com.xbf8.secure_s3.file_chunk;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    public FileChunkEntity getOne(long id, long fileId) {
        Optional<FileChunkEntity> entity = this.fileChunkRepository.findById(id);
        if (entity.isEmpty() || entity.get().getFileId() != fileId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return entity.get();
    }

    public void saveEDEK(FileChunkEntity entity, String edek) {
        entity.setEdek(edek);
        this.fileChunkRepository.save(entity);
    }
}

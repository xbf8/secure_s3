package com.xbf8.secure_s3.file;

import java.time.Instant;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.xbf8.secure_s3.file.dto.CreateFileDto;

@Service
public class FileService {
    private final FileRepository fileRepository;
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FileEntity create(CreateFileDto dto) {
        FileEntity entity = new FileEntity();
        entity.setSize(dto.size());
        entity.setExtension(dto.extension());
        entity.setCreatedAt(Instant.now());

        try {
            return this.fileRepository.save(entity);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public FileEntity getOne(long id) {
        Optional<FileEntity> entity = this.fileRepository.findById(id);
        if (entity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return entity.get();
    }
}

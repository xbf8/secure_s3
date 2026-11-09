package com.xbf8.secure_s3.file;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xbf8.secure_s3.file_chunk.FileChunkEntity;
import com.xbf8.secure_s3.file_chunk.FileChunkService;
import com.xbf8.secure_s3.file_chunk.dto.FileChunkResponse;

@RestController
@RequestMapping(path = "/files")
public class FileController {
    private final FileService fileService;
    private final FileChunkService fileChunkService;
    public FileController(FileService fileService, FileChunkService fileChunkService) {
        this.fileService = fileService;
        this.fileChunkService = fileChunkService;
    }

    @PostMapping(path = "/{id}/chunks")
    public ResponseEntity<List<FileChunkResponse>> createChunks(@PathVariable long id) {
        FileEntity file = this.fileService.getOne(id);
        List<FileChunkEntity> chunks = this.fileChunkService.create(file.getId(), file.getSize());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(chunks.stream().map(c -> FileChunkResponse.from(c)).toList());
    }
}

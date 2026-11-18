package com.xbf8.secure_s3.session;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xbf8.secure_s3.file.FileEntity;
import com.xbf8.secure_s3.file.FileService;
import com.xbf8.secure_s3.file.dto.CreateFileDto;
import com.xbf8.secure_s3.file_chunk.FileChunkService;
import com.xbf8.secure_s3.session.dto.SessionResponse;
import com.xbf8.secure_s3.session.enums.SessionOperation;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/sessions", version = "v1.0")
public class SessionController {
    private final SessionService sessionService;
    private final FileService fileService;
    private final FileChunkService fileChunkService;
    public SessionController(
        SessionService sessionService,
        FileService fileService,
        FileChunkService fileChunkService
    ) {
        this.sessionService = sessionService;
        this.fileService = fileService;
        this.fileChunkService = fileChunkService;
    }

    @PostMapping(path = "/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessionResponse> createUpload(
        @Valid @RequestBody CreateFileDto dto
    ) {
        FileEntity file = this.fileService.create(dto);
        this.fileChunkService.create(file.getId(), file.getSize());
        SessionResponse body = this.sessionService.create(file.getId(), SessionOperation.UPLOAD);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(body);
    }

    @PostMapping(path = "/upload/{fid}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SessionResponse> createReupload(
        @PathVariable long fid
    ) {
        this.fileService.getOne(fid);
        SessionResponse body = this.sessionService.create(fid, SessionOperation.UPLOAD);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(body);
    }

    @PostMapping(path = "/download/{fid}")
    public ResponseEntity<SessionResponse> createDownload(
        @PathVariable long fid
    ) {
        this.fileService.getOne(fid);
        SessionResponse body = this.sessionService.create(fid, SessionOperation.DOWNLOAD);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(body);
    }
}

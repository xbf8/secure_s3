package com.xbf8.secure_s3.file;

import java.io.InputStream;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xbf8.secure_s3.file_chunk.FileChunkEncryptionService;
import com.xbf8.secure_s3.file_chunk.FileChunkEntity;
import com.xbf8.secure_s3.file_chunk.FileChunkService;
import com.xbf8.secure_s3.file_chunk.dto.FileChunkResponse;
import com.xbf8.secure_s3.openbao.OpenbaoService;
import com.xbf8.secure_s3.openbao.dto.OpenbaoGenerateDataKey;

import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/files")
public class FileController {
    private final FileService fileService;
    private final FileChunkService fileChunkService;
    private final OpenbaoService openbaoService;
    private final FileChunkEncryptionService fileChunkEncryptionService;
    public FileController(
        FileService fileService,
        FileChunkService fileChunkService,
        OpenbaoService openbaoService,
        FileChunkEncryptionService fileChunkEncryptionService
    ) {
        this.fileService = fileService;
        this.fileChunkService = fileChunkService;
        this.openbaoService = openbaoService;
        this.fileChunkEncryptionService = fileChunkEncryptionService;
    }

    @PostMapping(path = "/{id}/chunks")
    public ResponseEntity<List<FileChunkResponse>> createChunks(@PathVariable long id) {
        FileEntity file = this.fileService.getOne(id);
        List<FileChunkEntity> chunks = this.fileChunkService.create(file.getId(), file.getSize());
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(chunks.stream().map(c -> FileChunkResponse.from(c)).toList());
    }

    @PutMapping(path = "/{id}/chunks/{cid}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> uploadChunk(
        @PathVariable long id,
        @PathVariable long cid,
        HttpServletRequest request
    ) {
        // FileChunkEntity chunk = this.fileChunkService.getOne(cid, id);
        // 1. Check the size of the stream.
        try (InputStream requestInputStream = request.getInputStream()) {
            // 2. Create the DEK.
            OpenbaoGenerateDataKey key = this.openbaoService.generateDataKey();

            // 3. Encrypt the chunk.
            InputStream encryptInputStream = this.fileChunkEncryptionService
                .encrypt(requestInputStream, key.plaintext());

            // 4. Save the encrypted chunk in the rustfs.
        } catch (Exception e) {

        }
        // 5. Save the EDEK to chunk entity.
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}

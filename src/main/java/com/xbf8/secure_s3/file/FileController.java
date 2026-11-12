package com.xbf8.secure_s3.file;

import java.io.InputStream;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.xbf8.secure_s3.file_chunk.FileChunkEncryptionService;
import com.xbf8.secure_s3.file_chunk.FileChunkEntity;
import com.xbf8.secure_s3.file_chunk.FileChunkService;
import com.xbf8.secure_s3.file_chunk.dto.FileChunkResponse;
import com.xbf8.secure_s3.openbao.OpenbaoService;
import com.xbf8.secure_s3.openbao.dto.OpenbaoGenerateDataKey;
import com.xbf8.secure_s3.s3.S3Service;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/files")
public class FileController {
    private final FileService fileService;
    private final FileChunkService fileChunkService;
    private final OpenbaoService openbaoService;
    private final FileChunkEncryptionService fileChunkEncryptionService;
    private final S3Service s3Service;
    public FileController(
        FileService fileService,
        FileChunkService fileChunkService,
        OpenbaoService openbaoService,
        FileChunkEncryptionService fileChunkEncryptionService,
        S3Service s3Service
    ) {
        this.fileService = fileService;
        this.fileChunkService = fileChunkService;
        this.openbaoService = openbaoService;
        this.fileChunkEncryptionService = fileChunkEncryptionService;
        this.s3Service = s3Service;
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
        FileChunkEntity chunk = this.fileChunkService.getOne(cid, id);
        if (chunk.getSize() != request.getContentLengthLong() * 8) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        try (InputStream requestInputStream = request.getInputStream()) {
            OpenbaoGenerateDataKey key = this.openbaoService.generateDataKey();

            InputStream encryptInputStream = this.fileChunkEncryptionService
                .encrypt(requestInputStream, key.plaintext());

            long CipherLength = this.fileChunkEncryptionService.calcCipherLenght(
                request.getContentLengthLong()
            );

            // TODO: We can add a project/user id base file name, for exampe the main system might
            //       have the user_id, so we can make the file names like this
            //       "<user-id>/<file-id>/<part-index>.enc".
            String objectKey = String.format("%d/%d.enc", chunk.getFileId(), chunk.getIndex());
            this.s3Service.uploadStream(encryptInputStream, CipherLength, objectKey);

            this.fileChunkService.saveEDEK(chunk, key.ciphertext());

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(path = "/{id}/chunks")
    public ResponseEntity<List<FileChunkResponse>> getChunks(
        @PathVariable long id
    ) {
        List<FileChunkEntity> chunks = this.fileChunkService.getAll(id);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(chunks.stream().map(c -> FileChunkResponse.from(c)).toList());
    }

    @GetMapping(path = "/{id}/chunks/{cid}")
    public ResponseEntity<StreamingResponseBody> downloadChunk(
        @PathVariable long id,
        @PathVariable long cid
    ) {
        FileChunkEntity chunk = this.fileChunkService.getOne(cid, id);
        if (chunk.getEdek() == null) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }

        byte[] dek = this.openbaoService.decryptDataKey(chunk.getEdek());

        String objectKey = String.format("%d/%d.enc", chunk.getFileId(), chunk.getIndex());
        StreamingResponseBody body = outputStream -> {
            try (InputStream fileInputStream = this.s3Service.downloadStream(objectKey)) {
                InputStream decryptInputStream = this.fileChunkEncryptionService.decrypt(
                    fileInputStream,
                    dek
                );
                decryptInputStream.transferTo(outputStream);
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        };

        return ResponseEntity
            .status(HttpStatus.OK)
            .header("Chunk-Index", chunk.getIndex().toString())
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(body);
    }
}

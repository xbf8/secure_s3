package com.xbf8.secure_s3.openbao;

import java.util.Base64;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.ParameterizedTypeReference;

import com.xbf8.secure_s3.SS3Properties;
import com.xbf8.secure_s3.openbao.dto.OpenbaoDataKeyRequest;
import com.xbf8.secure_s3.openbao.dto.OpenbaoDataKeyResponse;
import com.xbf8.secure_s3.openbao.dto.OpenbaoDecryptRequest;
import com.xbf8.secure_s3.openbao.dto.OpenbaoDecryptResponse;
import com.xbf8.secure_s3.openbao.dto.OpenbaoGenerateDataKey;
import com.xbf8.secure_s3.openbao.dto.OpenbaoResponse;

@Service
public class OpenbaoService {
    private final String kekName;
    private final RestClient restClient;
    public OpenbaoService(SS3Properties ss3Properties) {
        this.kekName = ss3Properties.openbao().keyName();
        this.restClient = RestClient.builder()
            .baseUrl(ss3Properties.openbao().url() + "/v1")
            .defaultHeader("X-Vault-Token", ss3Properties.openbao().token())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

    public OpenbaoGenerateDataKey generateDataKey() {
        OpenbaoResponse<OpenbaoDataKeyResponse> response = this.restClient.post()
            .uri("/transit/datakey/plaintext/{key}", this.kekName)
            .body(new OpenbaoDataKeyRequest(256))
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
        OpenbaoDataKeyResponse data = response.data();
        byte[] plaintext = Base64.getDecoder().decode(data.plaintext());
        if (plaintext.length != 32) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Invalid DEK length"
            );
        }
        return new OpenbaoGenerateDataKey(plaintext, data.ciphertext());
    }

    public byte[] decryptDataKey(String edek) {
        OpenbaoResponse<OpenbaoDecryptResponse> response = this.restClient.post()
            .uri("/transit/decrypt/{key}", this.kekName)
            .body(new OpenbaoDecryptRequest(edek))
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});
        if (response == null || response.data() == null) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Openbad returend an empty response"
            );
        }
        byte[] plaintext = Base64.getDecoder().decode(response.data().plaintext());
        if (plaintext.length != 32) {
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Invalid DEK length"
            );
        }
        return plaintext;
    }
}

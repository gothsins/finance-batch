package com.guilherme.finance_batch.writer;

import com.guilherme.finance_batch.config.ApiProperties;
import com.guilherme.finance_batch.dto.AuthRequest;
import com.guilherme.finance_batch.dto.TransactionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiTransactionWriter implements ItemWriter<TransactionRequest> {

    private final ApiProperties apiProperties;
    private String token;

    private RestClient restClient() {
        return RestClient.builder()
                .baseUrl(apiProperties.getBaseUrl())
                .build();
    }

    private String authenticate() {
        try {
            var response = restClient()
                    .post()
                    .uri("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new AuthRequest(apiProperties.getEmail(), apiProperties.getPassword()))
                    .retrieve()
                    .toEntity(String.class);

            String body = response.getBody();

            String token = body.replaceAll(".*\"token\":\"([^\"]+)\".*", "$1");
            return token;

        } catch (Exception e) {
            log.error("Falha na autenticação — {}: {}", e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    @Override
    public void write(Chunk<? extends TransactionRequest> chunk) {
        if (token == null) {
            log.info("Autenticando no finance-api...");
            token = authenticate();
            log.info("Autenticado com sucesso.");
        }

        for (TransactionRequest transaction : chunk) {
            try {
                var response = restClient()
                        .post()
                        .uri("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .body(transaction)
                        .retrieve()
                        .toBodilessEntity();

                log.info("✅ Importada: {} | {} | {}",
                        transaction.getDescription(),
                        transaction.getAmount(),
                        transaction.getType());

            } catch (Exception e) {
                log.error("❌ Erro ao importar: {} — Causa: {} | Mensagem: {}",
                        transaction.getDescription(),
                        e.getClass().getSimpleName(),
                        e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }
}
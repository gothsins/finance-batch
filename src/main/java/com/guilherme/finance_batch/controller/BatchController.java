package com.guilherme.finance_batch.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {

    private final JobLauncher jobLauncher;
    private final Job csvImportJob;

    @PostMapping("/import")
    public ResponseEntity<String> runImport() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(csvImportJob, params);

            return ResponseEntity.ok("Job iniciado com sucesso.");
        } catch (Exception e) {
            log.error("Erro ao iniciar o Job: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("Erro ao iniciar o Job: " + e.getMessage());
        }
    }
}
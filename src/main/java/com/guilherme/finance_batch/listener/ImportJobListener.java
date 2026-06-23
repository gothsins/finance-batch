package com.guilherme.finance_batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
public class ImportJobListener implements JobExecutionListener {

    @Override
    public void afterJob(JobExecution jobExecution) {
        long readCount = jobExecution.getStepExecutions()
                .iterator().next().getReadCount();
        long writeCount = jobExecution.getStepExecutions()
                .iterator().next().getWriteCount();
        long skipCount = jobExecution.getStepExecutions()
                .iterator().next().getProcessSkipCount()
                + jobExecution.getStepExecutions()
                .iterator().next().getWriteSkipCount();

        String report = String.format("""
                ========================================
                 RELATÓRIO DE IMPORTAÇÃO - finance-batch
                ========================================
                 Data/Hora : %s
                 Status    : %s
                 Lidas     : %d
                 Importadas: %d
                 Rejeitadas: %d
                ========================================
                """,
                LocalDateTime.now(),
                jobExecution.getStatus(),
                readCount,
                writeCount,
                skipCount
        );

        log.info("\n{}", report);
        saveReport(report);
    }

    private void saveReport(String report) {
        try {
            new java.io.File("output").mkdirs();
            try (FileWriter fw = new FileWriter("output/import-report.txt", true)) {
                fw.write(report);
            }
        } catch (IOException e) {
            log.error("Erro ao salvar relatório: {}", e.getMessage());
        }
    }
}
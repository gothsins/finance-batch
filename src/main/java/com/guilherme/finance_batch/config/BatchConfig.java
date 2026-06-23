package com.guilherme.finance_batch.config;

import com.guilherme.finance_batch.dto.CsvTransaction;
import com.guilherme.finance_batch.dto.TransactionRequest;
import com.guilherme.finance_batch.listener.ImportJobListener;
import com.guilherme.finance_batch.processor.TransactionProcessor;
import com.guilherme.finance_batch.reader.CsvTransactionReader;
import com.guilherme.finance_batch.writer.ApiTransactionWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BatchConfig {

    private final TransactionProcessor processor;
    private final ApiTransactionWriter writer;
    private final ImportJobListener jobListener;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job csvImportJob() {
        return new JobBuilder("csvImportJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(csvImportStep())
                .build();
    }

    @Bean
    public Step csvImportStep() {
        return new StepBuilder("csvImportStep", jobRepository)
                .<CsvTransaction, TransactionRequest>chunk(10, transactionManager)
                .reader(CsvTransactionReader.create())
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .skipLimit(10)
                .skip(Exception.class)
                .build();
    }
}
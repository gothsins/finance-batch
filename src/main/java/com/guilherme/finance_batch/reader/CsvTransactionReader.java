package com.guilherme.finance_batch.reader;

import com.guilherme.finance_batch.dto.CsvTransaction;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.core.io.ClassPathResource;

public class CsvTransactionReader {

    public static FlatFileItemReader<CsvTransaction> create() {
        return new FlatFileItemReaderBuilder<CsvTransaction>()
                .name("csvTransactionReader")
                .resource(new ClassPathResource("input/transactions.csv"))
                .delimited()
                .delimiter(",")
                .names("date", "description", "amount", "type", "categoryId")
                .targetType(CsvTransaction.class)
                .linesToSkip(1) // pula o cabeçalho
                .build();
    }
}
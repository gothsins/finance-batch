package com.guilherme.finance_batch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TransactionRequest {
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private String type;
    private Long categoryId;
}
package com.guilherme.finance_batch.dto;

import lombok.Data;

@Data
public class CsvTransaction {
    private String date;
    private String description;
    private String amount;
    private String type;
    private String categoryId;
}
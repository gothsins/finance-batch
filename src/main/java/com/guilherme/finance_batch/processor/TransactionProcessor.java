package com.guilherme.finance_batch.processor;

import com.guilherme.finance_batch.dto.CsvTransaction;
import com.guilherme.finance_batch.dto.TransactionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Slf4j
@Component
public class TransactionProcessor implements ItemProcessor<CsvTransaction, TransactionRequest> {

    @Override
    public TransactionRequest process(CsvTransaction item) {
        try {
            LocalDate date = LocalDate.parse(item.getDate().trim());

            BigDecimal amount = new BigDecimal(item.getAmount().trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Linha rejeitada — amount inválido: {}", item);
                return null;
            }

            String type = item.getType().trim();
            if (!type.equals("INCOME") && !type.equals("EXPENSE")) {
                log.warn("Linha rejeitada — type inválido: {}", item);
                return null;
            }

            Long categoryId = Long.parseLong(item.getCategoryId().trim());

            return new TransactionRequest(
                    item.getDescription().trim(),
                    amount,
                    date,
                    type,
                    categoryId
            );

        } catch (DateTimeParseException e) {
            log.warn("Linha rejeitada — data inválida: {}", item);
            return null;
        } catch (NumberFormatException e) {
            log.warn("Linha rejeitada — número inválido: {}", item);
            return null;
        }
    }
}
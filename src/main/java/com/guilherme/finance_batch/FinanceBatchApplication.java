package com.guilherme.finance_batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class FinanceBatchApplication {
	public static void main(String[] args) {
		SpringApplication.run(FinanceBatchApplication.class, args);
	}
}

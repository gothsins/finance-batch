package com.guilherme.finance_batch.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "finance.api")
public class ApiProperties {
    private String baseUrl;
    private String email;
    private String password;
}
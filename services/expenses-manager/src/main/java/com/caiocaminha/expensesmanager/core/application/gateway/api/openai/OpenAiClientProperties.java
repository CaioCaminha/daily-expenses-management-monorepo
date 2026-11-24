package com.caiocaminha.expensesmanager.core.application.gateway.api.openai;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//@Component
@Getter
public class OpenAiClientProperties {
//    @Value("${open-api.apiKey}")
    private String apiKey;
}

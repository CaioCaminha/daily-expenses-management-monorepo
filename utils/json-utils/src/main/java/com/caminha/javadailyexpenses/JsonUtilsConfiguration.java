package com.caminha.javadailyexpenses;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@ComponentScan
@Configuration
public class JsonUtilsConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return ObjectMapperUtils.defaultObjectMapper();
    }

}

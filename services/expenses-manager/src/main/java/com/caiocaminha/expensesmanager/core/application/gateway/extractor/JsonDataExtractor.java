package com.caiocaminha.expensesmanager.core.application.gateway.extractor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class JsonDataExtractor<T> {

    private final ObjectMapper objectMapper;

    public JsonDataExtractor(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
    }

//    @Override
//    public T extract(Mono<String> data, Class<T>  classz) throws JsonProcessingException {
//        data.flatMap(dataExtracted -> {
//            try {
//                return objectMapper.readValue(dataExtracted, classz);
//            } catch (JsonProcessingException e) {
//                throw new RuntimeException(e);
//            }
//        }).onErrorResume( throwable -> {
//            throws throwable
//        });
//    }
}

package com.caiocaminha.expensesmanager.core.application.gateway.extractor;

import com.caiocaminha.expensesmanager.core.domain.shared.DataExtractorPort;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CsvDataExtractor<T> implements DataExtractorPort<T> {

    @Override
    public T extract(Mono<String> data, Class<T> classz) throws JsonProcessingException {
        return null;
    }
}

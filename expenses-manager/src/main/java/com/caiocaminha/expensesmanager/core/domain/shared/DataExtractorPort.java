package com.caiocaminha.expensesmanager.core.domain.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import reactor.core.publisher.Mono;

public interface DataExtractorPort<T> {
    T extract(Mono<String> data, Class<T>  classz) throws JsonProcessingException;
}

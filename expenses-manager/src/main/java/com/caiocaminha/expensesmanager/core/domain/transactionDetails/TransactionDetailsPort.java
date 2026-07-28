package com.caiocaminha.expensesmanager.core.domain.transactionDetails;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public interface TransactionDetailsPort {
    Flux<TransactionDetails> findByUserId(UUID userId);

    Mono<TransactionDetails> upsert(TransactionDetails transactionDetails);
}

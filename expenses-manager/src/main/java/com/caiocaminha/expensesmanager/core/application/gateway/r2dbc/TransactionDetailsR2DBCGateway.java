package com.caiocaminha.expensesmanager.core.application.gateway.r2dbc;

import com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.entities.TransactionDetailsEntity;
import com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.repositories.TransactionDetailsRepository;
import com.caiocaminha.expensesmanager.core.domain.transactionDetails.Category;
import com.caiocaminha.expensesmanager.core.domain.transactionDetails.TransactionDetails;
import com.caiocaminha.expensesmanager.core.domain.transactionDetails.TransactionDetailsPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class TransactionDetailsR2DBCGateway implements TransactionDetailsPort {

    private final TransactionDetailsRepository transactionDetailsRepository;

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    public TransactionDetailsR2DBCGateway(
            TransactionDetailsRepository repository,
            R2dbcEntityTemplate r2dbcEntityTemplate
    ) {
        this.transactionDetailsRepository = repository;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    @Override
    public Flux<TransactionDetails> findByUserId(UUID userId) {
        return transactionDetailsRepository.findByUserId(userId)
                .map(TransactionDetailsEntity::toDomain);
    }

    @Override
    public Mono<TransactionDetails> upsert(TransactionDetails transactionDetails) {
        /**
         * Formatter that supports nano secconds being from 0 to 9 digits long
         */
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .optionalEnd()
                .toFormatter();

        log.info("starting to save into database");
        log.info("description: %s | cost: %s | transactionDate: %s".formatted(transactionDetails.details(), transactionDetails.cost(), transactionDetails.transactionDate()));
        return r2dbcEntityTemplate.getDatabaseClient().sql(
                """  
                INSERT INTO transaction_details (id, user_id, details, category, cost, date_executed, paid_by, created_at, updated_at)
                   VALUES (:id, :userId, :details, :category, :cost, :dateExecuted, :paidBy, :createdAt, :updatedAt)
                   ON CONFLICT (id)
                   DO UPDATE SET
                        details = EXCLUDED.details,
                        category = EXCLUDED.category,
                        cost = EXCLUDED.cost,
                        date_executed = EXCLUDED.date_executed,
                        paid_by = EXCLUDED.paid_by,
                        updated_at = NOW()::TIMESTAMP
                   RETURNING *"""
        ).bind("id", transactionDetails.id())
                .bind("userId", transactionDetails.userId())
                .bind("details", transactionDetails.details())
                .bind("category", transactionDetails.category().name())
                .bind("cost", transactionDetails.cost())
                .bind("dateExecuted", transactionDetails.transactionDate())
                .bind("paidBy", transactionDetails.paidBy())
                .bind("createdAt", transactionDetails.createdAt()) //TODO continue binding
                .bind("updatedAt", transactionDetails.updatedAt())
                .map((row, metadata) -> {
                    log.info("building TransactionDetails from RowSpec");
                    log.info("building new transactionDetailsDto from CSV description: %s | cost: %s | transactionDate: %s".formatted(row.get("created_at", String.class),row.get("updated_at", String.class), LocalDate.parse(Objects.requireNonNull(row.get("date_executed", String.class)))));
                    return new TransactionDetails(
                            UUID.fromString(Objects.requireNonNull(row.get("id", String.class))),
                            UUID.fromString(Objects.requireNonNull(row.get("user_id", String.class))),
                            Category.valueOf(row.get("category", String.class)),
                            row.get("details", String.class),
                            row.get("cost", Double.class),
                            LocalDate.parse(Objects.requireNonNull(row.get("date_executed", String.class))),
                            row.get("paid_by", String.class),
                            LocalDateTime.parse(Objects.requireNonNull(row.get("created_at", String.class)), formatter),
                            LocalDateTime.parse(Objects.requireNonNull(row.get("updated_at", String.class)), formatter)
                    );
                }).one();
    }

    private String retrievePropertyNameThroughReflection(
            String columnName
    ) {
        return Arrays.stream(TransactionDetailsEntity.class.getFields())
                .filter(field -> field.getName().equals(columnName)).findFirst()
                .map(field -> Arrays.stream(field.getAnnotationsByType(Column.class)).findFirst().get().value())
                .orElse("");
    }

}

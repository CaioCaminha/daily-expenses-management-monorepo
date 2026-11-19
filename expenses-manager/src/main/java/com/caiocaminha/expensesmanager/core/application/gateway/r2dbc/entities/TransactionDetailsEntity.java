package com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.entities;

import com.caiocaminha.expensesmanager.core.domain.transactionDetails.TransactionDetails;
import com.caiocaminha.expensesmanager.core.domain.transactionDetails.Category;
import com.caminha.postgresutils.utils.utils.persistence.PersistableEntity;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Table(TransactionDetailsEntity.TABLE_NAME)
@AllArgsConstructor
public class TransactionDetailsEntity extends PersistableEntity<String> {
    @Id
    private String id = UUID.randomUUID().toString();
    @Column("user_id")
    private String userId;
    @Column("details")
    private String details;
    @Column("category")
    private Category category;
    @Column("cost")
    private Double cost;
    @Column("date_executed")
    private LocalDate date;
    @Column("paid_by")
    @Nullable
    private String paidBy;

    public final static String TABLE_NAME = "transaction_details";


    public TransactionDetailsEntity(
            UUID userId,
            String details,
            Category category,
            Double cost,
            LocalDate date,
            String paidBy
    ) {
        this.userId = userId.toString();
        this.details = details;
        this.category = category;
        this.cost = cost;
        this.date = date;
        this.paidBy = paidBy;
    }

    @Override
    public String getId() {
        return this.id;
    }


    public TransactionDetails toDomain() {
        return new TransactionDetails(
                UUID.fromString(this.id),
                UUID.fromString(this.userId),
                this.category,
                this.details,
                this.cost,
                this.date,
                this.paidBy,
                this.createdAt,
                this.updatedAt
        );
    }

}

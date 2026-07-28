package com.caiocaminha.expensesmanager.core.application.gateway.controller.transaction.dto;

import com.univocity.parsers.annotations.Parsed;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
public class TransactionDetailsDto {

    @Parsed(field = "Descrição")
    @Getter
    private String description;
    @Parsed(field = "Histórico")
    private String type;
    @Parsed(field = "Data Lançamento")
    private String transactionDate;
    @Parsed(field = "Valor")
    private String cost;
    @Parsed(field = "Saldo")
    private String balance;

    public static TransactionDetailsDto of(
            String description,
            String type,
            String transactionDate,
            String cost,
            String balance
    ) {
        return new TransactionDetailsDto(description, type, transactionDate, cost, balance);
    }

    public TransactionType getType() {
        return switch(type.toLowerCase()) {
            case "pagamento simples nacional",
                 "pagamento darf numerado"-> TransactionType.TAX_BILLING;
            case "compra no débito", "compra no debito", "debito online td" -> TransactionType.DEBIT_CARD;
            case "pix enviado" -> TransactionType.PIX;
            case "pix recebido", "transferencia recebida" -> TransactionType.EARNINGS;
            default -> TransactionType.UNIDENTIFIED_TRANSACTION;
        };
    }

    public LocalDate getTransactionDate() {
        return LocalDate.parse(transactionDate, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public Double getCost() {
        return Double.parseDouble(
                cost.replace("-", "")
                        .replace(",", ".")
        );
    }

    public Double getBalance() {
        return Double.parseDouble(balance);
    }

}

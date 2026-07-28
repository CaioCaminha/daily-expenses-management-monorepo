package com.caiocaminha.expensesmanager.core.domain.transactionDetails;

//something similar to what I'm doing at work. Using some kind of CQRS at the application layer
//use *request classes to creating/updating and regular domain classes for retrieving flows
public record CreateTransactionRequest(
        String data //can be a file
) {
}

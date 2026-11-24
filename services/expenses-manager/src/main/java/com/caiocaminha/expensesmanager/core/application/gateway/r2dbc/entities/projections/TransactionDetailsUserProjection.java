package com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.entities.projections;

/**
 * Projection for retrieving only userId and details from transaction_details
 * @param userId
 * @param details
 */
public record TransactionDetailsUserProjection(String userId, String details) {
}

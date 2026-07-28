package com.caiocaminha.expensesmanager.core.application.utils;


import com.caiocaminha.expensesmanager.core.utils.DefaultFunction;

public interface TransactionProvider {

    <T> T withTransaction(
            DefaultFunction<T> block
    );

}

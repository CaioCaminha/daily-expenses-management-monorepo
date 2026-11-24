package com.caiocaminha.expensesmanager.core.archive_studies.aop;

import org.springframework.stereotype.Component;

@Component
public class AccountDAOImpl implements AccountDAO{

    @Override
    public void addAccount() {
        System.out.println("doing something");
    }
}

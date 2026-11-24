package com.caiocaminha.expensesmanager.core.archive_studies.aop;

import org.aspectj.lang.annotation.Before;

@ComponentAspect
public class DemoLoggingAspect {

    @Before("execution(public void addAccount())")
    public void beforeAddAccountAdvice() {
        System.out.println("Doing something before");
    }


}

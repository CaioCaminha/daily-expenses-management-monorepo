package com.caiocaminha.expensesmanager.core.application.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.CorePublisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
@Aspect
public class CustomTransactionalAspect {

    private final TransactionalOperator transactionalOperator;

    public CustomTransactionalAspect(
            TransactionalOperator operator
    ) {
        this.transactionalOperator = operator;
    }


    /**
     * Postgres default isolation level is READ_COMMITED, allows the transaction to see commited
     * changes from concurrent transactions;
     *
     * Repeatable read might be required for complex queries and updates, is implemented using
     * Snapshot Isolation, it does not see changes commited after its own transaction started.
     *  If using repeatable read it needs retrying logic because it might throw:
     *      "Could not serialize access due to concurrent update"
     *
     * todo I need to use .as(transactionalOperator::transactional) operator at the end of Publisher
     *  maybe it's a good opportunity to use a Spring AOP proxy to transform this logic into an annotation
     */
    @Around("@annotation(CustomTransactional)")
    public Object withTransaction(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class<?> returnType = methodSignature.getReturnType();

        Object result = joinPoint.proceed();

        if(returnType == Mono.class) {
            return ((Mono<?>) result).as(transactionalOperator::transactional); //this wraps the whole reactive pipeline under the same transaction
        }
        if(returnType == Flux.class){
            return ((Flux<?>) result).as(transactionalOperator::transactional);//this wraps the whole reactive pipeline under the same transaction
        }
        return result;
    }

}

package com.caiocaminha.expensesmanager.core.archive_studies;

import java.time.LocalDate;
//import java.util.concurrent.StructuredTaskScope;

public class StructuredConcurrency {

//    public static void main(String[] args) {
//        try(var threadScope = new StructuredTaskScope<OrderInfo>()) {
//            StructuredTaskScope.Subtask<OrderInfo> task1 = threadScope.fork(
//                    () -> new OrderInfo("caio", "10", "caio", LocalDate.now())
//            );
//
//            task1.get();
//
//            var result = threadScope.join();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

}


record OrderInfo(
        String name,
        String quantity,
        String owner,
        LocalDate deliveryDate
){}

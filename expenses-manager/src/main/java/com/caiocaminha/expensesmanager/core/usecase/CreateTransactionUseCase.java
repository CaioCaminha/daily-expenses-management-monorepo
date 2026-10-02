package com.caiocaminha.expensesmanager.core.usecase;

import com.caiocaminha.expensesmanager.core.domain.transactionDetails.TransactionDetails;
import com.caiocaminha.expensesmanager.core.domain.transactionDetails.TransactionDetailsPort;
import tools.jackson.dataformat.csv.CsvMapper;

import java.io.IOException;

public class CreateTransactionUseCase {

    private static final CsvMapper mapper = new CsvMapper();


    private final TransactionDetailsPort transactionDetailsPort;

    public CreateTransactionUseCase(
            TransactionDetailsPort transactionDetailsPort
    ) {
        this.transactionDetailsPort = transactionDetailsPort;
    }

    public void execute(
        TransactionDetails transactionDetails
    ) throws IOException {

        //TODO should have an unique constraint based on userId + cost + transactionDate + details;
        // problem with that: If I buy on the same location, at the same day, with the same cost - it would fail to insert
        // this affects data consistency, since it would not insert this "duplicated" field

        //TODO MUST TAKE INTO ACCOUNT THE BALANCE - IF THE BALANCE IS UNCHANGED, IT'S A DUPLICATE, OTHERWISE IS A VALID TRANSACTION


        transactionDetails.internalHashCode();



        transactionDetails.transactionDate();
        transactionDetails.cost();
        transactionDetails.details();
        transactionDetails.userId();

        transactionDetailsPort.upsert(transactionDetails);
    }

}

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

        //TODO think on a way of properly checking unicity



        //TODO finish CreateTransactionUseCase / createFromMultipart  handler
        //  Next step is to document process of creating a handlerFunction that supports receiving MultipartData from ServerRequest
        // document internally the process of creating createFromMultipart handler function
        // document on medium a how-to article of how to consume a multipartfile from an WebFlux.fn HandlerFunction
        //     consuming a Flux<DataBuffer> using PipedInputStreams and PipedOutputStreams


        transactionDetails.internalHashCode();



        transactionDetails.transactionDate();
        transactionDetails.cost();
        transactionDetails.details();
        transactionDetails.userId();

        transactionDetailsPort.upsert(transactionDetails);
    }

}

package com.caiocaminha.expensesmanager.core.application.gateway.controller.transaction;

import com.caiocaminha.expensesmanager.core.application.gateway.controller.transaction.dto.TransactionDetailsDto;
import com.caminha.postgresutils.utils.utils.transactional.CustomTransactional;
import com.caiocaminha.expensesmanager.core.domain.transactionDetails.Category;
import com.caiocaminha.expensesmanager.core.domain.transactionDetails.TransactionDetails;
import com.caiocaminha.expensesmanager.core.domain.transactionDetails.TransactionDetailsPort;
import com.caiocaminha.expensesmanager.core.domain.user.UserDetailsRequest;
import com.caiocaminha.expensesmanager.core.domain.user.UserDetailsPort;
import com.caiocaminha.expensesmanager.core.usecase.CreateTransactionUseCase;
import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.ResultIterator;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePartEvent;
import org.springframework.http.codec.multipart.PartEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.Function;


@Slf4j
@Component
public class TransactionHandler {

    private final Integer DATE_INDEX = 0;
    private final Integer HISTORY_INDEX = 1;
    private final Integer DESCRIPTION_INDEX = 2;
    private final Integer VALUE_INDEX = 3;
    private final Integer BALANCE_INDEX = 4;

//    private final DataExtractorPort<TransactionDetails> dataExtractorPort;
//
    private final CreateTransactionUseCase createTransactionUseCase;

    private final UserDetailsPort userDetailsPort;

    private final TransactionDetailsPort transactionDetailsPort;


    public TransactionHandler(
            CreateTransactionUseCase createTransactionUseCase,
//            @Qualifier
//            DataExtractorPort<TransactionDetails> dataExtractorPort
            UserDetailsPort userDetailsPort,
            TransactionDetailsPort transactionDetailsPort,
            TransactionalOperator transactionalOperator
    ) {
        this.userDetailsPort = userDetailsPort;
        this.transactionDetailsPort = transactionDetailsPort;
        this.createTransactionUseCase = createTransactionUseCase;
//        this.dataExtractorPort = dataExtractorPort;
    }

//    public Mono<ServerResponse> testTransactionUpsert(ServerRequest request) {
//        return request.bodyToMono(TransactionDetailsRequest.class)
//                .map(TransactionDetailsRequest::toDomain)
//                .flatMap(transactionDetails -> transactionProvider.withTransaction(
//                        () -> transactionDetailsPort.upsert(transactionDetails)
//                ))
//                .flatMap(savedTransactionDetails -> {
//                    System.out.println("Returning ServerResponse");
//                    return ServerResponse.ok().bodyValue(savedTransactionDetails); //serialization error here Java LocalDate class
//                }).onErrorResume(e ->
//                        ServerResponse.badRequest().bodyValue("Error occured %s".formatted(e.getMessage()))
//                );
//    }


    @CustomTransactional
    public Mono<ServerResponse> testHandler(ServerRequest request) {
        return request.bodyToMono(UserDetailsRequest.class)
                .flatMap(userDetailsPort::upsertUser)
                .flatMap(savedUserDetails -> {
                    System.out.println("Serializing object to return ServerResponse");
                    return ServerResponse.ok().bodyValue(savedUserDetails);
                })
                .onErrorResume(e ->
                        ServerResponse.badRequest()
                                .bodyValue("Error saving user details %s".formatted(e.toString()))
                );
    }

    @CustomTransactional
    public Mono<ServerResponse> createTransaction(ServerRequest request) {
        return request.bodyToMono(TransactionDetails.class)
                .flatMap(transactionDetailsPort::upsert)
                .flatMap(transactionDetails ->  ServerResponse.ok().bodyValue(transactionDetails))
                .onErrorResume(ex -> ServerResponse.badRequest().bodyValue(ex.getMessage()));

    }

    public Mono<ServerResponse> getTransaction(ServerRequest request) {
        return ServerResponse.ok().bodyValue(
                request.queryParam("userId").map(
                        userId -> transactionDetailsPort.findByUserId(UUID.fromString(userId))
                ).orElse(Flux.empty())
        );
    }

    /**
     *concateMap: Used when each transformation returns another Reactive Stream ( in this case a Flux)
     *  Subscribes to each element in order, ensuring that element 1 is emitted before element 2 starts, ensuring sequential subscription
     *  Useful when it's important to guarantee the order (Alternative Option for flatMap when order is important)
     *  but if you want to process in parallel and mantain the order can use flatMapSequencial, subscribes in parallel but buffer reorders when emitting
     *
     *switchOnFirst: Lets you inspect the first signal (Usually the first element emmited by the source Flux)
     *  before deciding how you are going to consume the rest of the Flux. It's very useful when the way you
     *  process a stream depends on what the first element is. In this case it's useful because we need to decide
     *  if it's an instance of FormPartEvent or FilePartEvent, this checking will decide how we are going to consume
     *  all the remaining elements on the Flux
     *
     */
    @CustomTransactional
    public Mono<ServerResponse> createFromMultipart(ServerRequest request) {

        Flux<PartEvent> allPartEvents = request.bodyToFlux(PartEvent.class);
        /**
         * concatMap subscribes to inner publishers sequentially - which is essential for processing File Parts
         *  it's not possible to have interleaving of contents;
         */
        /**
         * Create an abstraction with an interface defining the contract and providers
         * Should have a provider for serializing data from a CSV file
         * Should have a provider for serializing data from a PDF file
         * Should have a provider for serializing data from a Json String
         */
        return ServerResponse.created(URI.create("")).body(
                allPartEvents.windowUntil(PartEvent::isLast)
                        .concatMap(part -> part.switchOnFirst((signal, partEvents) -> {
                            if(signal.hasValue()) { //will return true if it's an onNext signal
                                PartEvent event = signal.get(); //retrieves the item associated with this onNext signal

                                if(!signal.hasValue()) return Flux.empty();

                                if(event instanceof FilePartEvent) {
                                    Flux<DataBuffer> contents = partEvents.map(PartEvent::content);

                                    return streamParseCSV(contents)
                                            .buffer(500) //todo check if this is a proper value for buffering
                                            .flatMap( transactions ->
                                                    Flux.fromStream(transactions.stream().map(transactionDetailsPort::upsert))
                                            ).flatMap(Function.identity());
                                }
                            }
                            else {
                                log.error("Unsuported operation");
                            }
                            return Flux.empty();
                        })),
                new ParameterizedTypeReference<>() {}
        ).onErrorResume(e ->
                        ServerResponse.badRequest()
                                .bodyValue("Error saving transaction details %s".formatted(e.toString()))
        );
    }

    //todo - this needs to be extracted to a strategy, or a provider like structure, we need to support more file types
    // or the handlers itself needs to be a provider - or could have a provider that supports receiving a Flux<DataBuffer>
    private Flux<TransactionDetails> streamParseCSV(Flux<DataBuffer> contents) {
        return Flux.create(sink -> {
            PipedInputStream inputStream = new PipedInputStream(64 * 1024);
            PipedOutputStream outputStream;

            try {
                outputStream = new PipedOutputStream(inputStream);
            } catch (IOException e) {
                sink.error(e);
                return;
            }

            DataBufferUtils.write(contents, outputStream)
                    .doOnError(sink::error)
                    .doFinally( signalType -> {
                        try{ outputStream.close();} catch (IOException ignored) {}
                    })
                    //evaluate if this can be changed to .doOnNext/.doFinally in order to avoid breaking reactive pipeline
                    .subscribe(DataBufferUtils.releaseConsumer());

            Schedulers.boundedElastic().schedule( () -> {
               try(InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                   CsvParserSettings settings = new CsvParserSettings();
                   settings.setNumberOfRowsToSkip(4);
                   settings.setHeaderExtractionEnabled(true);
                   settings.setDelimiterDetectionEnabled(true, ';', ',');
                   settings.setLineSeparatorDetectionEnabled(true);

                   CsvParser parser = new CsvParser(settings);

                   ResultIterator<Record, ParsingContext> iterator = parser.iterateRecords(reader).iterator();


                   //TODO first lines of statement has some additional information - check how to workaround this
                   while(iterator.hasNext() && !sink.isCancelled()) {
                       Record record = iterator.next();
                       try {
                           System.out.println("caio - start from here");
//                           System.out.println(record);
                           System.out.println(record.toIndexMap());
                           var transactionDetailsDto = TransactionDetailsDto.of(
                                   record.getString(DESCRIPTION_INDEX),
                                   record.getString(HISTORY_INDEX),
                                   record.getString(DATE_INDEX),
                                   record.getString(VALUE_INDEX),
                                   record.getString(BALANCE_INDEX)
                           );

                           log.info("building new transactionDetailsDto from CSV description: %s | cost: %s | transactionDate: %s".formatted(transactionDetailsDto.getDescription(), transactionDetailsDto.getCost(), transactionDetailsDto.getTransactionDate()));
                           sink.next(
                                   new TransactionDetails(
                                           UUID.randomUUID(),
                                           Category.MARKET,
                                           transactionDetailsDto.getDescription(),
                                           transactionDetailsDto.getCost(),
                                           transactionDetailsDto.getTransactionDate(),
                                           "caio"
                                   )
                           );
                       } catch (IllegalArgumentException ex) {
                           log.error(ex.getMessage());
                           log.info("Regular fields were not present");
                       }
                   }

                   sink.complete();
               } catch (Throwable e) {
                   sink.error(e);
               }
            });

        }, FluxSink.OverflowStrategy.BUFFER);
    }

}

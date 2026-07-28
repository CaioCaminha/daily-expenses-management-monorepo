package com.caiocaminha.expensesmanager.core.application.gateway.controller.transaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TransactionRouter {

    private final TransactionHandler transactionHandler;

    public TransactionRouter(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }


    @Bean
    public RouterFunction<ServerResponse> transactionRouterFunction() {
        return route()
                .before(this::logRequest)
                .path("v1/statement", builder ->
                        builder
                                .POST(accept(MULTIPART_FORM_DATA).and(contentType(MULTIPART_FORM_DATA)), transactionHandler::createFromMultipart)
                                .nest( accept(APPLICATION_JSON), builder1 -> builder1
                                        .GET(transactionHandler::getTransaction)
                                        .POST(transactionHandler::createTransaction)
                                        .build()
                                )
                ).after(this::logResponse)
                .build();
    }




    private ServerRequest logRequest(ServerRequest request) {
        return request;
    }

    private ServerResponse logResponse(ServerRequest request, ServerResponse response) {
        return response;
    }

    @Bean
    public RouterFunction<ServerResponse> transactionRoutes() {
        /**
         * Example of nested route and Filtering using before function
         */
//        route()
//                .path("/person", b1 -> b1
//                        .nest(accept(APPLICATION_JSON), b2 -> b2
//                                .GET("/{id}", transactionHandler::createTransaction)
//                                .GET(transactionHandler::createTransaction)
//                                .before(serverRequest -> ServerRequest.from(serverRequest)
//                                        .header("X-RequestHeader", "Value")
//                                        .build()
//                                )
//                        )
//                        .POST("create", transactionHandler::createTransaction)
//                )
//                .after((request, response) -> logResponse(response))
//                .build();

        return route()
                .before(this::logRequest)
                .path("v1/statement", builder ->
                        builder
                                .POST(accept(MULTIPART_FORM_DATA).and(contentType(MULTIPART_FORM_DATA)), transactionHandler::createFromMultipart)
                                .nest(accept(APPLICATION_JSON), builder1 ->  { builder1
                                        .GET(transactionHandler::getTransaction)
                                        .POST(transactionHandler::createTransaction)
                                        .build();
                                })
                ).after(this::logResponse)
                .build();
    }

}

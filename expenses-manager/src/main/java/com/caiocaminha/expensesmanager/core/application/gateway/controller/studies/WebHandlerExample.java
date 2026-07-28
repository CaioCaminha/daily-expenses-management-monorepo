package com.caiocaminha.expensesmanager.core.application.gateway.controller.studies;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * WebHandler logic sits below @Controller @RestController annotations
 * it's a lower level declaration
 */

public class WebHandlerExample implements WebHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        exchange.getMultipartData();//gets multipart data
        if(exchange.getRequest().getPath().value().equals("/testwebhandler")) {
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);

            DataBuffer buffer = exchange
                    .getResponse().bufferFactory().wrap("hello world from web handler".getBytes(StandardCharsets.UTF_8));

            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
        exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        return exchange.getResponse().setComplete();
    }
}

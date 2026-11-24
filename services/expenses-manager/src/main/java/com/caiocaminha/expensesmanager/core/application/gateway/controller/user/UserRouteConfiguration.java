package com.caiocaminha.expensesmanager.core.application.gateway.controller.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;



@Configuration
public class UserRouteConfiguration {

    private final UserHandler userHandler;

    public UserRouteConfiguration(
            UserHandler userHandler
    ) {
        this.userHandler = userHandler;
    }

    /**
     * This is how the official documentation recommends to create the RouterFunction bean
     * @return
     */
    @Bean
    public RouterFunction<ServerResponse> userRoute() {
        HandlerFunction<ServerResponse> handlerFunction = request -> request
                .bodyToMono(String.class)
                .flatMap(body ->
                        ServerResponse.ok().bodyValue(body)
                );



        route(request -> request.method() == HttpMethod.GET, userHandler::getUser);
        return route()
                .path("/user", builder -> builder
                        .GET("/{email}", userHandler::getUser))
                .build();
    }


}

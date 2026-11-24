package com.caiocaminha.expensesmanager.core.application.gateway.controller.user;

import com.caiocaminha.expensesmanager.core.domain.user.UserDetailsPort;
import com.caiocaminha.expensesmanager.core.domain.user.UserEmailNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

@Component
public class UserHandler {

    private final UserDetailsPort userDetailsPort;

    public UserHandler(
            UserDetailsPort userDetailsPort
    ) {
        this.userDetailsPort = userDetailsPort;
    }

    public Mono<ServerResponse> getUser(ServerRequest request) {
        try {
            return ifPresentOrElseGet(
                    request.queryParam("email"),
                    userDetailsPort::getByEmail
            ).flatMap(user -> ServerResponse.ok().bodyValue(user)).onErrorResume(
                    e -> ServerResponse.badRequest().bodyValue("Error retrieving user by email | error: %s".formatted(e.getMessage()))
            );
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * If present execute the following consumerBlock
     * otherwise throw an exception or return a null value

     */
    private <T, R> R ifPresentOrElseGet(
            Optional<T> value,
            Function<? super T, R> function
    ) throws Throwable {
        return value.map(function).orElseThrow(() -> new UserEmailNotFoundException("Not possible to retrieve user"));
    }

}

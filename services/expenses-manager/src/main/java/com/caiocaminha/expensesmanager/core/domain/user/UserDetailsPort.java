package com.caiocaminha.expensesmanager.core.domain.user;

import reactor.core.publisher.Mono;

public interface UserDetailsPort {

    Mono<UserDetails> upsertUser(UserDetailsRequest user);

    Mono<UserDetails> getByEmail(String email);

}

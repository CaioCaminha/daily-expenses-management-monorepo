package com.caiocaminha.expensesmanager.core.application.gateway.r2dbc;

import com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.entities.UserDetailsEntity;
import com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.repositories.UserDetailsRepository;
import com.caiocaminha.expensesmanager.core.domain.user.UserDetails;
import com.caiocaminha.expensesmanager.core.domain.user.UserDetailsRequest;
import com.caiocaminha.expensesmanager.core.domain.user.UserDetailsPort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;

@Service
public class UserDetailsR2DBCGateway implements UserDetailsPort {

    private final UserDetailsRepository userDetailsRepository;

    public UserDetailsR2DBCGateway(
            UserDetailsRepository userDetailsRepository
    ) {
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    public Mono<UserDetails> upsertUser(UserDetailsRequest user) {
        System.out.println("saving on the database");
        UserDetailsEntity entity = UserDetailsEntity.from(user).toUpdated();
        return userDetailsRepository.upsert(
                entity.getId(),
                entity.firstName(),
                entity.lastName(),
                entity.email(),
                entity.username(),
                entity.createdAt,
                entity.updatedAt
        ).map(UserDetailsEntity::toDomain);
    }

    private void testing() {
        this.userDetailsRepository.findAll()
                .log()
                .blockFirst();
    }

    @Override
    public Mono<UserDetails> getByEmail(String email) {
        /**
         * Mono.flatMap expects a Function that receives a generic from type T of the Mono
         *  and returns a Mono of type R the returned type
         * <p>
         * Mono.map expects a Function that receives a generic from type T of the Mono
         *  but returns an object of type R the returned type, does not expect a Mono
         */
        return userDetailsRepository.findByEmail(email)
                .timeout(Duration.ofMillis(500))
                .onErrorResume(e -> cachedEntities()) //fallback to a cache in case it reaches the timeout
                .map(UserDetailsEntity::toDomain);
    }

    private Mono<UserDetailsEntity> cachedEntities() {
        return Mono.just(new UserDetailsEntity(UUID.randomUUID().toString(), "", "", "", ""));
    }
}

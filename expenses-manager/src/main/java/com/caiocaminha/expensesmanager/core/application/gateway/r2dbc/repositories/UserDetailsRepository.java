package com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.repositories;

import com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.entities.UserDetailsEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Repository
public interface UserDetailsRepository extends ReactiveCrudRepository<UserDetailsEntity, String> {
    
    Mono<UserDetailsEntity> findByEmail(String email);


    @Query(
            """
                   INSERT INTO user_details (id, first_name, last_name, email, username, created_at, updated_at) 
                   VALUES (:id, :firstName, :lastName, :email, :username, :createdAt, :updatedAt)
                   ON CONFLICT (email)
                   DO UPDATE SET
                        first_name = EXCLUDED.first_name,
                        last_name = EXCLUDED.last_name,
                        username = EXCLUDED.username,
                        updated_at = NOW()::TIMESTAMP
                   RETURNING *
                    """
    )
    Mono<UserDetailsEntity> upsert(
            String id,
            String firstName,
            String lastName,
            String email,
            String username,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    );

}

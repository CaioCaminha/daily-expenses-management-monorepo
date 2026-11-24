package com.caiocaminha.expensesmanager.core.domain.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserDetails(
        UUID id,
        String firstName,
        String lastName,
        String username,
        String email,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonIgnore
        LocalDateTime createdAt,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonIgnore
        LocalDateTime updatedAt
) {

    public UserDetails(
            String firstName,
            String lastName,
            String username,
            String email
    ) {
        this(
                UUID.randomUUID(),
                firstName,
                lastName,
                username,
                email,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

}

package com.caiocaminha.expensesmanager.core.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDetailsRequest(
        String firstName,
        String lastName,
        String username,
        String email
) {

    public UserDetails toDomain() {
        return new UserDetails(
                this.firstName,
                this.lastName,
                this.username,
                this.email
        );
    }

}

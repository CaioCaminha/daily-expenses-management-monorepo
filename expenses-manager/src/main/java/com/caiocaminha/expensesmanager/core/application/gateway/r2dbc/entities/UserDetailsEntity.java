package com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.entities;

import com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.utils.PersistableEntity;
import com.caiocaminha.expensesmanager.core.domain.user.UserDetails;
import com.caiocaminha.expensesmanager.core.domain.user.UserDetailsRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(UserDetailsEntity.TABLE_NAME)
@AllArgsConstructor
public class UserDetailsEntity extends PersistableEntity<String> {
    @Id
    private String id;
    @Column("first_name")
    private String firstName;
    @Column("username")
    private String username;
    @Column("last_name")
    private String lastName;
    @Column("email")
    private String email;

    public final static String TABLE_NAME = "user_details";


    public static UserDetailsEntity of(
            UUID id,
            String firstName,
            String username,
            String lastName,
            String email
    ) {
        return new UserDetailsEntity(
                id.toString(),
                username,
                firstName,
                lastName,
                email
        );
    }

    public static UserDetailsEntity from(UserDetailsRequest request) {
        return UserDetailsEntity.of(
                UUID.randomUUID(),
                request.firstName(),
                request.username(),
                request.lastName(),
                request.email()
        );
    }

    public UserDetails toDomain() {
        return new UserDetails(
                UUID.fromString(this.id),
                this.firstName,
                this.lastName,
                this.username,
                this.email,
                this.createdAt,
                this.updatedAt
        );
    }

    public UserDetailsEntity toUpdated() {
        this.updated();
        return this;
    }


    @Override
    public String getId() {
        return id;
    }
    public String firstName() {
        return this.firstName;
    }
    public String lastName() {
        return this.lastName;
    }
    public String email() {
        return this.email;
    }
    public String username() { return this.username; }

}

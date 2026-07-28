package com.caiocaminha.expensesmanager.core.application.gateway.r2dbc.repositories;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.util.Streamable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Deprecated(since = "Just for example from Spring Documentation")
@NoRepositoryBean //So Spring doesn't try to createa a Bean from this and fails due to generics
public interface BaseRepositoryExample <T, ID> extends ReactiveCrudRepository<T, ID> {

    default Flux<T> fetchingSomeData() {
        return findAll();
    }

    /**
       You can manually define traversal points, specifying how to split the name when spring is finding
     the properties
     */
    Flux<T> findByAddress_ZipCode(String zipcode);

    /**
     * Can use Streamable instead of Iterable
     * @param name
     * @return
     */
    Streamable<T> findByName(String name);


    /**
     *
     * Useful for resolving ambiguity between property names
     * Person -> Address -> ZipCode
     * but Person also has: Person -> AddressZip field
     * the traversal point floats from right to left, so it would try to match a addressZip first
     */
    Mono<T> findByAddressZipCode();

}



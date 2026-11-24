# R2DBC Postgres Implementation
## Decisions

### General
It was decided to use Standard Spring Data reactive library r2dbc (Reactive Relational Database Connectivity) with
PostgreSQL. Postgres uses MVCC (Multi-versioned Concurrency Control) to handle concurrency control, basically each transaction
has it's own version of the data, similar to a snapshot. This offers better performance than traditional locks, widely used
on pessimistic locking database mechanisms. Postgres also offers great documentation about it's locking types, 
[concurrency control](https://www.postgresql.org/docs/18/mvcc.html) which is very helpful to understand how postgres reacts
to different transaction isolation levels. 

To avoid the overhead added by using reflection, Spring Data uses a Factory class created at runtime which will
call the domain class constructor directly, spring recommends to stick to immutable objects, which materialization is just
a matter of calling it's constructor; Constructor only materialization is up to 30% faster than properties population.
Spring also recommends to provide an all-args constructor ([provided on entities using Lombok @AllArgsConstructor](https://github.com/CaioCaminha/java-daily-expenses-management/blob/main/src/main/java/com/caiocaminha/javadailyexpenses/core/application/gateway/r2dbc/entities/UserDetailsEntity.java#L15)) to skip
property population for optimal performance. Spring also states that it's an established pattern to rather use static factory
methods to expose these variants of all-args constructor

### Transaction Management
The idea is to have all the logic happening under a single transaction, from receiving the request on the proper
controller to actually saving on the database and returning a response.

My idea was to avoid adding @Transactional annotations everywhere in the code.
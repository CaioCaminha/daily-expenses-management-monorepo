# Java Daily-Expenses Orchestrator


## Performance Evaluation
Evaluate performance of this application and create reports to be compared with a similar application:
- Written in another cloud native framework such as Micronaut or Quarkus
- Written in Kotlin and Spring
- Using a NoSQL database client
- Using undertow as Servlet client/server

### \Action Items:

- Finish Postgres R2DBC implementation
- Implement Spring Transaction Manager ( Study Spring Framework transaction management )
  - Create a before advice that wraps every handler invocation with a transactionProvider.withTransaction call
  - Or Simply create an abstraction for every handler to implement and call transactionProvider.withTransaction
    - That's simpler than adding @Transactional to several methods
- Finish OpenAI WebClient implementation
  - Spring Boot 4 might have changed this WebFlux configuration
- Implement Controller - WebFlux Functional Web Framework
  - Understand how to receive a PartEvent to receive a MultiPartFile (CSV statement)
- Implement DataExtractors / Serialization Logic - Domain DataExtractorPort
  - CSV | JSON | PDF
- Implement User Authentication / Authorization
  - Spring Security or Spring Cloud Gateway - Or leverage for some Cloud product like aws cognito
- Implement Kafka layer to communicate with kotlin-analytics service
  - Study Kafka fundamentals and how to configure kafka on Spring
  - Study outbox patterns applied to kafka
- Configure TestContainers
  - Postgres Container
  - Kafka container
- Implement Unit and Integration tests


## Transaction API
| CSV / PDF Input                                                                                | JSON Input                                                               | WebHook Whatsapp API                                   |
|------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|--------------------------------------------------------|
| Implement OpenAI Gateway to get Categories based on Transaction's description                  | Create RequestHandler and UseCase to receive TransactionDetails directly | To be Defined how to configure WebHook to Whatsapp API |
| Implement RequestHandler and UseCase to receive MultiPartFile or similar                       | Save details on mongoDB                                                  | ------                                                 |
| Implement MongoDB Gateway and save transactionDetails                                          | -------                                                                  | ------                                                 |
| Maybe export to GoogleWorkspace Spreadsheet once a month with a detailed statement of expenses | -------                                                                  | ------                                                 |

### Tech Debts / Improvements:
- Replace Functional Web Client for Undertow - Performance Improvement.
    - Evaluate if it's actually a performance improvement

### Topics to study:

- Spring IoC Container and Beans - MISSING ARTICLE
- [Spring's Task Executor and @Async with virtual threads](https://docs.spring.io/spring-framework/reference/integration/scheduling.html#scheduling-task-executor-types)  - DONE
- [Emmbracing Virtual Threads - Spring](https://spring.io/blog/2022/10/11/embracing-virtual-threads) - DONE
- [Java Servlet Technology](https://docs.oracle.com/javaee/5/tutorial/doc/bnafd.html)
- Project Reactor + R2DBC
- Spring Data and Spring Data Mongo
- WebClient / WebFlux Configuration and core concepts
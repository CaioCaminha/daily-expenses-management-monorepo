package com.caiocaminha.expensesmanager.core.application;

import com.caminha.kafkautils.KafkaConfig;
import com.caminha.postgresutils.utils.config.R2DBCConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcTransactionManagerAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = {
        R2dbcAutoConfiguration.class,
        R2dbcTransactionManagerAutoConfiguration.class
})
@Import(
        {
                KafkaConfig.class,
                R2DBCConfiguration.class
        }
)
@EnableAsync
public class ExpensesManagerApplication {

    public static void main(String[] args) {
//        Undertow server = Undertow.builder()
//                .addHttpListener(8080, "localhost")
//                .setHandler(
//                        new HttpHandler() {
//                            @Override
//                            public void handleRequest(HttpServerExchange httpServerExchange) throws Exception {
//                                httpServerExchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
//                                httpServerExchange.getResponseSender().send("Hello World");
//                            }
//                        }
//                ).build();
//        server.start();

        System.out.println("this version is actually updated");
        SpringApplication.run(ExpensesManagerApplication.class, args);
    }

}

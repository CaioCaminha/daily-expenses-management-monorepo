package com.caminha.postgresutils.utils.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConditionalOnProperty(prefix = "db")
@ConfigurationProperties(prefix = "db")
public class R2DBCProperties {

    String name, user, password, url;
    Server server;

    @Data
    public static class Server {
        String host;
        Integer port;
    }

}


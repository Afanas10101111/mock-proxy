package com.github.afanas10101111.mp.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static org.h2.tools.Server.createWebServer;

@Slf4j
@Setter
@Component
@ConfigurationProperties("h2-console")
public class H2Server {
    private int port;
    private Server server;

    @EventListener(ContextRefreshedEvent.class)
    public void start() throws java.sql.SQLException {
        this.server = createWebServer("-webPort", String.valueOf(port), "-tcpAllowOthers").start();
        log.info("H2 console started on port " + port);
    }

    @EventListener(ContextClosedEvent.class)
    public void stop() {
        this.server.stop();
        log.info("H2 console stopped on port " + port);
    }
}

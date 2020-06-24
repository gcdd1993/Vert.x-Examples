package io.github.gcdd1993.vertxdemo.core;

import io.github.gcdd1993.vertxdemo.core.tcp.VertxTcpClient;
import io.github.gcdd1993.vertxdemo.core.tcp.VertxTcpServer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.junit.jupiter.api.Test;

public class VertxTcpTest {

    @Test
    public void deploy() {
        var vertx = Vertx.vertx();
        var options = new DeploymentOptions()
                .setWorkerPoolSize(4);
        vertx.deployVerticle(VertxTcpServer.class, options);
        vertx.deployVerticle(VertxTcpClient.class, options);
        while (true) {
            // blocking
        }
    }

}

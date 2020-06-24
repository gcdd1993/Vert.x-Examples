package io.github.gcdd1993.vertxdemo.core.tcp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;

/**
 * Created by gaochen on 2020/6/24.
 */
public class VertxTcpClient extends AbstractVerticle {

    private NetClient client;

    @Override
    public void start() throws Exception {
        var clientOptions = new NetClientOptions()
                .setConnectTimeout(10000)
                .setReconnectAttempts(10)
                .setReconnectInterval(500)
                // 日志的记录是由Netty而不是Vert.x的日志来执行
                // 这个功能不能用于生产环境
                .setLogActivity(true);
        client = vertx.createNetClient(clientOptions);
        client.connect(4321, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected!");
                var socket = res.result();
                socket.handler(buffer -> {
                    System.out.println("I received some bytes: " + buffer.length());
                });

                vertx.setPeriodic(1000, id -> {
                    socket.write(Buffer.buffer("some"));
                });

            } else {
                System.out.println("Failed to connect: " + res.cause().getMessage());
            }
        });

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}

package io.github.gcdd1993.vertxdemo.core.tcp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

/**
 * Created by gaochen on 2020/6/24.
 */
public final class VertxTcpServer extends AbstractVerticle {

    private NetServer server;

    @Override
    public void start() throws Exception {
        var options = new NetServerOptions()
                .setPort(4321);
        server = vertx.createNetServer(options);

        server.connectHandler(socket -> {
            socket.handler(buffer -> {
                System.out.println("I received some bytes: " + buffer.length());
                // 仅回传数据
                socket.write(buffer);
            });
            socket.exceptionHandler(throwable -> {
                System.out.println("I received some error: " + throwable.getCause().getMessage());
            });
            socket.closeHandler(v -> {
                System.out.println("The socket has been closed");
            });
        });

        server.listen(4321, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Server is now listening on actual port: " + server.actualPort());
            } else {
                System.out.println("Failed to bind!");
            }
        });

    }

    @Override
    public void stop() throws Exception {
        server.close(res -> {
            if (res.succeeded()) {
                System.out.println("Server is now closed");
            } else {
                System.out.println("close failed");
            }
        });
    }
}

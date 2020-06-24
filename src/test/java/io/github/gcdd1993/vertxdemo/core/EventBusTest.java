package io.github.gcdd1993.vertxdemo.core;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBusOptions;
import io.vertx.core.http.ClientAuth;
import io.vertx.core.net.JksOptions;
import org.junit.jupiter.api.Test;

/**
 * Vert.x :: EventBus
 */
public class EventBusTest {

    /**
     * 注册处理器
     */
    @Test
    public void register_eventBus() {
        var vertx = Vertx.vertx();
        var eventBus = vertx.eventBus();

        eventBus.consumer("news.uk.sport", message -> {
            System.out.println("I have received a message: " + message.body());
        });

        // 也可以不设置处理器而使用 consumer 方法直接返回一个 MessageConsumer，之后再来设置处理器。
        var consumer = eventBus.consumer("news.uk.sport");
        consumer.handler(message -> {
            System.out.println("I have received a message: " + message.body());
        });

    }

    /**
     * 注销处理器
     */
    @Test
    public void unregister_eventBus() {
        var vertx = Vertx.vertx();
        var eventBus = vertx.eventBus();

        var consumer = eventBus.consumer("news.uk.sport");
        consumer.handler(message -> {
            System.out.println("I have received a message: " + message.body());
        });
        // unregister
        consumer.unregister(res -> {
            if (res.succeeded()) {
                System.out.println("The handler un-registration has reached all nodes");
            } else {
                System.out.println("Un-registration failed!");
            }
        });
    }

    /**
     * 发布消息
     * <p>
     * 这个消息将会传递给所有在地址 news.uk.sport 上注册过的处理器。
     */
    @Test
    public void publish_message() {
        var vertx = Vertx.vertx();
        var eventBus = vertx.eventBus();

        var consumer = eventBus.consumer("news.uk.sport");
        consumer.handler(message -> {
            System.out.println("I have received a message: " + message.body());
        });

        // 这个消息将会传递给所有在地址 news.uk.sport 上注册过的处理器。
        eventBus.publish("news.uk.sport", "Yay! Someone kicked a ball");

    }

    /**
     * 发送消息
     * <p>
     * 与发布消息的不同之处在于，发送(send)的消息只会传递给在该地址注册的其中一个处理器，这就是点对点模式。
     * Vert.x 使用不严格的轮询算法来选择绑定的处理器。
     */
    @Test
    public void send_message() {
        var vertx = Vertx.vertx();
        var eventBus = vertx.eventBus();

        var consumer = eventBus.consumer("news.uk.sport");
        consumer.handler(message -> {
            System.out.println("I have received a message: " + message.body());
        });

        // 这个消息将会传递给所有在地址 news.uk.sport 上注册过的处理器。
        eventBus.send("news.uk.sport", "Yay! Someone kicked a ball");
    }

    /**
     * 设置消息头
     */
    @Test
    public void send_message_withHeader() {
        var vertx = Vertx.vertx();
        var eventBus = vertx.eventBus();

        var consumer = eventBus.consumer("news.uk.sport");
        consumer.handler(message -> {
            System.out.println("I have received a message: " + message.body());
            System.out.println("Headers ");
            message.headers()
                    .forEach(header -> System.out.println(header.getKey() + ": " + header.getValue()));
        });

        var deliveryOptions = new DeliveryOptions();
        deliveryOptions.addHeader("some-header", "some-value");
        eventBus.send("news.uk.sport", "Yay! Someone kicked a ball", deliveryOptions);

        while (true) {
            // blocked
        }
    }

    /**
     * 应答消息/发送回复
     * <p>
     * 消息确认
     */
    @Test
    public void reply_message() {
        var vertx = Vertx.vertx();
        var eventBus = vertx.eventBus();

        var consumer = eventBus.consumer("news.uk.sport");
        consumer.handler(message -> {
            System.out.println("I have received a message: " + message.body());
            message.reply("how interesting!");
        });

        eventBus.request("news.uk.sport", "Yay! Someone kicked a ball across a patch of grass", ar -> {
            if (ar.succeeded()) {
                System.out.println("Received reply: " + ar.result().body());
            }
        });

        while (true) {
            // blocked
        }
    }

    @Test
    public void send_message_withTimeout() {
        var vertx = Vertx.vertx();
        var eventBus = vertx.eventBus();

        var consumer = eventBus.consumer("news.uk.sport");
        consumer.handler(message -> {
            System.out.println("I have received a message: " + message.body());
        });

        var deliveryOptions = new DeliveryOptions();
        deliveryOptions.setSendTimeout(1000);
        eventBus.request("news.uk.sport", "Yay! Someone kicked a ball across a patch of grass", ar -> {
            if (ar.succeeded()) {
                System.out.println("Received reply: " + ar.result().body());
            } else {
                System.out.println("Message has no reply");
            }
        });

        while (true) {
            // blocked
        }
    }

    @Test
    public void eventBus_config() {
        var vertxOptions = new VertxOptions();
        var keyStoreOptions = new JksOptions()
                .setPath("keystore.jks")
                .setPassword("wibble");

        var eventBusOptions = new EventBusOptions()
                .setSsl(true)
                .setKeyStoreOptions(keyStoreOptions)
                .setTrustStoreOptions(keyStoreOptions)
                .setClientAuth(ClientAuth.REQUIRED);

        vertxOptions.setEventBusOptions(eventBusOptions);
        Vertx.clusteredVertx(vertxOptions, res -> {
            if (res.succeeded()) {
                var vertx = res.result();
                var eventBus = vertx.eventBus();
                System.out.println("We now have a clustered event bus: " + eventBus);
            } else {
                System.out.println("Failed: " + res.cause());
            }
        });
    }

}

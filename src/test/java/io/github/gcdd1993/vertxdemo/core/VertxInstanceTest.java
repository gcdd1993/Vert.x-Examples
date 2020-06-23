package io.github.gcdd1993.vertxdemo.core;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.spi.cluster.ClusterManager;
import org.junit.jupiter.api.Test;

/**
 * Vert.x :: Instance
 */
public class VertxInstanceTest {

    /**
     * 创建 Vertx 对象时指定配置项
     */
    @Test
    public void single_instance() {
        var vertx = Vertx.vertx();

        var options = new VertxOptions()
                .setWorkerPoolSize(40);
        vertx = Vertx.vertx(options);
    }

    /**
     * 创建集群模式的 Vert.x 对象
     * <p>
     * 必须实现{@link ClusterManager}，否则跑出异常：No ClusterManagerFactory instances found on classpath
     */
    @Test
    public void cluster_instance() {
        var options = new VertxOptions();
        Vertx.clusteredVertx(options,
                res -> {
                    if (res.succeeded()) {
                        Vertx vertx = res.result(); // 获取到了集群模式下的 Vertx 对象
                        // 做一些其他的事情
                    } else {
                        // 获取失败，可能是集群管理器出现了问题
                        System.out.println("something wrong.");
                    }
                });
    }

    /**
     * 流式API
     */
    @Test
    public void fluent_api() {
        var vertx = Vertx.vertx();
        vertx.setPeriodic(1000, id -> {
            // 这个处理器将会每隔一秒被调用一次
            System.out.println("timer fired!");
        });
        while (true) {
            // blocked
        }
    }

    /**
     * 运行阻塞式代码
     * <p>
     * 使用executeBlocking
     * <p>
     * 默认情况下，如果 executeBlocking 在同一个上下文环境中（如：同一个 Verticle 实例）被调用了多次，那么这些不同的 executeBlocking 代码块会 顺序执行（一个接一个）。
     * 若您不需要关心您调用 executeBlocking 的顺序，可以将 ordered 参数的值设为 false。这样任何 executeBlocking 都会在 Worker Pool 中并行执行。
     */
    @Test
    public void blockMethod_useExecuteBlocking() {
        var vertx = Vertx.vertx();
        vertx.executeBlocking(future -> {
            // 调用一些需要耗费显著执行时间返回结果的阻塞式API
            String result = _blockingMethod();
            future.complete(result);
        }, res -> {
            System.out.println("The result is: " + res.result());
        });
        while (true) {
            // blocked
        }
    }

    /**
     * 运行阻塞式代码
     * <p>
     * 可以为不同的用途创建不同的池
     */
    @Test
    public void blockMethod_multiWorkers() {
        var vertx = Vertx.vertx();
        var poolSize = 10;
        var maxExecuteTime = 120000;
        var executor = vertx.createSharedWorkerExecutor("my-worker-pool", poolSize, maxExecuteTime);
        executor.executeBlocking(future -> {
            // 调用一些需要耗费显著执行时间返回结果的阻塞式API
            String result = _blockingMethod();
            future.complete(result);
        }, res -> {
            System.out.println("The result is: " + res.result());
        });
        while (true) {
            // blocked
        }
        // executor.close();
    }

    /**
     * 并发合并
     */
    @Test
    public void concurrency_compositeFutureAll() {
//        Promise<HttpServer> httpServerFuture = Promise.promise();
//        httpServer.listen(httpServerFuture.completer());
//
//        Future<NetServer> netServerFuture = Future.future();
//        netServer.listen(netServerFuture.completer());
//
//        CompositeFuture.all(httpServerFuture, netServerFuture).setHandler(ar -> {
//            if (ar.succeeded()) {
//                // 所有服务器启动完成
//            } else {
//                // 有一个服务器启动失败
//            }
//        });

    }

    /**
     * 顺序合并
     * <p>
     * 这里例子中，有三个操作被串起来了：
     * <p>
     * 1、一个文件被创建（fut1）
     * 2、一些东西被写入到文件（fut2）
     * 3、文件被移走（startFuture）
     * 如果这三个步骤全部成功，则最终的 Future（startFuture）会是成功的；其中任何一步失败，则最终 Future 就是失败的。
     */
    @Test
    public void sequence_compositeFutureAll() {
        var vertx = Vertx.vertx();
        var fs = vertx.fileSystem();
        var startFuture = Future.<Void>future();

        var fut1 = Future.<Void>future();
        fs.createFile("/foo", fut1.completer());

        fut1.compose(v -> {
            // fut1中文件创建完成后执行
            var fut2 = Future.<Void>future();
            fs.writeFile("/foo", Buffer.buffer(), fut2.completer());
            return fut2;
        }).compose(v -> {
                    // fut2文件写入完成后执行
                    fs.move("/foo", "/bar", startFuture.completer());
                },
                // 如果任何一步失败，将startFuture标记成failed
                startFuture);
    }

    /**
     * 模拟耗时I/O操作
     */
    private String _blockingMethod() {
        for (int i = 0; i < 10; i++) {
            System.out.println(10 - i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "finished.";
    }


}

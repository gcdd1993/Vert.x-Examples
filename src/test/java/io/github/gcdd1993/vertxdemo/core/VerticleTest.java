package io.github.gcdd1993.vertxdemo.core;

import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;

/**
 * Vert.x :: Verticle
 */
public class VerticleTest {

    /**
     * Stardand Verticle：这是最常用的一类 Verticle —— 它们永远运行在 Event Loop 线程上。稍后的章节我们会讨论更多。
     * <p>
     * 线程安全
     */
    @Test
    public void standard_verticle() {
        var vertx = Vertx.vertx();
    }

    /**
     * Worker Verticle：这类 Verticle 会运行在 Worker Pool 中的线程上。一个实例绝对不会被多个线程同时执行。
     */
    @Test
    public void worker_verticle() throws InterruptedException {
        var vertx = Vertx.vertx();
        var options = new DeploymentOptions()
                .setWorker(true);
        vertx.deployVerticle(MyVerticle.class, options,
                res -> {
                    if (res.succeeded()) {
                        System.out.println("Deployment id is: " + res.result());
                        var deploymentId = res.result();
                        // undeployed
                        vertx.undeploy(deploymentId, res1 -> {
                            if (res1.succeeded()) {
                                System.out.println("Undeployed ok");
                            } else {
                                System.out.println("Undeployed failed!");
                            }
                        });
                    } else {
                        System.out.println("Deployment failed!");
                    }
                });
        Thread.sleep(3000);
    }

    /**
     * 设置Verticle实例数
     */
    @Test
    public void verticle_multi_instances() {
        var vertx = Vertx.vertx();
        var options = new DeploymentOptions()
                .setInstances(16);
        vertx.deployVerticle(MyVerticle.class, options);
        while (true) {
            // blocked
        }
    }

    /**
     * 向 Verticle 传入配置
     */
    @Test
    public void verticle_config() {
        // json
        var jsonObj = new JsonObject()
                .put("name", "tim")
                .put("directory", "/blah");
        var options = new DeploymentOptions()
                .setConfig(jsonObj);

    }

    /**
     * Context 对象
     * <p>
     * 通常来说这个 Context 会是一个 Event Loop Context，它绑定到了一个特定的 Event Loop 线程上。所以在该 Context 上执行的操作总是在同一个 Event Loop 线程中。
     * 对于运行内联的阻塞代码的 Worker Verticle 来说，会关联一个 Worker Context，并且所有的操作运都会运行在 Worker 线程池的线程上。
     */
    @Test
    public void context() {
        var vertx = Vertx.vertx();
        var context = vertx.getOrCreateContext();
        if (context.isEventLoopContext()) {
            System.out.println("Context attached to Event Loop");
        } else if (context.isWorkerContext()) {
            System.out.println("Context attached to Worker Thread");
        } else if (context.isMultiThreadedWorkerContext()) {
            System.out.println("Context attached to Worker Thread - multi threaded worker");
        } else if (!Context.isOnVertxThread()) {
            System.out.println("Context not attached to a thread managed by vert.x");
        }
    }

    /**
     * 可以在Context上异步执行代码
     */
    @Test
    public void run_async_on_context() {
        var vertx = Vertx.vertx();
        var context = vertx.getOrCreateContext();

        // Context 对象提供了存储和读取共享数据的方法。
        context.put("data", "hello");
        context.runOnContext(v -> {
            System.out.println("This will be executed asynchronously in the same context");

            String hello = context.get("data");
            System.out.println(hello);
        });
    }

    /**
     * 执行周期性/延迟性操作
     */
    @Test
    public void timer() throws InterruptedException {
        var vertx = Vertx.vertx();
        // 一次性计时器
        var timerId = vertx.setTimer(1000, id -> {
            System.out.println("And one second later this is printed");
        });
        System.out.println("First this is printed");

        Thread.sleep(3000);

        // 返回值是一个唯一的计时器id，该id可用于之后取消该计时器，这个计时器id会传入给处理器。
        vertx.cancelTimer(timerId);

        // 周期性计时器
        var periodicTimerId = vertx.setPeriodic(1000, id -> {
            System.out.println("And every second this is printed");
        });

        Thread.sleep(10000);

        // 返回值是一个唯一的计时器id，该id可用于之后取消该计时器，这个计时器id会传入给处理器。
        vertx.cancelTimer(periodicTimerId);
    }

    /**
     * Verticle Worker Pool
     * <p>
     * 设置Worker Pool的名称
     */
    @Test
    public void workerPool_name() {
        var vertx = Vertx.vertx();
        var options = new DeploymentOptions()
                .setWorkerPoolName("the-specific-pool");
        vertx.deployVerticle(MyVerticle.class, options);
    }


}

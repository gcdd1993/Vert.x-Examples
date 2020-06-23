package io.github.gcdd1993.vertxdemo.core;

import io.vertx.core.AbstractVerticle;

/**
 * Created by gaochen on 2020/6/23.
 */
public class MyVerticle extends AbstractVerticle {

    // Verticle部署时调用
    @Override
    public void start() throws Exception {
        System.out.println("[" + Thread.currentThread().getName() + "] " + getClass().getSimpleName() + " start");
        super.start();
    }

    // 可选 - Verticle撤销时调用
    @Override
    public void stop() throws Exception {
        System.out.println("[" + Thread.currentThread().getName() + "] " + getClass().getSimpleName() + " stop");
        super.stop();
    }
}

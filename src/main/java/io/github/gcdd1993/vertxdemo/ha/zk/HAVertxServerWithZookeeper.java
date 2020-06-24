package io.github.gcdd1993.vertxdemo.ha.zk;

import io.github.gcdd1993.vertxdemo.core.tcp.VertxTcpClient;
import io.github.gcdd1993.vertxdemo.core.tcp.VertxTcpServer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;

/**
 * Created by gaochen on 2020/6/24.
 */
public class HAVertxServerWithZookeeper {

    public static void main(String[] args) {
        var options = new VertxOptions()
                .setClusterManager(getClusterManager());

        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                var deploymentOptions = new DeploymentOptions()
                        .setHa(true)
                        .setInstances(1)
                        .setWorkerPoolSize(4);
                vertx.deployVerticle(VertxTcpServer.class, deploymentOptions);
                vertx.deployVerticle(VertxTcpClient.class, deploymentOptions);
            } else {
                // failed!
            }
        });
        while (true) {
            // blocking
        }
    }

    private static ClusterManager getClusterManager() {
        var zkConfig = new JsonObject();
        zkConfig.put("zookeeperHosts", "localhost");
        zkConfig.put("rootPath", "io.vertx");
        zkConfig.put("retry", new JsonObject()
                .put("initialSleepTime", 3000)
                .put("maxTimes", 3));
        return new ZookeeperClusterManager(zkConfig);
    }
}

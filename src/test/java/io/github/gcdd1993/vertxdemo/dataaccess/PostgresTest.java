package io.github.gcdd1993.vertxdemo.dataaccess;

import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import org.junit.jupiter.api.Test;

/**
 * Created by gaochen on 2020/6/24.
 */
public class PostgresTest {

    @Test
    public void pg_connect() {
        var port = 5432;
        var host = "10.9.108.139";
        var database = "dbpe";
        var user = "maxtropy";
        var password = "maxtropy";
        var connectOptions = new PgConnectOptions()
                .setPort(port)
                .setHost(host)
                .setDatabase(database)
                .setUser(user)
                .setPassword(password);

        var poolOptions = new PoolOptions()
                .setMaxSize(5);

        var client = PgPool.pool(connectOptions, poolOptions);
        client
                .query("SELECT * FROM alarm_log")
                .execute(ar -> {
                    if (ar.succeeded()) {
                        var result = ar.result();
                        System.out.println("Got " + result.size() + " rows ");
                    } else {
                        System.out.println("Failure: " + ar.cause().getMessage());
                    }

                    // Now close the pool
                    client.close();
                });

        while (true) {
            // bloking
        }
    }
}

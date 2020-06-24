package io.github.gcdd1993.vertxdemo.rx;

import io.vertx.reactivex.core.Vertx;
import io.vertx.core.file.OpenOptions;
import org.junit.jupiter.api.Test;

/**
 * Created by gaochen on 2020/6/24.
 */
public class VertxRxStreamTest {

    @Test
    public void readStream() {
        var vertx = Vertx.vertx();
        var fileSystem = vertx.fileSystem();
//        fileSystem.open("C:\\Users\\13983\\Desktop\\cas.log", new OpenOptions(), result -> {
//            var file = result.result();
//            var observable = FlowableHelper.toFlowable(file);
//            observable.forEach(data -> System.out.println("Read data: " + data.toString("UTF-8")));
//        });
        fileSystem.open("C:\\Users\\13983\\Desktop\\cas.log", new OpenOptions(), result -> {
            var file = result.result();
            var observable = file.toObservable();
            observable.forEach(data -> System.out.println("Read data: " + data.toString("UTF-8")));
        });
        while (true) {
            // bloking
        }
    }

}

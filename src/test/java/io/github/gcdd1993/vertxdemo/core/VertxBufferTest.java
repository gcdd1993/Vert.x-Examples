package io.github.gcdd1993.vertxdemo.core;

import io.vertx.core.buffer.Buffer;
import org.junit.jupiter.api.Test;

/**
 * 在 Vert.x 内部，大部分数据被重新组织（shuffle，表意为洗牌）成  Buffer  格式。
 * 一个Buffer是可以读取或写入的0个或多个字节序列，并且根据需要可以自动扩容、将任意字节写入Buffer 。
 * 您也可以将Buffer想象成字节数组（译者注：类似于JDK中的ByteBuffer）。
 */
public class VertxBufferTest {

    @Test
    public void buffer_create() {
        var emptyBuffer = Buffer.buffer();
        var emptyBufferWithLength = Buffer.buffer(1000);

        var stringBuffer = Buffer.buffer("some thing");

        var stringBufferWithEncoding = Buffer.buffer("some thing", "UTF-8");

        var bytes = new byte[]{1, 3, 5};
        var byteBuffer = Buffer.buffer(bytes);
    }

    @Test
    public void buffer_write() {
        var emptyBufferWithLength = Buffer.buffer(1000);
        // 追加到Buffer
        emptyBufferWithLength
                .appendInt(123)
                .appendString("hello\\n");

        // 随机访问写Buffer
        emptyBufferWithLength
                .setInt(1000, 123)
                .setString(0, "hello");
    }

    @Test
    public void buffer_read() {
        var stringBuffer = Buffer.buffer("some thing");

        for (int i = 0; i < stringBuffer.length(); i += 4) {
            System.out.println("int value at " + i + " is " + stringBuffer.getInt(i));
        }
    }
}

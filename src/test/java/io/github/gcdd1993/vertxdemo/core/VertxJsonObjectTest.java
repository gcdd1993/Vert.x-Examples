package io.github.gcdd1993.vertxdemo.core;

import io.vertx.core.json.JsonObject;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class VertxJsonObjectTest {

    @Test
    public void jsonObject_from_string() {
        var jsonString = "{\"foo\":\"bar\"}";
        var jsonObject = new JsonObject(jsonString);

        Assertions.assertEquals(jsonObject.getString("foo"), "bar");
    }

    @Test
    public void jsonObject_from_map() {
        var map = new HashMap<String, Object>();
        map.put("foo", "bar");
        map.put("xyz", 3);

        var jsonObject = new JsonObject(map);

        Assertions.assertEquals(jsonObject.getString("foo"), "bar");
        Assertions.assertEquals(jsonObject.getInteger("xyz"), 3);

    }

    @Test
    public void jsonObject_put() {
        var jsonObject = new JsonObject();
        jsonObject.put("foo", "bar")
                .put("num", 123)
                .put("mybool", true);

        Assertions.assertEquals(jsonObject.getString("foo"), "bar");
        Assertions.assertEquals(jsonObject.getInteger("num"), 123);
        Assertions.assertEquals(jsonObject.getBoolean("mybool"), true);
    }

    @Test
    public void jsonObject_from_pojo() {
        var jsonObject = new JsonObject();
        jsonObject.put("name", "小王")
                .put("age", 24);

        var person = jsonObject.mapTo(Person.class);
        Assertions.assertEquals(person.getName(), "小王");
        Assertions.assertEquals(person.getAge(), 24);
    }

    @Data
    public static class Person {
        private String name;
        private int age;
    }

}

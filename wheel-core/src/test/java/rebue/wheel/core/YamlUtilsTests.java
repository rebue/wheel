package rebue.wheel.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class YamlUtilsTests {

    @Test
    public void test01() throws IOException {
        final String content = new String(Files.readAllBytes(Paths.get(YamlUtilsTests.class.getResource("/").getPath() + "/application-prod.yml")));
        getAsString(content, "spring");
        getAsString(content, "spring.redis.password");
        getAsString(content, "spring.sleuth");
        getAsString(content, "spring.sleuth.sampler.probability");
        getAsString(content, "orp");
        getAsString(content, "spring.zipkin");
        getAsString(content, "spring.zipkin.base-url");
        getAsStringList(content, "spring.redis.sentinel.nodes");
        getAsMapList(content, "orp.strategies.ding-talk.clients");
        log.info("-------------------------------------------");
        setAsString(content, "spring.aaa", "AAA");
        setAsString(content, "spring.bbb.bbb", "BBB");
        setAsString(content, "ccc", "CCC");
        setAsString(content, "ddd.ddd", "DDD");
        setAsString(content, "eee.eee.eee", "EEE");
        log.info("-------------------------------------------");
        final List<String> stringList = new LinkedList<>();
        stringList.add("aaaaa:aaa");
        stringList.add("bbbbb:bbb");
        stringList.add("ccccc:ccc");
        stringList.add("ddddd:ddd");
        stringList.add("eeeee:eee");
        setAsStringList(content, "spring.redis.sentinel.nodes", stringList);
        log.info("-------------------------------------------");
        final List<Map<String, String>> mapList = new LinkedList<>();
        Map<String, String>             map     = new LinkedHashMap<>();
        map.put("id", "id-aaa");
        map.put("secret", "secret-aaa");
        mapList.add(map);
        map = new LinkedHashMap<>();
        map.put("id", "id-bbb");
        map.put("secret", "secret-bbb");
        mapList.add(map);
        map = new LinkedHashMap<>();
        map.put("id", "id-ccc");
        map.put("secret", "secret-ccc");
        mapList.add(map);
        setAsMapList(content, "orp.strategies.ding-talk.clients", mapList);
    }

    private void getAsString(final String content, final String key) {
        log.info("{}: {}", key, YamlUtils.getAsString(content, key));
    }

    private void getAsStringList(final String content, final String key) {
        log.info("{}: {}", key, YamlUtils.getAsStringList(content, key));
    }

    private void getAsMapList(final String content, final String key) {
        log.info("{}: {}", key, YamlUtils.getAsMapList(content, key));
    }

    private void setAsString(final String content, final String key, final String value) {
        log.info("{}: {}", key, YamlUtils.setAsString(content, key, value));
    }

    private void setAsStringList(final String content, final String key, final List<String> value) {
        log.info("{}: {}", key, YamlUtils.setAsStringList(content, key, value));
    }

    private void setAsMapList(final String content, final String key, final List<Map<String, String>> value) {
        log.info("{}: {}", key, YamlUtils.setAsMapList(content, key, value));
    }
}

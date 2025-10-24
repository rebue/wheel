package rebue.wheel.kafka.httpsink;

import static rebue.wheel.kafka.httpsink.HttpSinkCst.PLUGIN_NAME;
import static rebue.wheel.kafka.httpsink.HttpSinkCst.PLUGIN_VERSION;
import static rebue.wheel.kafka.httpsink.config.HttpSinkConfig.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import lombok.extern.slf4j.Slf4j;
import rebue.wheel.kafka.httpsink.config.HttpSinkConfig;

@Slf4j
public class HttpSinkTask extends SinkTask {
    private HttpClient httpClient;
    private URI        httpUrl;
    private String     contentType;

    @Override
    public String version() {
        return PLUGIN_VERSION;
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("starting task: {}\n{}", PLUGIN_NAME, props);
        HttpSinkConfig     config      = new HttpSinkConfig(props);
        // 获取 HTTP 版本
        HttpClient.Version httpVersion = HttpClient.Version.valueOf(config.getString(CONFIG_KEY_HTTP_VERSION));
        // 创建 HttpClient 实例，支持连接池
        httpClient = HttpClient.newBuilder()
                .version(httpVersion)
                .build();
        // 获取 url
        try {
            String url = config.getString(CONFIG_KEY_HTTP_URL);
            if (url == null || url.trim().isEmpty()) {
                //noinspection deprecation
                url = config.getString(CONFIG_KEY_HTTP_API_URL);
            }
            httpUrl = new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        // 获取 Http header Content-Type
        contentType = config.getString(CONFIG_KEY_HTTP_CONTENT_TYPE);
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        for (final SinkRecord record : records) {
            log.trace("{} put record: {}", PLUGIN_NAME, record);
            Object value = record.value();
            if (value == null) {
                continue;
            }
            log.info("record value type: {}", value.getClass().getName());
            String valueStr = value.toString();
            if (valueStr.isEmpty()) {
                continue;
            }
            log.info("record value: {}", valueStr);

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(httpUrl)
                    .header("Content-Type", this.contentType)
                    .POST(HttpRequest.BodyPublishers.ofString(valueStr))
                    .build();
            try {
                HttpResponse<String> httpResponse = httpClient.send(
                        httpRequest, HttpResponse.BodyHandlers.ofString());
                log.info("response: {},{}", httpResponse.statusCode(), httpResponse.body());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void stop() {
        if (httpClient != null) {
            httpClient.close();
        }
    }
}

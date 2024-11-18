package rebue.wheel.kafka.httpsink;

import static rebue.wheel.kafka.httpsink.HttpSinkCst.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpSinkTask extends SinkTask {
    private URI                httpApiUrl;
    private HttpClient         httpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String version() {
        return PLUGIN_VERSION;
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("starting task: {}\n{}", PLUGIN_NAME, props);
        HttpSinkConfig config = new HttpSinkConfig(props);
        try {
            httpApiUrl = new URI(config.getString(CONFIG_KEY_HTTP_API_URL));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpClient.Version httpVersion = HttpClient.Version.valueOf(config.getString(CONFIG_KEY_HTTP_VERSION));
        // 创建 HttpClient 实例，支持连接池
        httpClient = HttpClient.newBuilder()
                .version(httpVersion)
                .build();
    }

    @Override
    public void put(Collection<SinkRecord> records) {
        for (final SinkRecord record : records) {
            log.trace("{} put record: {}", PLUGIN_NAME, record);
            try {
                Object value = record.value();
                if (value == null) {
                    continue;
                }
                String valueStr = objectMapper.writeValueAsString(value);
                log.info("record value: {}", valueStr);
                if (valueStr.isEmpty()) {
                    continue;
                }
                HttpRequest          httpRequest  = HttpRequest.newBuilder()
                        .uri(httpApiUrl)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(valueStr))
                        .build();
                HttpResponse<String> httpResponse = httpClient.send(
                        httpRequest, HttpResponse.BodyHandlers.ofString());
                log.info("response: {},{}", httpResponse.statusCode(), httpResponse.body());
            } catch (IOException | InterruptedException e) {
                throw new RetriableException(e);
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

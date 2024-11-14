package rebue.wheel.kafka.httpsink;

import static rebue.wheel.kafka.httpsink.HttpSinkCst.CONFIG_KEY_HTTP_API_URL;
import static rebue.wheel.kafka.httpsink.HttpSinkCst.CONFIG_KEY_HTTP_VERSION;

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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpSinkTask extends SinkTask {
    private URI        httpApiUrl;
    private HttpClient httpClient;

    @Override
    public String version() {
        return getClass().getPackage().getImplementationVersion();
    }

    @Override
    public void start(Map<String, String> props) {
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
            log.debug("sink record: {}", record);
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(httpApiUrl)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString((String) record.value()))
                    .build();
            try {
                httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                throw new RetriableException(e);
            }
        }
    }

    @Override
    public void stop() {
        httpClient.close();
    }
}

package rebue.wheel.kafka.httpsink;

import static rebue.wheel.kafka.httpsink.HttpSinkCst.PLUGIN_NAME;

import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpSinkConnector extends SinkConnector {

    @Override
    public void start(Map<String, String> props) {
        log.info("Starting connector {}", PLUGIN_NAME);
    }

    @Override
    public Class<? extends Task> taskClass() {
        return HttpSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int i) {
        return List.of();
    }

    @Override
    public void stop() {
        log.info("Stop connector {}", PLUGIN_NAME);
    }

    @Override
    public ConfigDef config() {
        return HttpSinkConfig.configDef();
    }

    @Override
    public String version() {
        return getClass().getPackage().getImplementationVersion();
    }
}

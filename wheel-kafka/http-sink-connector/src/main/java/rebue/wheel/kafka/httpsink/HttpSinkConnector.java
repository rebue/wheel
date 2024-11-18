package rebue.wheel.kafka.httpsink;

import static rebue.wheel.kafka.httpsink.HttpSinkCst.PLUGIN_NAME;
import static rebue.wheel.kafka.httpsink.HttpSinkCst.PLUGIN_VERSION;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpSinkConnector extends SinkConnector {

    private Map<String, String> props;

    @Override
    public String version() {
        return PLUGIN_VERSION;
    }

    @Override
    public void start(Map<String, String> props) {
        log.info("Starting connector {}\n{}", PLUGIN_NAME, props);
        this.props = props;

    }

    @Override
    public Class<? extends Task> taskClass() {
        return HttpSinkTask.class;
    }

    @Override
    public List<Map<String, String>> taskConfigs(int maxTasks) {
        List<Map<String, String>> configs = new LinkedList<>();
        configs.add(props);
        return configs;
    }

    @Override
    public void stop() {
        log.info("Stop connector {}", PLUGIN_NAME);
    }

    @Override
    public ConfigDef config() {
        return HttpSinkConfig.configDef();
    }
}

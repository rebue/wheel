package rebue.wheel.vertx.config;

import lombok.Data;

@Data
public class PulsarClientProperties {

    /**
     * 请求pulsar服务的url
     */
    private String serviceUrl = System.getProperty("pulsar-default-service-url", "pulsar://localhost:6650");

}

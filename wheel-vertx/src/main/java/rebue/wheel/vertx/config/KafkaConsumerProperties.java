package rebue.wheel.vertx.config;

import lombok.Data;

@Data
public class KafkaConsumerProperties {

    /**
     * 请求kafka服务的url
     */
    private String serviceUrl = System.getProperty("kafka-default-service-url", "pulsar://localhost:6650");

}

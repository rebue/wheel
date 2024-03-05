package rebue.wheel.vertx.config;

import lombok.Data;

@Data
public class GrpcNettyProperties {

    private ServerProperties server = new ServerProperties();

    @Data
    public static class ServerProperties {
        /**
         * 主机
         */
        private String host;

        /**
         * 主机端口号
         */
        private Integer port;
    }

}

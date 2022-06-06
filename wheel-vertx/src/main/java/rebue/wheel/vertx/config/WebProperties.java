package rebue.wheel.vertx.config;

import java.util.Map;

import io.vertx.ext.web.handler.LoggerFormat;
import lombok.Data;

@Data
public class WebProperties {
    /**
     * 是否记录日志
     */
    private Boolean             isLogging    = false;

    /**
     * 日志格式
     */
    private LoggerFormat        loggerFormat = LoggerFormat.SHORT;

    /**
     * 是否需要CORS
     */
    private Boolean             isCors       = false;

    /**
     * httpServerOptions
     */
    private Map<String, Object> server;

}

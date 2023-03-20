package rebue.wheel.vertx.config;

import io.vertx.ext.web.handler.LoggerFormat;
import lombok.Data;

import java.util.Map;

@Data
public class WebProperties {
    /**
     * 是否记录日志
     */
    private Boolean             isLogging      = false;
    /**
     * 日志格式
     */
    private LoggerFormat        loggerFormat   = LoggerFormat.SHORT;
    /**
     * 路由器是否解析 forwarded 类型的 headers
     * 默认 NONE: 不解析
     * FORWARD: 标准的Forward header，<a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Forwarded">https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Forwarded</a>
     * X_FORWARD: X-Forward-*
     * ALL: 支持 FORWARD 和 X_FORWARD
     */
    private String              allowForward   = "NONE";
    /**
     * 是否响应时间
     * 如果是，将在响应的header中包含x-response-time返回响应的时间
     */
    private Boolean             isResponseTime = false;
    /**
     * 超时时间(毫秒)
     * 如果有设置且不为0，超时则返回503(返回值可以通过timeoutErrorCode自定义)
     * 如果未设置或为0则无超时处理
     */
    private Long                timeout        = 0L;
    /**
     * 超时返回的错误状态码(如果不设置，默认为503)
     */
    private Integer             timeoutErrorCode;
    /**
     * 是否需要CORS
     */
    private Boolean             isCors         = false;
    /**
     * httpServerOptions
     */
    private Map<String, Object> server;

}

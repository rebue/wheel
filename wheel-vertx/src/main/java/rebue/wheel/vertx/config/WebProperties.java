package rebue.wheel.vertx.config;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.vertx.ext.web.handler.LoggerFormat;
import lombok.Data;

@Data
public class WebProperties {
    /**
     * 是否记录日志
     */
    private Boolean                  isLogging                 = false;
    /**
     * 日志格式
     * 默认 DEFAULT: remote-client - - [timestamp] "method uri version" status content-length "referrer" "user-agent"
     */
    private LoggerFormat             loggerFormat              = LoggerFormat.DEFAULT;
    /**
     * 路由器是否解析 forwarded 类型的 headers
     * 默认 NONE: 不解析
     * FORWARD: 标准的Forward header，
     * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Forwarded">https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Forwarded</a>
     * X_FORWARD: X-Forward-*
     * ALL: 支持 FORWARD 和 X_FORWARD
     */
    private String                   allowForward              = "NONE";
    /**
     * 是否打印来源的IP
     * 默认 false: 不打印
     */
    private Boolean                  printSrcIp                = false;
    /**
     * 是否返回响应时间
     * 如果是，将在响应的header中包含x-response-time返回响应的时间
     */
    private Boolean                  returnResponseTime        = false;
    /**
     * 超时时间(毫秒)
     * 如果有设置且不为0，超时则返回503(返回值可以通过timeoutErrorCode自定义)
     * 如果未设置或为0则无超时处理
     */
    private Long                     timeout                   = 0L;
    /**
     * 超时返回的错误状态码(如果不设置，默认为503)
     */
    private Integer                  timeoutErrorCode;
    /**
     * 是否自动响应content-type(处理器会通过 getAcceptableContentType 方法来选择适当的内容类型)
     */
    private Boolean                  isAutoResponseContentType = false;
    /**
     * 是否跨域
     */
    private Boolean                  isCors                    = false;
    /**
     * 是否开启黑名单
     */
    private BlackListProperties      blackList                 = new BlackListProperties();
    /**
     * 是否限流
     */
    private LimitRateProperties      limitRate                 = new LimitRateProperties();
    /**
     * websocket
     */
    private List<SseRouteProperties> websocketRoutes           = new LinkedList<>();
    /**
     * sockjs
     */
    private List<SseRouteProperties> sockjsRoutes              = new LinkedList<>();
    /**
     * 动态路由
     */
    private DynamicRouteProperties   dynamicRoute              = new DynamicRouteProperties();
    /**
     * 实现自签名证书
     */
    private Boolean                  selfSignedCertificate     = false;
    /**
     * http转https(值为http监听的端口号，不设置、null或0则不进行http监听和转换)
     */
    private Map<String, Object>      http2https;
    /**
     * httpServerOptions
     */
    private Map<String, Object>      server;
    /**
     * 全局路由处理器列表
     */
    private Map<String, Object>      globalRouteHandlers       = new LinkedHashMap<>();

    /**
     * 黑名单
     */
    @Data
    public static class BlackListProperties {
        private Boolean enabled     = false;
        /**
         * redis中key的前缀，默认是 rebue.wheel.vertx.web.black-list:
         */
        private String  redisPrefix = "rebue.wheel.vertx.web.black-list:";
    }

    /**
     * 限流
     */
    @Data
    public static class LimitRateProperties {
        private Boolean enabled     = false;
        /**
         * redis中key的前缀，默认是 rebue.wheel.vertx.web.limit-rate:
         */
        private String  redisPrefix = "rebue.wheel.vertx.web.limit-rate:";
    }

    /**
     * 动态路由
     */
    @Data
    public static class DynamicRouteProperties {
        private Boolean enabled = false;
        /**
         * 消息队列的名称
         */
        private String  mqName  = "rebue.vertx-web.dynamic-route.refresh";
        /**
         * 消息中间件的类型
         * RabbitMQ/Kafka
         */
        private String  mqType  = "RabbitMQ";
    }

    /**
     * 服务端发送事件(SSE，泛指SSE，SockJS，websocket)路由
     */
    @Data
    public static class SseRouteProperties {
        /**
         * 路由路径
         */
        private String path;
        /**
         * 发送消息给浏览器的主题
         * 收到此主题的消息，服务端将转发给所有浏览器
         */
        private String sendTopic;
        /**
         * 接收浏览器消息的主题
         * 服务端收到浏览器发来的消息，将发送到此主题
         */
        private String receiveTopic;
        /**
         * 设置浏览器ID的Cookie的key
         */
        private String userAgentIdCookieKey;
    }
}

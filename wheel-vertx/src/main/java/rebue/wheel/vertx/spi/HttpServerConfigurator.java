package rebue.wheel.vertx.spi;

import io.vertx.core.http.HttpServer;

/**
 * Http服务器配置器
 *
 * @author zbz
 */
public interface HttpServerConfigurator {

    /**
     * @return 配置器名称
     */
    String name();

    /**
     * 前置配置路由
     */
    void config(HttpServer httpServer);

}

package rebue.wheel.vertx.spi;

import io.vertx.ext.web.Router;

/**
 * 路由前置配置器
 *
 * @author zbz
 */
public interface RoutePreConfigurator {

    /**
     * @return 配置器名称
     */
    String name();

    /**
     * 前置配置路由
     */
    void config(Router router);

}

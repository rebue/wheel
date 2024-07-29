package rebue.wheel.vertx.spi;

import io.vertx.ext.web.Router;

/**
 * 路由后置配置器
 *
 * @author zbz
 */
public interface RoutePostConfigurator {

    /**
     * @return 配置器名称
     */
    String name();

    /**
     * 后置配置路由
     */
    void config(Router router);

}

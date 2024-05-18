package rebue.wheel.vertx.spi;

import com.google.inject.Injector;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

/**
 * 全局路由后置处理器工厂
 *
 * @author zbz
 */
public interface GlobalRoutePostHandlerFactory {

    /**
     * @return 工厂名称
     */
    String name();

    /**
     * 创建全局路由后置处理器
     *
     * @param vertx    vertx实例
     * @param injector 注入器
     * @param options  处理器的配置选项
     * @return 全局路由后置处理器
     */
    Handler<RoutingContext> create(Vertx vertx, Injector injector, Object options);

}

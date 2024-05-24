package rebue.wheel.vertx.spi;

import com.google.inject.Injector;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;

/**
 * 全局路由处理器工厂
 *
 * @author zbz
 */
public interface GlobalRouteHandlerFactory {

    /**
     * @return 工厂名称
     */
    String name();

    /**
     * 初始化
     *
     * @param vertx    vertx实例
     * @param injector 注入器
     * @param options  处理器的配置选项
     */
    void init(Vertx vertx, Injector injector, Object options);

    /**
     * 创建全局路由前置处理器
     *
     * @return 全局路由前置处理器
     */
    default Handler<RoutingContext> createPreHandler() {
        return null;
    }

    ;

    /**
     * 创建全局路由后置处理器
     *
     * @return 全局路由后置处理器
     */
    default Handler<RoutingContext> createPostHandler() {
        return null;
    }

    ;
}

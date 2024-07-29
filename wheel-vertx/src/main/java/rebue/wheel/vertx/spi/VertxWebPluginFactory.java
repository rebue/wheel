package rebue.wheel.vertx.spi;

import com.google.inject.Injector;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

/**
 * Vert.x web插件工厂
 *
 * @author zbz
 */
public interface VertxWebPluginFactory {

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
    void init(Vertx vertx, Router router, Injector injector, Object options);

}

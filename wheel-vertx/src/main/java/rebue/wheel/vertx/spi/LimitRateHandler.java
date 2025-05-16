package rebue.wheel.vertx.spi;

import java.util.Map;

import com.google.inject.Injector;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.SecurityPolicyHandler;

/**
 * 限流处理器
 *
 * @author zbz
 */
public interface LimitRateHandler extends SecurityPolicyHandler {

    /**
     * 处理器名称
     * 
     * @return 处理器名称
     */
    String name();

    /**
     * 初始化
     *
     * @param vertx    vertx实例
     * @param router   路由器
     * @param injector 注入器
     * @param options  处理器的配置选项
     */
    void init(Vertx vertx, Router router, Injector injector, Map<String, Object> options);

}

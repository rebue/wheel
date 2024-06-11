package rebue.wheel.vertx.web;

import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.PlatformHandler;

/**
 * 压缩响应的处理器
 *
 * @author zbz
 */
public class CompressResponseHandler implements PlatformHandler {

    @Override
    public void handle(RoutingContext routingContext) {
        routingContext.next();

        if (routingContext.normalizedPath().endsWith(".gz")) {
            routingContext.response().headers().set("Content-Encoding", "gzip");
        }

        if (routingContext.normalizedPath().endsWith(".br")) {
            routingContext.response().headers().set("Content-Encoding", "br");
        }

        if (routingContext.normalizedPath().contains(".wasm")) {
            routingContext.response().headers().set("Content-Type", "application/wasm");
        }
    }

}

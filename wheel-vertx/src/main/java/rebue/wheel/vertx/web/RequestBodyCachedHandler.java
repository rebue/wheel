package rebue.wheel.vertx.web;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RoutingContextInternal;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存body的处理器
 *
 * @author zbz
 *
 */
@Slf4j
public class RequestBodyCachedHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        log.debug("RequestBodyCachedHandler.handle");
        final String originBody = ctx.body().asString();
        log.debug("originBody: {}", originBody);
        if (originBody == null) {
            log.warn("body is null");
            ctx.next();
            return;
        }
        ctx.put("originBody", originBody);
        ((RoutingContextInternal) ctx).setBody(Buffer.buffer(originBody));
        ctx.next();
    }

}

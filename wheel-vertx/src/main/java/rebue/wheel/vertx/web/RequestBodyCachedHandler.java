package rebue.wheel.vertx.web;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.RoutingContextInternal;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存body的处理器
 * body只能读取一次，将其存入ctx中方便多次读取
 *
 * @author zbz
 */
@Slf4j
@Deprecated
public class RequestBodyCachedHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        log.debug("RequestBodyCachedHandler.handle");
        final String originRequestBody = ctx.body().asString();
        log.debug("originRequestBody: {}", originRequestBody);
        if (originRequestBody == null) {
            log.warn("body is null");
            ctx.next();
            return;
        }
        ctx.put("originRequestBody", originRequestBody);
        ((RoutingContextInternal) ctx).setBody(Buffer.buffer(originRequestBody));
        ctx.next();
    }

}

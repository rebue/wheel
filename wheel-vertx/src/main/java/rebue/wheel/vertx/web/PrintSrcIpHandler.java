package rebue.wheel.vertx.web;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 打印来源IP的处理器
 *
 * @author zbz
 */
@Slf4j
public class PrintSrcIpHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        log.debug("PrintSrcIpHandler.handle");
        HttpServerRequest req    = ctx.request();
        String            realIp = req.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(realIp)) {
            log.info("X-Real-IP: {}", realIp);
        }
        String xforward = req.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(xforward)) {
            log.info("X-Forwarded-For: {}", xforward);
        }
        SocketAddress socketAddress = req.localAddress();
        if (socketAddress != null) {
            log.info("local address: {}", socketAddress);
        }
        socketAddress = req.remoteAddress();
        if (socketAddress != null) {
            log.info("remote address: {}", socketAddress);
        }
        ctx.next();
    }

}

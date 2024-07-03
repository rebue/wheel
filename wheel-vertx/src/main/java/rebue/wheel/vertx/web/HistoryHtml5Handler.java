package rebue.wheel.vertx.web;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.PlatformHandler;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.api.dic.HttpStatusCodeDic;

/**
 * 历史模式为HTML5的处理器
 * https://router.vuejs.org/zh/guide/essentials/history-mode.html#HTML5-%E6%A8%A1%E5%BC%8F
 *
 * @author zbz
 */
@Slf4j
public class HistoryHtml5Handler implements PlatformHandler {

    @Override
    public void handle(RoutingContext routingContext) {
        log.info("HistoryHtml5Handler.handle");
        HttpServerRequest  request  = routingContext.request();
        HttpServerResponse response = routingContext.response();
        if ("GET".equals(request.method().name()) && HttpStatusCodeDic.NOT_FOUND.getCode() == response.getStatusCode()) {
            routingContext.reroute("index.html");
        }
        routingContext.next();
    }

}

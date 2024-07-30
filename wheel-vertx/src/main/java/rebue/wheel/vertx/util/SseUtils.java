package rebue.wheel.vertx.util;

import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;

public class SseUtils {
    public static void init(HttpServerResponse response) {
        response.setChunked(true);
        response.putHeader("Content-Type", "text/event-stream");
        response.putHeader("Connection", "keep-alive");
        response.putHeader("Cache-Control", "no-cache");
    }

    public static Future<Void> send(HttpServerResponse response, String data) {
        return response.write("data:" + data + "\n\n");
    }
}

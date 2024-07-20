package rebue.wheel.vertx.util;

import io.vertx.core.buffer.Buffer;
import io.vertx.httpproxy.Body;
import rebue.wheel.core.compress.BrotliUtils;
import rebue.wheel.core.compress.GzipUtils;

public class BodyUtils {
    /**
     * 从Body中获取内容字符串
     * 
     * @param contentEncoding http响应headers中的Content_Encoding，指出了编码格式(目前支持gzip/br或无编码)
     * @param buffer          body的缓冲区
     * @return 内容字符串
     */
    public static String getContent(String contentEncoding, Buffer buffer) {
        if (contentEncoding != null) {
            switch (contentEncoding) {
            case "gzip":
                return GzipUtils.decompress(buffer.getBytes());
            case "br":
                return BrotliUtils.decompress(buffer.getBytes());
            }
        }
        return buffer.toString();
    }

    /**
     * 从内容字符串构建一个新的body
     * 
     * @param contentEncoding http响应headers中的Content_Encoding，指出了编码格式(目前支持gzip/br或无编码)
     * @param content         内容字符串
     * @return Body
     */
    public static Body newBody(String contentEncoding, String content) {
        byte[] contentBytes = null;
        if (contentEncoding != null) {
            switch (contentEncoding) {
            case "gzip" -> contentBytes = GzipUtils.compress(content);
            case "br" -> contentBytes = BrotliUtils.compress(content);
            }
        }
        if (contentBytes == null) {
            contentBytes = content.getBytes();
        }
        return Body.body(Buffer.buffer(contentBytes));
    }
}

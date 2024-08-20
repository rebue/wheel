package rebue.wheel.vertx.spi;

import io.vertx.core.eventbus.MessageCodec;

/**
 * 消息解码器适配器
 */
public interface MessageCodecAdapter<T> {
    /**
     * @return 解码器适配器名称
     */
    String name();

    /**
     * @return 消息的类
     */
    Class<T> messageClass();

    /**
     * @return 消息解码器
     */
    MessageCodec<T, T> messageCodec();

}

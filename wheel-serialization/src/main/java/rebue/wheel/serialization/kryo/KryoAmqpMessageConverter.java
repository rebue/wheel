package rebue.wheel.serialization.kryo;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class KryoAmqpMessageConverter extends AbstractMessageConverter {

    public static final String CONTENT_TYPE_KRYO = "application/kryo";
    public static final String DEFAULT_CHARSET   = "UTF-8";

    /**
     * Convert from a Message to a Java object.
     *
     * @param message the message to convert
     *
     * @return the converted Java object
     *
     * @throws MessageConversionException in case of conversion failure
     *
     */
    @Override
    public Object fromMessage(final Message message) throws MessageConversionException {
        try {
            return KryoUtils.readObject(message.getBody());
        } catch (final Exception e) {
            final String msg = "RabbitMQ将消息中的Body字节数组转换成对象时出现异常";
            log.error(msg, e);
            throw new MessageConversionException(msg, e);
        }
    }

    @Override
    protected Message createMessage(final Object object, final MessageProperties messageProperties) {
        byte[] body;
        try {
            body = KryoUtils.writeObject(object);
        } catch (final Exception e) {
            final String msg = "RabbitMQ创建消息将对象转换成字节数组时出现异常";
            log.error(msg, e);
            throw new MessageConversionException(msg, e);
        }
        messageProperties.setContentType(CONTENT_TYPE_KRYO);
        if (messageProperties.getContentEncoding() == null) {
            messageProperties.setContentEncoding(DEFAULT_CHARSET);
        }
        return new Message(body, messageProperties);
    }

}

package rebue.wheel.serialization.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RabbitTemplateUtils {

    public static boolean send(final RabbitTemplate rabbitTemplate, final String exchange, final String routingKey, final Object msg, final long timeout) {
        log.info("开始发送消息: exchange-{}, routingKey-{}, msg-{}, timeout-{}", exchange, routingKey, msg, timeout);
        try {
            return rabbitTemplate.invoke(operations -> {
                operations.convertAndSend(exchange, routingKey, msg);
                return operations.waitForConfirms(timeout);// TODO 配置
            }, (tag, multiple) -> {
                log.info("收到exchange的应答-Ack: " + tag + "," + multiple);
            }, (tag, multiple) -> {
                log.info("收到exchange的应答-Nack: " + tag + "," + multiple);
            });
        } catch (final Exception e) {
            log.error("发送消息失败", e);
            return false;
        }
    }

}

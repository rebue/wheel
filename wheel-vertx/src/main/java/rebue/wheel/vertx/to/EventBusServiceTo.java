package rebue.wheel.vertx.to;

import lombok.Builder;
import lombok.Data;

/**
 * EventBus服务的传递参数
 *
 * @author zbz
 *
 */
@Data
@Builder
public class EventBusServiceTo {

    /**
     * 消息地址
     */
    private String addr;

    /**
     * 消息动作
     */
    private String action;

    /**
     * 消息主体
     */
    private Object body;

}

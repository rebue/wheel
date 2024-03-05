package rebue.wheel.vertx.to;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * EventBus服务的传递参数
 *
 * @author zbz
 */
@DataObject
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public EventBusServiceTo(final JsonObject jsonObject) {
        setAddr(jsonObject.getString("addr"));
        setAction(jsonObject.getString("action"));
        setBody(jsonObject.getValue("body"));
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}

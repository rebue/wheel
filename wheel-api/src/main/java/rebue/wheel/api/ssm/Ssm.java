package rebue.wheel.api.ssm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务端发送消息对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
public class Ssm<T> {
    /**
     * 命令
     */
    private String cmd;
    /**
     * 数据
     */
    private T      data;

}

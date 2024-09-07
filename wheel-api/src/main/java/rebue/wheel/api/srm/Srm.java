package rebue.wheel.api.srm;

import lombok.Data;

/**
 * 服务端接收消息对象
 */
@Data
public class Srm {
    /**
     * 命令
     */
    private String cmd;
    /**
     * 数据
     */
    private String data;

}

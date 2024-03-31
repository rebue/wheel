package rebue.wheel.core.db.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字典项元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
public class DicItemMeta {
    /**
     * 字典项编码
     */
    private Integer code;
    /**
     * 字典项名称
     */
    private String  name;
    /**
     * 字典项描述
     */
    private String  desc;
}

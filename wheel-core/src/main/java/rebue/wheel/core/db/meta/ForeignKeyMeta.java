package rebue.wheel.core.db.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 外键元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForeignKeyMeta {
    /**
     * 外键表名
     */
    private String fkTableName;
    /**
     * 外键字段名
     */
    private String fkFiledName;
    /**
     * 主键表名
     */
    private String pkTableName;
    /**
     * 主键字段名
     */
    private String pkFieldName;
}

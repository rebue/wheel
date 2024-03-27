package rebue.wheel.core.db.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 外键元数据
 * XXX 外键表: 多对一关系中代表多的这个表; 主键表: 多对一关系中代表一的这个表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForeignKeyMeta {
    /**
     * 外键表名(多对一关系中代表多的这个表的表名)
     */
    private String fkTableName;
    /**
     * 外键字段名
     */
    private String fkFieldName;
    /**
     * 主键表名(多对一关系中代表一的这个表的表名)
     */
    private String pkTableName;
    /**
     * 主键表别名
     */
    private String pkTableAlias;
    /**
     * 主键字段名
     */
    private String pkFieldName;
}

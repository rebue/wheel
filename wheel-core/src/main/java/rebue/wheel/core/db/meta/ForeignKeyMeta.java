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
     * 外键字段别名
     * 为外键字段名去掉为_id的后缀，然后转小驼峰
     * 用于生成关联属性名的前缀，后面跟主键表属性类简名
     */
    private String fkFieldAlias;
    /**
     * 主键表名(多对一关系中代表一的这个表的表名)
     */
    private String pkTableName;
    /**
     * 主键表别名
     * 用于 join on 关联表别名使用
     * 按外键表关联的顺序生成，例如a、b、c、d、e....
     */
    private String pkTableAlias;
    /**
     * 主键字段名
     */
    private String pkFieldName;
}

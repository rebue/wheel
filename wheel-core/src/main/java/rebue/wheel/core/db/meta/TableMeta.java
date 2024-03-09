package rebue.wheel.core.db.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * 表元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableMeta {
    /**
     * 表名
     */
    private String               name;
    /**
     * 表注释
     */
    private String               remark;
    /**
     * 表的主键列表
     */
    @Builder.Default
    private List<String>         primaryKeys  = new LinkedList<>();
    /**
     * 表的unique列表
     */
    @Builder.Default
    private List<String>         uniques      = new LinkedList<>();
    /**
     * 表的外键列表
     */
    @Builder.Default
    private List<ForeignKeyMeta> importedKeys = new LinkedList<>();
    /**
     * 字段集合
     */
    @Builder.Default
    private List<FieldMeta>      fields       = new LinkedList<>();
}

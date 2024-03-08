package rebue.wheel.core.db.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldMeta {
    /**
     * 字段名
     */
    private String  name;
    /**
     * 字段类型
     */
    private Integer type;
    /**
     * 字体类型名称
     */
    private String  typeName;
    /**
     * 精度
     */
    private Integer precision;
    /**
     * 数值范围
     */
    private Integer scale;
    /**
     * 是否主键
     */
    private Boolean isPrimaryKey;
    /**
     * 是否唯一
     */
    private Boolean isUnique;
    /**
     * 是否可空
     */
    private Boolean isNullable;
    /**
     * 是否无符号(仅在MySQL中有效)
     */
    @Builder.Default
    private Boolean isUnsigned   = false;
    /**
     * 列注释
     */
    private String  remark;
    /**
     * 是否外键
     */
    @Builder.Default
    private Boolean isForeignKey = false;
    /**
     * 外键引用的表名
     */
    private String  referencedTableName;
    /**
     * 外键引用的表类名
     */
    private String  referencedTableClassName;
    /**
     * 外键引用的表实例名
     */
    private String  referencedTableInstanceName;
    /**
     * 外键引用的列名
     */
    private String  referencedColumnName;
    /**
     * 外键引用的列类名
     */
    private String  referencedColumnClassName;
    /**
     * 外键引用的列实例名
     */
    private String  referencedColumnInstanceName;
}

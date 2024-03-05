package rebue.wheel.core.db.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 属性元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyMeta {
    /**
     * 属性名
     */
    private String    name;
    /**
     * 标题
     */
    private String    title;
    /**
     * 属性类名称
     */
    private String    className;
    /**
     * 属性类简名
     */
    private String    classSimpleName;
    /**
     * Js的类型
     */
    private String    jsType;
    /**
     * 属性注释
     */
    private String    remark;
    /**
     * 字段元数据
     */
    private FieldMeta field;
}

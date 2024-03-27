package rebue.wheel.core.db.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private String       name;
    /**
     * 属性别名
     */
    private String       alias;
    /**
     * 标题
     */
    private String       title;
    /**
     * 属性类名称
     */
    private String       className;
    /**
     * 属性类简名
     */
    private String       classSimpleName;
    /**
     * Js的类型
     */
    private String       jsType;
    /**
     * 是否关键字
     */
    private Boolean      isKeyWord;
    /**
     * 属性注释
     */
    private String       remark;
    /**
     * 属性注释列表
     */
    private List<String> remarks;
    /**
     * 字段元数据
     */
    private FieldMeta    field;
}

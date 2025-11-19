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
     * 属性名(大驼峰)
     */
    private String       nameUpperCamel;
    /**
     * 属性别名
     * 原用于给外键关联属性命名，后续用外键名称为前缀+属性类简名来命名
     */
    @Deprecated
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
     * 是否密钥
     */
    private Boolean      isKey;
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

package rebue.wheel.core.db.meta;

import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * POJO元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PojoMeta {
    /**
     * 类名(其实是类的简名，不带包，因为从数据库中无法知道应该是哪个包)
     */
    private String             className;
    /**
     * 标题
     */
    private String             title;
    /**
     * 实例名
     */
    private String             instanceName;
    /**
     * 实例简名(不带项目前缀)
     */
    private String             instanceSimpleName;
    /**
     * 小写连字号名
     */
    private String             lowerHyphenName;
    /**
     * 小写连字号名(不带项目前缀)
     */
    private String             lowerHyphenNameWithoutPrefix;
    /**
     * 类注释
     */
    private String             remark;
    /**
     * 类注释列表
     */
    private List<String>       remarks;
    /**
     * 对应的表元数据
     */
    private TableMeta          table;
    /**
     * 属性列表
     */
    @Builder.Default
    private List<PropertyMeta> properties = new LinkedList<>();
    /**
     * ID列表
     */
    @Builder.Default
    private List<PropertyMeta> ids        = new LinkedList<>();
}

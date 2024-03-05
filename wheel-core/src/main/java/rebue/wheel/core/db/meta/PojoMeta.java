package rebue.wheel.core.db.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * POJO元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PojoMeta {
    /**
     * 类名
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
     * 小写连字号名
     */
    private String             lowerHyphenName;
    /**
     * 小写连字号名(不带项目前缀)
     */
    private String             lowerHyphenNameWithoutPrefix;
    /**
     * 注释
     */
    private String             remark;
    /**
     * 对应的表元数据
     */
    private TableMeta          table;
    /**
     * 属性列表
     */
    @Builder.Default
    private List<PropertyMeta> properties = new LinkedList<>();
}

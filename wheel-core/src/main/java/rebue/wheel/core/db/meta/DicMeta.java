package rebue.wheel.core.db.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * 字典元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DicMeta {
    /**
     * 字典名称
     */
    private String            name;
    /**
     * 字典类名(其实是类的简名，不带包，因为从数据库中无法知道应该是哪个包)
     */
    private String            className;
    /**
     * 字典备注
     */
    private List<String>      remarks;
    /**
     * 字典项列表
     */
    @Builder.Default
    private List<DicItemMeta> items = new LinkedList<>();
}

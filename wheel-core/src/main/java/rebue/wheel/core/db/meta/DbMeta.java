package rebue.wheel.core.db.meta;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * 数据库元数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DbMeta {
    /**
     * 数据库名称
     */
    private String          name;
    /**
     * 数据库产品名称
     */
    private String          productName;
    /**
     * 表元数据的列表
     */
    @Builder.Default
    private List<TableMeta> tables = new LinkedList<>();
}

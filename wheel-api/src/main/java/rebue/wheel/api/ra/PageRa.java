package rebue.wheel.api.ra;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 带有分页信息的附加内容
 * 主要给分页查询返回生成后的分页信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Builder
@JsonInclude(Include.NON_NULL)
public class PageRa<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页
     */
    private Integer           pageNum;
    /**
     * 总记录数
     */
    protected Long            total;
    /**
     * 结果集
     */
    private List<T>           list;

}
package rebue.wheel.api.ra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 带有数量结果的附加内容
 * 主要给需要获取数量系列的查询方法使用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class CountRa implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回数量
     */
    private Long count;

}
package rebue.wheel.api.ra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 带有Integer结果的附加内容
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class IntegerRa implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回Integer的值
     */
    private Integer value;

}
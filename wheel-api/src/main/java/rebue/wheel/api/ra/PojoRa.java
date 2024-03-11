package rebue.wheel.api.ra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 带有POJO的附加内容
 * 主要给获取单个POJO的系列查询方法使用
 *
 * @param <T> POJO类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PojoRa<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回单个POJO对象
     */
    private T one;

}
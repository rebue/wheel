package rebue.wheel.api.ra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 带有ID的附加内容 主要给添加方法返回生成后的ID
 *
 * @param <T> POJO类唯一标识的属性的类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class IdRa<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回系统生成的ID
     */
    private T id;

}
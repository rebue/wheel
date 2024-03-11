package rebue.wheel.api.ra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * 带有ID的附加内容 主要给添加方法返回生成后的ID
 *
 * @param <T> POJO类唯一标识的属性的类
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor // 不知道@Data中默认包含的@RequiredArgsConstructor为何没起效
@JsonInclude(Include.NON_NULL)
public class IdRa<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回系统生成的ID
     */
    @NonNull
    private T id;

}
package rebue.wheel.api.ra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * 带有数量结果的附加内容
 * 主要给需要获取数量系列的查询方法使用
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor // 不知道@Data中默认包含的@RequiredArgsConstructor为何没起效
@JsonInclude(Include.NON_NULL)
public class CountRa implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回数量
     */
    @NonNull
    private Long count;

}
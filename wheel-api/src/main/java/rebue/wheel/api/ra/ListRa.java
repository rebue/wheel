package rebue.wheel.api.ra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.List;

/**
 * 带有List的附加内容
 * 主要给获取POJO列表的系列查询方法使用
 *
 * @param <POJO> 组成List列表中元素的类
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor // 不知道@Data中默认包含的@RequiredArgsConstructor为何没起效
@JsonInclude(Include.NON_NULL)
public class ListRa<POJO> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回POJO列表
     */
    @NonNull
    private List<POJO> list;

}
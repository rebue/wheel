package rebue.wheel.api.ra;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * 带有分页信息的附加内容
 * 主要给分页查询返回生成后的分页信息
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor // 不知道@Data中默认包含的@RequiredArgsConstructor为何没起效
@JsonInclude(Include.NON_NULL)
public class PageRa<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 返回分页信息
     */
    @NonNull
    private PageInfo<T> page;

}
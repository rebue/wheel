package rebue.wheel.api.ro;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rebue.wheel.api.dic.ResultDic;

/**
 * 返回结果
 *
 * @param <T> 返回附加内容的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class Ro<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回结果的类型
     */
    private ResultDic         result;

    /**
     * 返回结果的信息
     */
    private String            msg;

    /**
     * 详情
     */
    private String            detail;

    /**
     * 附加的内容
     * (如果前面的属性已经能够满足需求，可不需要附加的内容，设为null或不设置即可)
     */
    private T                 extra;

    /**
     * 返回结果的自定义编码
     * (如果通过result已经能够满足需求，可不需要自定义编码，设为null或不设置即可)
     */
    private String            code;

    public Ro(final ResultDic result, final String msg) {
        this.result = result;
        this.msg    = msg;
    }

    public Ro(final ResultDic result, final T extra) {
        this.result = result;
        this.extra  = extra;
    }

    public Ro(final ResultDic result, final String msg, final String detail) {
        this.result = result;
        this.msg    = msg;
        this.detail = detail;
    }

    public Ro(final ResultDic result, final String msg, final String detail, final String code) {
        this.result = result;
        this.msg    = msg;
        this.detail = detail;
        this.code   = code;
    }

    public Ro(final ResultDic result, final String msg, final T extra) {
        this.result = result;
        this.msg    = msg;
        this.extra  = extra;
    }

    public Ro(final ResultDic result, final String msg, final String detail, final T extra) {
        this.result = result;
        this.msg    = msg;
        this.detail = detail;
        this.extra  = extra;
    }

    public static Ro<?> success(final String msg) {
        return new Ro<>(ResultDic.SUCCESS, msg);
    }

    public static <T> Ro<T> success(final T extra) {
        return new Ro<>(ResultDic.SUCCESS, extra);
    }

    public static <T> Ro<T> success(final String msg, final T extra) {
        return new Ro<>(ResultDic.SUCCESS, msg, extra);
    }

    public static Ro<?> illegalArgument(final String msg) {
        return new Ro<>(ResultDic.ILLEGAL_ARGUMENT, msg);
    }

    public static Ro<?> illegalArgument(final String msg, final String detail) {
        return new Ro<>(ResultDic.ILLEGAL_ARGUMENT, msg, detail);
    }

    public static <T> Ro<T> illegalArgument(final T extra) {
        return new Ro<>(ResultDic.ILLEGAL_ARGUMENT, extra);
    }

    public static <T> Ro<T> illegalArgument(final String msg, final T extra) {
        return new Ro<>(ResultDic.ILLEGAL_ARGUMENT, msg, extra);
    }

    public static Ro<?> warn(final String msg) {
        return new Ro<>(ResultDic.WARN, msg);
    }

    public static Ro<?> warn(final String msg, final String detail) {
        return new Ro<>(ResultDic.WARN, msg, detail);
    }

    public static <T> Ro<T> warn(final T extra) {
        return new Ro<>(ResultDic.WARN, extra);
    }

    public static <T> Ro<T> warn(final String msg, final T extra) {
        return new Ro<>(ResultDic.WARN, msg, extra);
    }

    public static Ro<?> fail(final String msg) {
        return new Ro<>(ResultDic.FAIL, msg);
    }

    public static Ro<?> fail(final String msg, final String detail) {
        return new Ro<>(ResultDic.FAIL, msg, detail);
    }

    public static <T> Ro<T> fail(final T extra) {
        return new Ro<>(ResultDic.FAIL, extra);
    }

    public static <T> Ro<T> fail(final String msg, final T extra) {
        return new Ro<>(ResultDic.FAIL, msg, extra);
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    @JsonIgnore
    public boolean isSuccess() {
        return ResultDic.SUCCESS.equals(result);
    }

}

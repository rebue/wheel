package rebue.wheel.api.ro;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import rebue.wheel.api.dic.ResultDic;

import java.io.Serializable;

/**
 * 返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ro implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回结果的类型
     */
    protected ResultDic result;

    /**
     * 返回结果的信息
     */
    protected String msg;

    /**
     * 详情
     */
    protected String detail;

    /**
     * 附加的内容
     * (如果前面的属性已经能够满足需求，可不需要附加的内容，设为null或不设置即可)
     */
    protected Object extra;

    /**
     * 返回结果的自定义编码
     * (如果通过result已经能够满足需求，可不需要自定义编码，设为null或不设置即可)
     */
    protected String code;

    public Ro(final ResultDic result, final String msg) {
        this.result = result;
        this.msg = msg;
    }

    public Ro(final ResultDic result, final Object extra) {
        this.result = result;
        this.extra = extra;
    }

    public Ro(final ResultDic result, final String msg, final String detail) {
        this.result = result;
        this.msg = msg;
        this.detail = detail;
    }

    public Ro(final ResultDic result, final String msg, final String detail, final String code) {
        this.result = result;
        this.msg = msg;
        this.detail = detail;
        this.code = code;
    }

    public Ro(final ResultDic result, final String msg, final Object extra) {
        this.result = result;
        this.msg = msg;
        this.extra = extra;
    }

    public Ro(final ResultDic result, final String msg, final String detail, final Object extra) {
        this.result = result;
        this.msg = msg;
        this.detail = detail;
        this.extra = extra;
    }

    public static Ro success(final String msg) {
        return new Ro(ResultDic.SUCCESS, msg);
    }

    public static Ro success(final Object extra) {
        return new Ro(ResultDic.SUCCESS, extra);
    }

    public static Ro success(final String msg, final Object extra) {
        return new Ro(ResultDic.SUCCESS, msg, extra);
    }

    public static Ro illegalArgument(final String msg) {
        return new Ro(ResultDic.ILLEGAL_ARGUMENT, msg);
    }

    public static Ro illegalArgument(final String msg, final String detail) {
        return new Ro(ResultDic.ILLEGAL_ARGUMENT, msg, detail);
    }

    public static Ro illegalArgument(final Object extra) {
        return new Ro(ResultDic.ILLEGAL_ARGUMENT, extra);
    }

    public static Ro illegalArgument(final String msg, final Object extra) {
        return new Ro(ResultDic.ILLEGAL_ARGUMENT, msg, extra);
    }

    public static Ro warn(final String msg) {
        return new Ro(ResultDic.WARN, msg);
    }

    public static Ro warn(final String msg, final String detail) {
        return new Ro(ResultDic.WARN, msg, detail);
    }

    public static Ro warn(final Object extra) {
        return new Ro(ResultDic.WARN, extra);
    }

    public static Ro warn(final String msg, final Object extra) {
        return new Ro(ResultDic.WARN, msg, extra);
    }

    public static Ro fail(final String msg) {
        return new Ro(ResultDic.FAIL, msg);
    }

    public static Ro fail(final String msg, final String detail) {
        return new Ro(ResultDic.FAIL, msg, detail);
    }

    public static Ro fail(final Object extra) {
        return new Ro(ResultDic.FAIL, extra);
    }

    public static Ro fail(final String msg, final Object extra) {
        return new Ro(ResultDic.FAIL, msg, extra);
    }

    /**
     * 判断是否成功
     *
     * @return 是否成功
     */
    @JsonIgnore
    public boolean isSuccess() {
        return ResultDic.SUCCESS.equals(this.result);
    }

    /**
     * 判断是否出错
     *
     * @return 是否错误
     */
    @JsonIgnore
    public boolean isError() {
        return !ResultDic.SUCCESS.equals(this.result);
    }

}

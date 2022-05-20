package rebue.wheel.vertx.ro;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import rebue.wheel.api.dic.ResultDic;

/**
 * Vert.x的返回结果
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@DataObject
public class Vro implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回结果的类型
     */
    protected ResultDic       result;

    /**
     * 返回结果的信息
     */
    protected String          msg;

    /**
     * 详情
     */
    protected String          detail;

    /**
     * 附加的内容
     * (如果前面的属性已经能够满足需求，可不需要附加的内容，设为null或不设置即可)
     */
    protected Object          extra;

    /**
     * 返回结果的自定义编码
     * (如果通过result已经能够满足需求，可不需要自定义编码，设为null或不设置即可)
     */
    protected String          code;

    public Vro(final ResultDic result, final String msg) {
        this.result = result;
        this.msg    = msg;
    }

    public Vro(final ResultDic result, final Object extra) {
        this.result = result;
        this.extra  = extra;
    }

    public Vro(final ResultDic result, final String msg, final String detail) {
        this.result = result;
        this.msg    = msg;
        this.detail = detail;
    }

    public Vro(final ResultDic result, final String msg, final String detail, final String code) {
        this.result = result;
        this.msg    = msg;
        this.detail = detail;
        this.code   = code;
    }

    public Vro(final ResultDic result, final String msg, final Object extra) {
        this.result = result;
        this.msg    = msg;
        this.extra  = extra;
    }

    public Vro(final ResultDic result, final String msg, final String detail, final Object extra) {
        this.result = result;
        this.msg    = msg;
        this.detail = detail;
        this.extra  = extra;
    }

    public static Vro success(final String msg) {
        return new Vro(ResultDic.SUCCESS, msg);
    }

    public static Vro success(final Object extra) {
        return new Vro(ResultDic.SUCCESS, extra);
    }

    public static Vro success(final String msg, final Object extra) {
        return new Vro(ResultDic.SUCCESS, msg, extra);
    }

    public static Vro illegalArgument(final String msg) {
        return new Vro(ResultDic.ILLEGAL_ARGUMENT, msg);
    }

    public static Vro illegalArgument(final String msg, final String detail) {
        return new Vro(ResultDic.ILLEGAL_ARGUMENT, msg, detail);
    }

    public static Vro illegalArgument(final Object extra) {
        return new Vro(ResultDic.ILLEGAL_ARGUMENT, extra);
    }

    public static Vro illegalArgument(final String msg, final Object extra) {
        return new Vro(ResultDic.ILLEGAL_ARGUMENT, msg, extra);
    }

    public static Vro warn(final String msg) {
        return new Vro(ResultDic.WARN, msg);
    }

    public static Vro warn(final String msg, final String detail) {
        return new Vro(ResultDic.WARN, msg, detail);
    }

    public static Vro warn(final Object extra) {
        return new Vro(ResultDic.WARN, extra);
    }

    public static Vro warn(final String msg, final Object extra) {
        return new Vro(ResultDic.WARN, msg, extra);
    }

    public static Vro fail(final String msg) {
        return new Vro(ResultDic.FAIL, msg);
    }

    public static Vro fail(final String msg, final String detail) {
        return new Vro(ResultDic.FAIL, msg, detail);
    }

    public static Vro fail(final Object extra) {
        return new Vro(ResultDic.FAIL, extra);
    }

    public static Vro fail(final String msg, final Object extra) {
        return new Vro(ResultDic.FAIL, msg, extra);
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

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }

    public Vro(final JsonObject jsonObject) {
        log.debug("Vro constructor: {}", jsonObject);
        this.result = ResultDic.getItem(jsonObject.getInteger("result"));
        this.msg    = jsonObject.getString("msg");
        this.detail = jsonObject.getString("detail");
        this.extra  = jsonObject.getString("extra");
        this.code   = jsonObject.getString("code");
    }

}

package rebue.wheel.api.dic;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * HTTP response status code
 *
 * @author zbz
 */
@AllArgsConstructor
@Getter
public enum HttpStatusCodeDic implements Dic {
    /**
     * 200: 请求成功(一般用于响应GET请求)
     */
    OK(200, "请求成功"),
    /**
     * 201: 服务器成功处理了请求，并创建了新的资源(一般用于响应POST或PUT请求)
     */
    CREATED(201, "服务器成功处理了请求，并创建了新的资源"),
    /**
     * 202: 服务器已接受请求，但尚未处理
     */
    ACCEPTED(202, "服务器已接受请求，但尚未处理"),
    /**
     * 204: 服务器成功处理了没有内容的请求
     */
    NO_CONTENT(204, "服务器成功处理了没有内容的请求"),
    /**
     * 300: 响应有多个选择
     */
    MULTIPLE_CHOICES(300, "响应有多个选择"),
    /**
     * 301: 请求的资源被永久移动到新的地址
     */
    MOVED_PERMANENTLY(301, "请求的资源被永久移动到新的地址"),
    /**
     * 302: 要求客户端临时跳转到新的地址
     */
    FOUND(302, "要求客户端临时跳转到新的地址"),
    /**
     * 400: 请求格式错误
     */
    BAD_REQUEST(400, "请求格式错误"),
    /**
     * 401: 用户认证失败
     */
    UNAUTHORIZED(401, "用户认证失败"),
    /**
     * 403: 用户未被授权访问该资源
     */
    FORBIDDEN(403, "用户未被授权访问该资源"),
    /**
     * 404: 请求的地址不存在
     */
    NOT_FOUND(404, "请求的地址不存在"),
    /**
     * 406: 请求头的格式不正确
     */
    NOT_ACCEPTABLE(406, "请求头的格式不正确"),
    /**
     * 410: 请求的资源被永久删除，且不会再得到的
     */
    GONE(410, "请求的资源被永久删除，且不会再得到的"),
    /**
     * 422: 请求格式正确，但是由于含有语义错误，无法响应
     */
    UNPROCESSABLE_ENTITY(422, "请求格式正确，但是由于含有语义错误，无法响应"),
    /**
     * 500: 服务器内部发生错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部发生错误"),
    /**
     * 502: 网关错误
     */
    BAD_GATEWAY(502, "网关错误"),
    /**
     * 503: 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    /**
     * 504: 网关超时
     */
    GATEWAY_TIMEOUT(504, "网关超时");

    private final Integer code;
    private final String  desc;

    @Override
    public String getName() {
        return name();
    }

    /**
     * springdoc显示枚举说明将会调用此方法
     */
    @Override
    public String toString() {
        return getCode() + "(" + getDesc() + ")";
    }

    /**
     * 通过code得到枚举的实例(Jackson反序列化时会调用此方法)
     * 注意：此方法必须是static的方法，且返回类型必须是本枚举类，而不能是接口Dic
     * 否则Jackson将调用默认的反序列化方法，而不会调用本方法
     */
    @JsonCreator // Jackson在反序列化时，调用 @JsonCreator 标注的构造器或者工厂方法来创建对象
    public static HttpStatusCodeDic getItem(final Integer pcode) {
        final HttpStatusCodeDic result = (HttpStatusCodeDic) DicUtils.getItem(HttpStatusCodeDic.class, pcode);
        if (result == null) {
            throw new IllegalArgumentException("输入的code(" + pcode + ")不在枚举的取值范围内");
        }
        return result;
    }

}

package rebue.wheel.api.dic;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 方法
 * 0:ALL(*)
 * 1:GET(GET)
 * 2:POST(POST)
 * 3:PUT(PUT)
 * 4:DELETE(DELETE)
 */
@AllArgsConstructor
@Getter
public enum HttpMethodDic implements Dic {

    /**
     * 0: *
     */
    ALL(0, "*"),
    /**
     * 1: GET
     */
    GET(1, "GET"),
    /**
     * 2: POST
     */
    POST(2, "POST"),
    /**
     * 3: PUT
     */
    PUT(3, "PUT"),
    /**
     * 4: DELETE
     */
    DELETE(4, "DELETE");

    private final Integer code;

    private final String  desc;

    /**
     * 通过code得到枚举的实例(Jackson反序列化时会调用此方法)
     * 注意：此方法必须是static的方法，且返回类型必须是本枚举类，而不能是接口Dic
     * 否则Jackson将调用默认的反序列化方法，而不会调用本方法
     */
    // Jackson在反序列化时，调用 @JsonCreator 标注的构造器或者工厂方法来创建对象
    @JsonCreator
    public static HttpMethodDic getItem(final Integer pcode) {
        final HttpMethodDic result = (HttpMethodDic) DicUtils.getItem(HttpMethodDic.class, pcode);
        if (result == null) {
            throw new IllegalArgumentException("输入的code(" + pcode + ")不在枚举的取值范围内");
        }
        return result;
    }

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
}

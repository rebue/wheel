package rebue.wheel.api.dic;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回结果的字典
 * 1: 成功
 * -1: 参数错误
 * -2: 失败(系统方面的异常)
 * -3: 警告(用户方面的错误)
 */
@AllArgsConstructor
@Getter
public enum ResultDic implements Dic {
    /**
     * 1: 成功
     */
    SUCCESS(1, "成功"),
    /**
     * -1: 参数错误
     */
    ILLEGAL_ARGUMENT(-1, "参数错误"),
    /**
     * -2: 失败(系统方面的异常)
     */
    FAIL(-2, "失败"),
    /**
     * -3: 警告(用户方面的错误)
     */
    WARN(-3, "警告");

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
    public static ResultDic getItem(final Integer pcode) {
        final ResultDic result = (ResultDic) DicUtils.getItem(ResultDic.class, pcode);
        if (result == null) {
            throw new IllegalArgumentException("输入的code(" + pcode + ")不在枚举的取值范围内");
        }
        return result;
    }

}
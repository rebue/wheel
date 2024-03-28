package rebue.wheel.api.desensitize;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * 脱敏注解
 * 在属性上标记此注解，表示序列化此属性时会进行脱敏
 *
 * @author zbz
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时生效
@Target(ElementType.FIELD)          // 可用在字段上
@Documented
@JacksonAnnotationsInside           // 元注解，一般在有多个Jackson注解需要组合使用时使用
@JsonSerialize(using = DesensitizationSerialize.class)  // 自定义序列化器
public @interface Desensitize {
    /**
     * @return 脱敏策略
     * 如果要自定义策略，则不要设置此属性，且填写regex和replacement属性
     */
    DesensitizeStrategy value() default DesensitizeStrategy.CUSTOM;

    /**
     * @return 匹配的正则表达式
     */
    String regex() default "";

    /**
     * @return 要替换的表达式
     */
    String replacement() default "";
}

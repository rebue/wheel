package rebue.wheel.api.annotation;

import java.lang.annotation.*;

import rebue.wheel.api.strategy.DesensitizeStrategy;

/**
 * 脱敏
 * 
 * 在字段上标记此注解，表示读取此字段会进行脱敏
 * 
 * @author zbz
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Desensitize {
    /**
     * @return 脱敏策略
     *         如果要自定义策略，则不要设置此属性，且填写regex和replacement属性
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

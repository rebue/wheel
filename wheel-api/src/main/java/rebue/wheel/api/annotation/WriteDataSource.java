package rebue.wheel.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 写数据源
 * 
 * 在方法上标记此注解，表示此方法操作的数据源强制路由为读写分离中的写数据源
 * 
 * @author zbz
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WriteDataSource {

}

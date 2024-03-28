package rebue.wheel.core.beetl.function;

import lombok.SneakyThrows;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 添加脱敏注解的函数
 * 根据remark解析情况添加脱敏注解
 * 接收参数为remark
 */
public class AddDesensitizeAnnotationFunction implements Function {

    @Override
    @SneakyThrows
    public Object call(Object[] paras, Context ctx) {
        if (paras == null || paras.length != 1 || !(paras[0] instanceof String remark)) {
            final String msg = "参数不正确(String)";
            throw new IllegalArgumentException(msg);
        }

        if (remark.contains("@脱敏名称")) {
            ctx.byteWriter.writeString("@Desensitize(DesensitizeStrategy.NAME)\n");
        } else if (remark.contains("@脱敏身份证号")) {
            ctx.byteWriter.writeString("@Desensitize(DesensitizeStrategy.ID_CARD)\n");
        } else if (remark.contains("@脱敏手机号")) {
            ctx.byteWriter.writeString("@Desensitize(DesensitizeStrategy.MOBILE)\n");
        } else if (remark.contains("@脱敏邮箱")) {
            ctx.byteWriter.writeString("@Desensitize(DesensitizeStrategy.EMAIL)\n");
        }
        return null;
    }
}

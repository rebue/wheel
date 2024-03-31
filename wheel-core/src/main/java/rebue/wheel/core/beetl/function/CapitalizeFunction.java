package rebue.wheel.core.beetl.function;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Context;
import org.beetl.core.Function;

/**
 * 首字母大写的函数
 */
public class CapitalizeFunction implements Function {

    @Override
    @SneakyThrows
    public String call(Object[] paras, Context ctx) {
        if (paras == null || paras.length != 1 || !(paras[0] instanceof String text)) {
            final String msg = "参数不正确(String)";
            throw new IllegalArgumentException(msg);
        }
        return StringUtils.capitalize(text);
    }
}

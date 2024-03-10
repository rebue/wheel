package rebue.wheel.core.beetl.function;

import org.beetl.core.Context;
import org.beetl.core.Function;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 获取Remarks的注释
 * 在每行前面加上 "*" 号
 */
public class GetCommentOfRemarksFunction implements Function {

    @Override
    public Object call(Object[] paras, Context ctx) {
        if (paras == null || paras.length != 1 || !(paras[0] instanceof String)) {
            final String msg = "参数不正确(String)";
            throw new IllegalArgumentException(msg);
        }
        final String remarks = (String) paras[0];
        return Arrays.stream(remarks.split("\n"))
                .map(line -> " * " + line)
                .collect(Collectors.joining("\n"));
    }
}

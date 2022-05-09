package rebue.wheel.core;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {
    /**
     * 获取堆栈信息
     *
     * @param throwable 异常
     * @return 堆栈信息
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }
}

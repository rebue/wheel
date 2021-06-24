package rebue.wheel.api.exception;

/**
 * 未捕获异常的处理器(全部打印异常的栈信息，否则JVM会省略显示部分栈信息)
 */
public class UncaughtExceptionHandlerX implements Thread.UncaughtExceptionHandler {
    static {
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandlerX());
    }

    @Override
    public void uncaughtException(final Thread t, final Throwable e) {
        final StackTraceElement[] ses = e.getStackTrace();
        System.err.println("Exception in thread \"" + t.getName() + "\" " + e.toString());
        for (final StackTraceElement se : ses) {
            System.err.println("\tat " + se);
        }
        final Throwable ec = e.getCause();
        if (ec != null) {
            uncaughtException(t, ec);
        }
    }

}

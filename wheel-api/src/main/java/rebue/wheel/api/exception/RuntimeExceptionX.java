package rebue.wheel.api.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 扩展RuntimeException，详细记录异常日志
 */
@Slf4j
public class RuntimeExceptionX extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RuntimeExceptionX(final String msg) {
        super(msg);
        log.error(msg);
    }

    public RuntimeExceptionX(final String msg, final Throwable t) {
        super(msg, t);
        log.error(msg, t);
    }

}

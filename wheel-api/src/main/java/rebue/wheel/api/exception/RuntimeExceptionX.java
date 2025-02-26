package rebue.wheel.api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扩展RuntimeException，详细记录异常日志
 */
public class RuntimeExceptionX extends RuntimeException {
    private static final Logger log              = LoggerFactory.getLogger(RuntimeExceptionX.class);
    private static final long   serialVersionUID = 1L;

    private final Long          errorCode;

    public Long getErrorCode() {
        return errorCode;
    }

    public RuntimeExceptionX(final String msg) {
        this(null, msg);
    }

    public RuntimeExceptionX(final String msg, final Throwable t) {
        this(null, msg, t);
    }

    public RuntimeExceptionX(final Long errorCode, final String msg) {
        super(msg);
        this.errorCode = errorCode;
        log.error(errorCode == null ? msg : errorCode + msg);
    }

    public RuntimeExceptionX(final Long errorCode, final String msg, final Throwable t) {
        super(msg, t);
        this.errorCode = errorCode;
        log.error(errorCode == null ? msg : errorCode + msg, t);
    }

}
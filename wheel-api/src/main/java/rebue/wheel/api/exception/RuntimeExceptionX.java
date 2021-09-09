package rebue.wheel.api.exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 扩展RuntimeException，详细记录异常日志
 */
@Slf4j
@Data
public class RuntimeExceptionX extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String            errorCode;

    public RuntimeExceptionX(final String msg) {
        this(null, msg);
    }

    public RuntimeExceptionX(final String msg, final Throwable t) {
        this(null, msg, t);
    }

    public RuntimeExceptionX(String errorCode, final String msg) {
        super(msg);
        this.errorCode = errorCode;
        log.error(errorCode == null ? msg : errorCode + msg);
    }

    public RuntimeExceptionX(String errorCode, final String msg, final Throwable t) {
        super(msg, t);
        this.errorCode = errorCode;
        log.error(errorCode == null ? msg : errorCode + msg, t);
    }

}

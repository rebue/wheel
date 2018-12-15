package rebue.wheel.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 扩展RuntimeException，详细记录异常日志
 */
public class RuntimeExceptionX extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private static Logger     _log             = LoggerFactory.getLogger(RuntimeExceptionX.class);

    public RuntimeExceptionX(final String msg, final Throwable t) {
        super(msg, t);
        _log.error(msg, t);
    }

}

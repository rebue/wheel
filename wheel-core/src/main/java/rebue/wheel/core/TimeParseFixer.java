package rebue.wheel.core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 修正多线程下解析时间bug的类
 *
 * @author nnzbz
 */
public class TimeParseFixer {

    private final String                  _sPattern;
    private final ThreadLocal<DateFormat> _threadLocal = new ThreadLocal<>();

    public TimeParseFixer(String sPattern) {
        _sPattern = sPattern;
    }

    private DateFormat getDateFormat() {
        DateFormat df = _threadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(_sPattern);
            _threadLocal.set(df);
        }
        return df;
    }

    public String formatDate(Date date) throws ParseException {
        return getDateFormat().format(date);
    }

    public Date parse(String sDate) throws ParseException {
        return getDateFormat().parse(sDate);
    }

}
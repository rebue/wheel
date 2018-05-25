package rebue.wheel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OsUtils {
    private final static Logger _log = LoggerFactory.getLogger(OsUtils.class);

    public static boolean isWin() {
        String os = System.getProperty("os.name");
        _log.info("当前操作系统是: {}", os);
        return (os.toLowerCase().startsWith("win"));
    }
}

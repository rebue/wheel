package rebue.wheel.core;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OsUtils {
    public static boolean isWin() {
        final String os = System.getProperty("os.name");
        log.info("当前操作系统是: {}", os);
        return (os.toLowerCase().startsWith("win"));
    }
}

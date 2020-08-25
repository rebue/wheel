package rebue.wheel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUtilsTests {
    private final static Logger _log = LoggerFactory.getLogger(NetUtilsTests.class);

    @BeforeClass
    public static void init() {
        NetUtils.setFirstNetworkInterface("en0");
    }

//    @Test
    public void test01() {
        _log.info(NetUtils.getFirstIpOfLocalHost());
        _log.info(NetUtils.getFirstMacAddrOfLocalHost());
    }

    @Test
    public void test02() {
        final ExecutorService executorService = new ThreadPoolExecutor(200, 200, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new AbortPolicy());
        final int             iTaskCount      = 1;
        for (int i = 0; i < iTaskCount; i++) {
            executorService.execute(() -> {
                _log.info(NetUtils.getFirstIpOfLocalHost());
                _log.info(NetUtils.getFirstMacAddrOfLocalHost());
            });
        }
    }
}

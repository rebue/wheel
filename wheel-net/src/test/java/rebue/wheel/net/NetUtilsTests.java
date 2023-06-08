package rebue.wheel.net;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NetUtilsTests {

    @BeforeAll
    public static void init() {
        NetUtils.setFirstNetworkInterface("en0");
    }

    // @Test
    public void test01() {
        log.info(NetUtils.getFirstIpOfLocalHost());
        log.info(NetUtils.getFirstMacAddrOfLocalHost());
    }

    @Test
    public void test02() {
        final ExecutorService executorService = new ThreadPoolExecutor(200, 200, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new AbortPolicy());
        final int             iTaskCount      = 1;
        for (int i = 0; i < iTaskCount; i++) {
            executorService.execute(() -> {
                log.info(NetUtils.getFirstIpOfLocalHost());
                log.info(NetUtils.getFirstMacAddrOfLocalHost());
            });
        }
    }
}

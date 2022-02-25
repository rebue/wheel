package rebue.wheel.core;

import java.util.Date;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import rebue.wheel.core.idworker.ClockBackwardsException;
import rebue.wheel.core.idworker.IdWorker1;
import rebue.wheel.core.idworker.IdWorker2;
import rebue.wheel.core.idworker.IdWorker3;

@SpringBootTest
public class IdWorkerTest {

    protected static Logger _logger       = LoggerFactory.getLogger(IdWorkerTest.class);

    private final String    PRINT_RUNTIME = "%s:%dms(%s)";

    /**
     * 测试IdWorker1单线程循环1百万次的时间
     */
    @Test
    public void test01() {
        final Date dtStart = new Date();
        for (int i = 0; i < 1000000; i++) {
            IdWorker1.getId();
        }
        final Date dtOver = new Date();
        _logger.info(String.format(PRINT_RUNTIME, "test01", dtOver.getTime() - dtStart.getTime(),
                "测试IdWorker1单线程循环1百万次的时间"));
    }// test01

    /**
     * 测试IdWorker2单线程循环1千万次的时间
     */
    // @Test
    public void test02() {
        final IdWorker2 idWorker = new IdWorker2(2, 1);
        final Date      dtStart  = new Date();
        final Set<Long> randoms  = new HashSet<>();
        for (int i = 0; i < 10000000; i++) {
            try {
                final long id = idWorker.getId();
                if (!randoms.add(id)) {
                    _logger.error(String.format("重复的id:%d", id));
                }
            } catch (final ClockBackwardsException e) {
                _logger.error(e.getMessage());
            }
        }
        final Date dtOver = new Date();
        _logger.info(String.format(PRINT_RUNTIME, "test02", dtOver.getTime() - dtStart.getTime(),
                "测试IdWorker2单线程循环1千万次的时间"));
    }// test02

    /**
     * 测试IdWorker3单线程循环1千万次的时间
     */
    // @Test
    public void test03() {
        final IdWorker3 idWorker = new IdWorker3(2);
        final Date      dtStart  = new Date();
        final Set<Long> randoms  = new HashSet<>();
        for (int i = 0; i < 10000000; i++) {
            try {
                final long id = idWorker.getId();
                if (!randoms.add(id)) {
                    _logger.error(String.format("重复的id:%d", id));
                }
            } catch (final ClockBackwardsException e) {
                _logger.error(e.getMessage());
            }
        }
        final Date dtOver = new Date();
        _logger.info(String.format(PRINT_RUNTIME, "test03", dtOver.getTime() - dtStart.getTime(),
                "测试IdWorker3单线程循环1千万次的时间"));
    }// test03

    @Test
    public void test04() {
        final long end      = System.currentTimeMillis();
        long       sequence = 0L;
        while (System.currentTimeMillis() <= end) {
            sequence++;
        }
        _logger.info(String.format("%s测试1毫秒运行循环%d次", "test04", sequence));
    }

    /**
     * 测试IdWorker2多线程生成1千万次Id的时间
     */
    // @Test
    public void test05() {
        final IdWorker2       idWorker        = new IdWorker2(2, 1);
        final Date            dtStart         = new Date();
        final Queue<Long>     ids             = new ConcurrentLinkedQueue<>();
        final ExecutorService executorService = new ThreadPoolExecutor(200, 200, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), new AbortPolicy());
        final int             iTaskCount      = 10000000;
        final Runnable        runnable        = () -> {
                                                  final long id = idWorker.getId();
                                                  ids.add(id);
                                              };
        for (int i = 0; i < iTaskCount; i++) {
            executorService.execute(runnable);
        }
        while (ids.size() < iTaskCount) {
            try {
                Thread.sleep(1);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        final Date      dtOver  = new Date();
        final Set<Long> randoms = new HashSet<>();
        Long            id      = ids.poll();
        while (id != null) {
            if (!randoms.add(id)) {
                _logger.error(String.format("重复的id:%d", id));
            }
            id = ids.poll();
        }
        _logger.info(String.format(PRINT_RUNTIME, "test05", dtOver.getTime() - dtStart.getTime(),
                "测试IdWorker2多线程生成1千万次Id的时间"));
    }

    /**
     * 测试IdWorker3多线程生成1千万次Id的时间
     */
    // @Test
    public void test06() {
        final IdWorker3       idWorker        = new IdWorker3(2);
        final Date            dtStart         = new Date();
        final Queue<Long>     ids             = new ConcurrentLinkedQueue<>();
        final ExecutorService executorService = new ThreadPoolExecutor(2, 2, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), new AbortPolicy());
        final int             iTaskCount      = 10000000;
        final Runnable        runnable        = () -> {
                                                  final long id = idWorker.getId();
                                                  ids.add(id);
                                              };
        for (int i = 0; i < iTaskCount; i++) {
            executorService.execute(runnable);
        }
        while (ids.size() < iTaskCount) {
            try {
                Thread.sleep(1);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        final Date      dtOver  = new Date();
        final Set<Long> randoms = new HashSet<>();
        Long            id      = ids.poll();
        while (id != null) {
            if (!randoms.add(id)) {
                _logger.error(String.format("重复的id:%d", id));
            }
            id = ids.poll();
        }
        _logger.info(String.format(PRINT_RUNTIME, "test06", dtOver.getTime() - dtStart.getTime(),
                "测试IdWorker3多线程生成1千万次Id的时间"));
    }
}

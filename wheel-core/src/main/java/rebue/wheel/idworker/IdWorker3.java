package rebue.wheel.idworker;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>说明：</b>产生ID（最简洁快速的算法）
 * 
 * @since 1.7
 * @see #getId
 */
public final class IdWorker3 {

    private final static Logger _logger       = LoggerFactory.getLogger(IdWorker3.class);

    private final long          _twepoch      = 1413942127819L;

    /**
     * 在分布式中通过传入workerId来唯一区别不同的worker<br>
     * 传值范围应该在0-31之间(也就是5bit)<br>
     * 同一个服务不同的实例必须是不同的worker<br>
     */
    private final int           _workerId;

    private final static long   _workerIdBits = 5L;
    private final long          _sequenceBits = 17L;

    private final static long   _maxWorkerId  = -1L ^ -1L << _workerIdBits;

    public static long getMaxWorkerId() {
        return _maxWorkerId;
    }

    private final long    _sequenceMask       = -1L ^ -1L << _sequenceBits;

    private final long    _workerIdShift      = _sequenceBits;
    private final long    _timestampLeftShift = _sequenceBits + _workerIdBits;

    private AtomicInteger _sequence           = new AtomicInteger(-1);
    private AtomicLong    _lastTimestamp      = new AtomicLong(-1L);

    /**
     * @see #_workerId workerId
     */
    public IdWorker3(final int workerId) {
        if (workerId < 0 || workerId > _maxWorkerId)
            throw new IllegalArgumentException(
                    String.format("worker Id can't be greater than %d or less than 0", _maxWorkerId));
        this._workerId = workerId;
        _logger.info(
                String.format("worker starting. timestamp bits %d, worker id bits %d, sequence bits %d, workerid %d",
                        64 - _workerIdBits - _sequenceBits, _workerIdBits, _sequenceBits, workerId));
    }

    public IdWorker3() {
        this(0);
        _logger.warn("调用了IdWorker3的无参构造方法，如果在分布式环境中可能会造成生成的id重复，请按规范做好规划");
    }

    /**
     * 生成ID
     * 
     * @return 当前时间差(42bit)+workerId(5bit)+顺序号(17bit)
     * @throws ClockBackwardsException
     */
    public long getId() throws ClockBackwardsException {
        long lastTimestamp = _lastTimestamp.get();
        long timestamp = timeGen();

        if (timestamp == lastTimestamp) {
        } else if (timestamp > lastTimestamp)
            _lastTimestamp.compareAndSet(lastTimestamp, timestamp);
        // 系统时钟被倒退了
        else if (timestamp < lastTimestamp)
            throw new ClockBackwardsException(_lastTimestamp.get() - timestamp);

        return (timestamp - _twepoch) << _timestampLeftShift | _workerId << _workerIdShift
                | _sequence.incrementAndGet() & _sequenceMask;
    }

    /**
     * 生成ID字符串
     * 
     * @return Long转hex字符串
     * @see #getId
     */
    public String getIdStr() {
        return Long.toHexString(getId());
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}

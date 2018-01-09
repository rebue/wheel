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

    private final static Logger _log          = LoggerFactory.getLogger(IdWorker3.class);

    /**
     * 起始的时间
     */
    private final long          _twepoch      = 1413942127819L;

    /**
     * 在分布式中通过传入appId来唯一区别不同的worker<br>
     * 传值范围应该在0-31之间(也就是5bit)<br>
     * 同一个服务不同的实例必须是不同的worker<br>
     */
    private final int           _appid;

    private final static long   _appidBits    = 5L;
    private final long          _sequenceBits = 17L;

    private final static long   _maxAppid     = -1L ^ -1L << _appidBits;

    public static long getMaxAppId() {
        return _maxAppid;
    }

    private final long    _sequenceMask       = -1L ^ -1L << _sequenceBits;

    private final long    _appidShift         = _sequenceBits;
    private final long    _timestampLeftShift = _sequenceBits + _appidBits;

    private AtomicInteger _sequence           = new AtomicInteger(-1);
    private AtomicLong    _lastTimestamp      = new AtomicLong(-1L);

    /**
     * @see #_appid appId
     */
    public IdWorker3(final int appid) {
        _log.info("开始创建IdWorker3的对象，传入的appid为{}", appid);
        if (appid == 0)
            _log.warn("appid默认为0，如果在分布式环境中可能会造成生成的id重复，请按规范做好规划");
        if (appid < 0 || appid > _maxAppid)
            throw new IllegalArgumentException(String.format("appid不能大于%d或小于0", _maxAppid));
        this._appid = appid;
        _log.info("创建IdWorker3的实例开始工作，appid {}, timestamp bits {}, worker id bits {}, sequence bits {}", appid,
                64 - _appidBits - _sequenceBits, _appidBits, _sequenceBits);
    }

    public IdWorker3() {
        this(0);
    }

    /**
     * 生成ID
     * 
     * @return 当前时间差(42bit)+appId(5bit)+顺序号(17bit)
     * @throws ClockBackwardsException
     */
    public long getId() throws ClockBackwardsException {
        long lastTimestamp = _lastTimestamp.get();
        long timestamp = System.currentTimeMillis();

        if (timestamp == lastTimestamp) {
        } else if (timestamp > lastTimestamp)
            _lastTimestamp.compareAndSet(lastTimestamp, timestamp);
        // 系统时钟被倒退了
        else if (timestamp < lastTimestamp)
            throw new ClockBackwardsException(_lastTimestamp.get() - timestamp);

        return (timestamp - _twepoch) << _timestampLeftShift | _appid << _appidShift
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

}

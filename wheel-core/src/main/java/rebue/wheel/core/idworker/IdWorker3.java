package rebue.wheel.core.idworker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <b>说明：</b>产生ID（最简洁、灵活且快速的算法）
 *
 * @see #getId
 * @since 1.7
 */
@Slf4j
public final class IdWorker3 {

    /**
     * 起始的时间
     */
    private final long _twepoch = 1413942127819L;

    /**
     * 在分布式中通过传入节点Id来唯一区别不同的worker<br>
     * 默认传值范围应该在0-31之间(也就是5bit)<br>
     * 同一个服务不同的实例必须是不同的worker<br>
     */
    private final int _nodeId;

    private final long _nodeIdBits;
    private final long _sequenceBits;

    private final long _maxNodeId;

    private final long _sequenceMask;

    private final long _appidShift;
    private final long _timestampLeftShift;

    private final AtomicInteger _sequence      = new AtomicInteger(-1);
    private final AtomicLong    _lastTimestamp = new AtomicLong(-1L);

    /**
     * @see #_nodeId appId
     */
    public IdWorker3(final int nodeId, final long nodeIdBits) {
        log.info("开始创建IdWorker3的对象，传入的nodeId为{}，nodeId分配长度为{}", nodeId, nodeIdBits);
        _nodeId = nodeId;
        _nodeIdBits = nodeIdBits;
        _maxNodeId = -1L ^ -1L << _nodeIdBits;
        _sequenceBits = 22 - _nodeIdBits;
        _sequenceMask = -1L ^ -1L << _sequenceBits;
        _appidShift = _sequenceBits;
        _timestampLeftShift = _sequenceBits + _nodeIdBits;
        if (_nodeId == 0) {
            log.warn("节点Id默认为0，如果在分布式环境中可能会造成生成的id重复，请按规范做好规划");
        }
        if (_nodeId < 0 || _nodeId > _maxNodeId) {
            throw new IllegalArgumentException(String.format("节点Id不能大于%d或小于0", _maxNodeId));
        }
        log.info("创建IdWorker3的实例开始工作，node id: {}, timestamp bits: {}, worker id bits: {}, sequence bits: {}",
                _nodeId, 64 - _nodeIdBits - _sequenceBits, _nodeIdBits, _sequenceBits);
    }

    public IdWorker3(final int nodeId) {
        this(nodeId, 5);
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
    public Long getId() throws ClockBackwardsException {
        final long lastTimestamp = _lastTimestamp.get();
        final long timestamp     = System.currentTimeMillis();

        if (timestamp == lastTimestamp) {
        } else if (timestamp > lastTimestamp) {
            _lastTimestamp.compareAndSet(lastTimestamp, timestamp);
        } else if (timestamp < lastTimestamp) {
            throw new ClockBackwardsException(_lastTimestamp.get() - timestamp);
        }

        return timestamp - _twepoch << _timestampLeftShift | _nodeId << _appidShift | _sequence.incrementAndGet() & _sequenceMask;
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
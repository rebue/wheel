package rebue.wheel.idworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>功能：</b>产生Id（仿Twitter的算法）
 * 
 * @since 1.7
 * @see #getId
 * @see #getIdStr
 */
public class IdWorker2 {

	protected static Logger _logger = LoggerFactory.getLogger(IdWorker2.class);

	private final long _datacenterId;
	private final long _workerId;

	private final long _twepoch = 1413942127819L;

	private final long _datacenterIdBits = 5L;
	private final long _workerIdBits = 5L;
	private final long _sequenceBits = 12L;

	private final long _maxDatacenterId = -1L ^ -1L << _datacenterIdBits;
	private final long _maxWorkerId = -1L ^ -1L << _workerIdBits;

	private final long _sequenceMask = -1L ^ -1L << _sequenceBits;
	private final long _workerIdMask = -1L ^ -1L << _workerIdBits;
	private final long _datacenterIdMask = -1L ^ -1L << _datacenterIdBits;

	private final long _workerIdShift = _sequenceBits;
	private final long _datacenterIdShift = _sequenceBits + _workerIdBits;
	private final long _timestampLeftShift = _sequenceBits + _workerIdBits + _datacenterIdBits;

	private int _sequence = -1;
	private long _lastTimestamp = -1L;

	public IdWorker2(final long datacenterId, final long workerId) {
		if (datacenterId < 0 || datacenterId > _maxDatacenterId)
			throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", _maxDatacenterId));
		if (workerId < 0 || workerId > _maxWorkerId)
			throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", _maxWorkerId));
		_datacenterId = datacenterId;
		_workerId = workerId;
		_logger.info(String.format(
				"worker starting. timestamp bits %d, datacenter id bits %d, worker id bits %d, sequence bits %d, datacenterId %d, workerid %d", 64
						- _datacenterIdBits - _workerIdBits - _sequenceBits, _datacenterIdBits, _workerIdBits, _sequenceBits, datacenterId, workerId));
	}

	/**
	 * 生成ID
	 * 
	 * @return 当前时间戳(42bit)+datacenterId(5bit)+workerId(5bit)+顺���号(12bit)
	 * @throws ClockBackwardsException
	 */
	public synchronized long getId() throws ClockBackwardsException {
		long timestamp = timeGen();
		if (timestamp == _lastTimestamp) {
			_sequence = (int) ((_sequence + 1) & _sequenceMask);
			if (_sequence == 0)
				timestamp = tilNextMillis(_lastTimestamp);
		} else if (timestamp > _lastTimestamp) {
			_sequence = 0;
		}
		// 系统时钟被倒退了
		else
			throw new ClockBackwardsException(_lastTimestamp - timestamp);
		_lastTimestamp = timestamp;
		return (timestamp - _twepoch) << _timestampLeftShift | (_datacenterId & _datacenterIdMask) << _datacenterIdShift
				| (_workerId & _workerIdMask) << _workerIdShift | _sequence;
	}

	/**
	 * 生成ID字符串
	 * 
	 * @return Long转hex字符串
	 * @see #getId
	 */
	public String getIdStr() throws ClockBackwardsException {
		return Long.toHexString(getId());
	}

	private long tilNextMillis(final long lastTimestamp) {
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp)
			timestamp = timeGen();
		return timestamp;
	}

	private long timeGen() {
		return System.currentTimeMillis();
	}
}

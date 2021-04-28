package rebue.wheel.core.idworker;

/**
 * 在运行时调整系统时钟，使时间倒退，产生此异常
 */
public class ClockBackwardsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * @param backwords
	 *            时钟倒退多少毫秒
	 */
	public ClockBackwardsException(Long backwords) {
		super(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", backwords));
	}

}

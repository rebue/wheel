package rebue.wheel.core.idworker;

import java.util.UUID;

/**
 * 产生UUID形式的Id（不带“-”）
 */
public class IdWorker1 {

	/**
	 * @return UUID（不带“-”）
	 */
	public static String getId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}

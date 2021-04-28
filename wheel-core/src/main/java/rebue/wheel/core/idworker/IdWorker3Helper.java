package rebue.wheel.core.idworker;

/**
 * @author zbz
 *
 */
public class IdWorker3Helper {

    private static IdWorker3 idwork3 = new IdWorker3();

    public static long getId() {
        return idwork3.getId();
    }

    public static String nextIdStr() {
        return idwork3.getIdStr();
    }

}

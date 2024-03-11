package rebue.wheel.core.idworker;

/**
 * @author zbz
 */
public class IdWorker3Helper {

    private static final IdWorker3 idworker3 = new IdWorker3();

    public static long getId() {
        return idworker3.getId();
    }

    public static String nextIdStr() {
        return idworker3.getIdStr();
    }

}
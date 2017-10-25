package com.zboss.wheel.idworker;

/**
 * @author zbz
 *
 */
public class IdWorker3Helper {

    private static IdWorker3 idwork3 = new IdWorker3();

    public static long nextId() {
        return idwork3.getId();
    }

    public static String nextIdStr() {
        return idwork3.getIdStr();
    }

}

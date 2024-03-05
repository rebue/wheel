package rebue.wheel.core;

import org.apache.commons.lang3.StringUtils;

public class BytesConverter {
    private static final String[] units = {"B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};

    /**
     * long类型的字节大小转换为带单位的字符串
     *
     * @param size 字节大小
     * @return 带单位的字符串
     */
    public static String convert(long size) {
        float resize = Math.abs(size);
        int   i      = 0;
        for (; resize >= 1024 && i < 4; i++)
            resize /= 1024;
        return ((resize < 0) ? "-" : "") + String.format("%.2f" + units[i], resize);
    }

    /**
     * 带单位的字符串转换为long类型的字节大小
     *
     * @param size 带单位的字符串
     * @return long类型的字节大小
     */
    public static long convert(String size) {
        for (int i = 1; i < units.length; i++) {
            String unit         = units[i];
            String lastTwoBytes = StringUtils.right(size, 2);
            if (lastTwoBytes.equalsIgnoreCase(unit)) {
                return Long.parseLong(size.substring(0, size.length() - 2))
                        * new Double(Math.pow(1024, i)).longValue();
            }
            if (StringUtils.right(lastTwoBytes, 1).equalsIgnoreCase(unit.substring(0, 1))) {
                return Long.parseLong(size.substring(0, size.length() - 1))
                        * new Double(Math.pow(1024, i)).longValue();
            }
        }
        if (size.endsWith("B") || size.endsWith("b")) {
            return Long.parseLong(size.substring(0, size.length() - 1));
        }
        return Long.parseLong(size, 1);
    }
}

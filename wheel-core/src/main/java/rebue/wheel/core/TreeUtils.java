package rebue.wheel.core;

/**
 * 树结构工具
 */
public class TreeUtils {

    /**
     * 递增树编码，传入的树编码递增
     *
     * @param treeCode 树编码
     * @param size     递增大小
     * @param tcSize   树编码长度
     */
    public static String incrementalTreeCode(String treeCode, int size, int tcSize) {
        //为了不超过整形的长度需要截取最后一级的树编码递增
        int    treeCodeLength = treeCode.length();
        String treeCodeSuffix = treeCode.substring(treeCodeLength - tcSize, treeCodeLength);
        String treeCodePrefix = treeCode.substring(0, treeCodeLength - tcSize);

        int treeCodeInt = Integer.parseInt(treeCodeSuffix) + size;
        // 计算前导零的长度
        int width = treeCodeSuffix.length();
        // 补齐计算时treeCode前方0被截掉的位数
        return treeCodePrefix + String.format("%0" + width + "d", treeCodeInt);
    }

    /**
     * 递增树编码，传入的树编码递增
     *
     * @param treeCode 树编码
     * @param size     递增大小
     */
    public static String incrementalTreeCode(String treeCode, int size) {
        int treeCodeInt = Integer.parseInt(treeCode) + size;
        // 计算前导零的长度
        int width = treeCode.length();
        // 补齐计算时treeCode前方0被截掉的位数
        return String.format("%0" + width + "d", treeCodeInt);
    }
}

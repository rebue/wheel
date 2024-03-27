package rebue.wheel.core.db;

import rebue.wheel.api.util.RegexUtils;

import java.util.Arrays;
import java.util.List;

public class RemarkUtils {

    /**
     * 获取备注列表
     *
     * @param remark 备注
     * @return 备注列表
     */
    public static List<String> getRemarks(String remark) {
        return Arrays.asList(remark.split("\n"));
    }

    /**
     * 获取标题
     *
     * @param remark 数据库的备注
     * @return 标题
     */
    public static String getTitle(String remark) {
        return RegexUtils.findFirstLine(remark);
    }
}

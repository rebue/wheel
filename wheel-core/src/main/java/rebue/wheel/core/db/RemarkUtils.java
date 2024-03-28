package rebue.wheel.core.db;

import rebue.wheel.api.util.RegexUtils;

import java.util.ArrayList;
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
        List<String> remarks = Arrays.asList(remark.split("\n"));
        String       title   = remarks.get(0);
        String[]     split   = title.split("@");
        if (split.length > 1) {
            List<String> remarksTemp = new ArrayList<>(remarks);
            remarksTemp.set(0, getTitle(remark));
            for (int i = split.length - 1; i > 0; i--) {
                remarksTemp.add(1, "@" + split[i]);
            }
            return remarksTemp;
        } else {
            return remarks;
        }
    }

    /**
     * 获取标题
     *
     * @param remark 数据库的备注
     * @return 标题
     */
    public static String getTitle(String remark) {
        String title = RegexUtils.findFirstLine(remark);
        int    index = title.indexOf('@');
        if (index != -1) {
            title = title.substring(0, index);
        }
        return title;
    }
}

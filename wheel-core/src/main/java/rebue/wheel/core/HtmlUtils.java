package rebue.wheel.core;

import java.util.Map;
import java.util.Map.Entry;

public class HtmlUtils {

    /**
     * 自动提交表单
     */
    public static String autoPostByFormParams(String url, Map<String, Object> params) {
        String paramsContent = "";
        for (Entry<String, Object> entry : params.entrySet()) {
            paramsContent += "<input name=\"" + entry.getKey() + "\" value=\"" + entry.getValue() + "\" type=\"hidden\" />";
        }

        String html = "<!DOCTYPE HTML>\n"  //
                + "<html>\n" //
                + "<body>\n"  //
                + "正在跳转......"//
                + "<form name=\"form1\" method=\"post\" action=\"%s\">" //
                + "%s"//
                + "</form>" //
                + "<script>"//
                + "document.form1.submit();"//
                + "</script>" //
                + "</body>\n" //
                + "</html>";
        return String.format(html, url, paramsContent);
    }
}

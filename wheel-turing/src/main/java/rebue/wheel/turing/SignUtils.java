package rebue.wheel.turing;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

/**
 * 签名的相关应用
 */
public class SignUtils {
    /**
     * 签名2-微信签名
     * 在请求时，通过签名算法算出签名，并将其放入请求的Map中
     * 签名算法: 将所有参数排序生成新的Map-》拼接成key1=value2&amp;key2=value2....的字符串-》md5hex-》大写
     */
    public static void sign2(Map<String, String> requestParams, String signKey) {
        // 排序生成新的Map
        Map<String, String> sortMap = new TreeMap<>(requestParams);
        // 拼接成key1=value2&key2=value2....的字符串
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> item : sortMap.entrySet()) {
            if (StringUtils.isBlank(item.getValue()))
                continue;
            if (sb.length() > 0)
                sb.append("&");
            sb.append(item.getKey() + "=" + item.getValue());
        }
        sb.append("&key=" + signKey);
        // md5hex->大写->放入请求的Map中
        requestParams.put("sign", DigestUtils.md5AsHexStr(sb.toString().getBytes()).toUpperCase());
    }

    /**
     * 签名2-校验微信签名
     * 接收到的请求时，对请求的参数进行签名校验
     */
    public static boolean verify2(Map<String, String> requestParams, String signKey) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> sortMap = new TreeMap<>(requestParams);

        for (Entry<String, String> item : sortMap.entrySet()) {
            if (StringUtils.isBlank(item.getValue()))
                continue;
            if ("sign".equalsIgnoreCase(item.getKey())) {
                continue;
            }
            if (sb.length() > 0)
                sb.append("&");
            sb.append(item.getKey() + "=" + item.getValue());
        }
        sb.append("&key=" + signKey);
        String sign = DigestUtils.md5AsHexStr(sb.toString().getBytes()).toUpperCase();
        return sign.equalsIgnoreCase(sortMap.get("sign"));
    }
}

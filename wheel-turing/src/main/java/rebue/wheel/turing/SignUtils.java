package rebue.wheel.turing;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 签名的相关应用
 */
public class SignUtils {
    private final static Logger _log = LoggerFactory.getLogger(SignUtils.class);

    /**
     * 签名-签名
     * 在请求时，通过签名算法算出签名，并将其放入请求的Map中
     * 签名算法步骤:
     * 1. 将所有参数排序生成新的Map
     * 2. 参数列表添加key后，一起拼接成name1=value1&name2=value2....&key=xxx的字符串
     * 3. 将字符串md5hex并大写生成签名
     * 4. 将生成的签名添加入参数map中
     * 
     * @param requestParams
     *            请求的参数map
     * @param signKeyName
     *            拼接到键值对字符串后的签名key的名称
     * @param signKeyValue
     *            拼接到键值对字符串后的签名key的值
     * @param signParamName
     *            将生成的签名添加入参数map中的名称
     */
    public static void sign(Map<String, Object> requestParams, String signKeyName, String signKeyValue, String signParamName) {
        _log.info("计算签名: requestParams={},signKeyName={},signKeyValue={},signParamName={}", requestParams, signKeyName, signKeyValue, signParamName);
        // 1. 将所有参数排序生成新的Map
        Map<String, Object> sortMap = new TreeMap<>(requestParams);
        // 2. 参数列表添加key一起拼接成name1=value1&name2=value2....&key=xxx的字符串
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> item : sortMap.entrySet()) {
            if (StringUtils.isBlank((String) item.getValue()))
                continue;
            if (sb.length() > 0)
                sb.append("&");
            sb.append(item.getKey() + "=" + item.getValue());
        }
        sb.append("&" + signKeyName + "=" + signKeyValue);
        // 3. 将字符串md5hex并大写生成签名
        String sign = DigestUtils.md5AsHexStr(sb.toString().getBytes()).toUpperCase();
        _log.info("生成签名: {}", sign);
        // 4. 将生成的签名添加入参数map中
        requestParams.put(signParamName, sign);
    }

    /**
     * 签名-验证签名
     * 接收到的请求时，对请求的参数进行签名校验
     * 
     * @param requestParams
     *            接收到的请求参数map
     * @param signKeyName
     *            拼接到键值对字符串后的签名key的名称
     * @param signKeyValue
     *            拼接到键值对字符串后的签名key的值
     * @param signParamName
     *            将生成的签名添加入参数map中的名称
     */
    public static boolean verify(Map<String, Object> requestParams, String signKeyName, String signKeyValue, String signParamName) {
        _log.info("验证签名: requestParams={},signKeyName={},signKeyValue={},signParamName={}", requestParams, signKeyName, signKeyValue, signParamName);
        StringBuilder sb = new StringBuilder();
        Map<String, Object> sortMap = new TreeMap<>(requestParams);

        for (Entry<String, Object> item : sortMap.entrySet()) {
            if (StringUtils.isBlank((String) item.getValue()))
                continue;
            if (signParamName.equalsIgnoreCase(item.getKey())) {
                continue;
            }
            if (sb.length() > 0)
                sb.append("&");
            sb.append(item.getKey() + "=" + item.getValue());
        }
        sb.append("&" + signKeyName + "=" + signKeyValue);
        String sign = DigestUtils.md5AsHexStr(sb.toString().getBytes()).toUpperCase();
        _log.info("生成签名: {}", sign);
        return sign.equalsIgnoreCase((String) sortMap.get(signParamName));
    }

    /**
     * 签名2-通用签名
     * 在请求时，通过签名算法算出签名，并将其放入请求的Map中
     */
    public static void sign1(Map<String, Object> requestParams, String signKeyValue) {
        sign(requestParams, "SignKeyName", signKeyValue, "SignParamName");
    }

    /**
     * 签名2-验证通用签名
     * 接收到的请求时，对请求的参数进行签名验证
     */
    public static boolean verify1(Map<String, Object> requestParams, String signKeyValue) {
        return verify(requestParams, "SignKeyName", signKeyValue, "SignParamName");
    }

    /**
     * 签名2-微信支付签名
     * 在请求时，通过签名算法算出签名，并将其放入请求的Map中
     */
    public static void sign2(Map<String, Object> requestParams, String signKeyValue) {
        sign(requestParams, "key", signKeyValue, "sign");
    }

    /**
     * 签名2-验证微信支付签名
     * 接收到的请求时，对请求的参数进行签名验证
     */
    public static boolean verify2(Map<String, Object> requestParams, String signKeyValue) {
        return verify(requestParams, "key", signKeyValue, "sign");
    }
}

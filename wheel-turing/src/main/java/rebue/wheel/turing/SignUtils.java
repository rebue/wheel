package rebue.wheel.turing;

import java.util.List;
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
     * 2. 参数列表添加key后，一起拼接成name1=value1&amp;name2=value2....&amp;key=xxx的字符串
     * 3. 将字符串md5hex并大写生成签名
     * 4. 将生成的签名添加入参数map中
     * 
     * @param requestParams
     *            请求的参数map
     * @param signKeyParamName
     *            记录签名key的参数的名称
     * @param signKey
     *            签名key的值
     * @param signResultParamName
     *            记录签名结果的参数的名称
     * @param isAddTimeStamp
     *            是否添加时间戳增加破解难度
     */
    public static void sign(Map<String, Object> requestParams, String signKeyParamName, String signKey, String signResultParamName, boolean isAddTimeStamp) {
        _log.info("计算签名: signKeyParamName={},signKey={},signResultParamName={}\\r\\nrequestParams={}", signKeyParamName, signKey, signResultParamName, requestParams);
        // 是否增加时间戳
        if (isAddTimeStamp) {
            requestParams.put("signTimestamp", Long.valueOf(System.currentTimeMillis()));
        }
        // 1. 将所有参数排序生成新的Map
        Map<String, Object> sortMap = new TreeMap<>(requestParams);
        // 2. 参数列表添加key一起拼接成name1=value1&name2=value2....&key=xxx的字符串
        StringBuilder sb = new StringBuilder();
        for (Entry<String, Object> item : sortMap.entrySet()) {
            // 排除已经签过名的参数
            if (item.getKey().equals(signResultParamName))
                continue;
            if (item.getValue() == null)
                continue;
            if (item.getValue() instanceof String && StringUtils.isBlank((String) item.getValue()))
                continue;
            if (sb.length() > 0)
                sb.append("&");
            sb.append(item.getKey() + "=" + item.getValue());
        }
        // 增加签名的key
        sb.append("&" + signKeyParamName + "=" + signKey);
        String src = sb.toString();
        _log.info("需要签名的字符串: {}", src);
        // 3. 将字符串md5hex并大写生成签名
        String sign = DigestUtils.md5AsHexStr(src.getBytes()).toUpperCase();
        _log.info("生成签名: {}", sign);
        // 4. 将生成的签名添加入参数map中
        requestParams.put(signResultParamName, sign);
    }

    /**
     * 签名-验证签名
     * 接收到的请求时，对请求的参数进行签名校验
     * 
     * @param requestParams
     *            接收到的请求参数map
     * @param signKeyParamName
     *            记录签名key的参数名称
     * @param signKey
     *            签名key的值
     * @param signResultParamName
     *            记录签名结果的参数名称
     * @param isAddTimeStamp
     *            是否添加时间戳增加破解难度
     */
    public static boolean verify(Map<String, ?> requestParams, String signKeyParamName, String signKey, String signResultParamName, boolean isAddTimeStamp) {
        _log.info("验证签名: signKeyParamName={},signKey={},signResultParamName={}\r\nrequestParams={}", signKeyParamName, signKey, signResultParamName, requestParams);
        StringBuilder sb = new StringBuilder();
        Map<String, ?> sortMap = new TreeMap<>(requestParams);
        boolean hasTimeStamp = false;
        String uncheckedSign = null;
        for (Entry<String, ?> item : sortMap.entrySet()) {
            String value = null;
            if (item.getValue() == null)
                continue;
            if (item.getValue() instanceof String) {
                if (StringUtils.isBlank((String) item.getValue()))
                    continue;
                // 签名的参数
                if (signResultParamName.equals(item.getKey())) {
                    uncheckedSign = (String) item.getValue();
                    // 排除签名的参数不加入计算签名
                    continue;
                }
            } else if (item.getValue() instanceof String[]) {
                String[] values = (String[]) item.getValue();
                if (values.length == 0 || StringUtils.isBlank(values[0]))
                    continue;
                // 签名的参数
                if (signResultParamName.equals(item.getKey())) {
                    uncheckedSign = values[0];
                    // 排除签名的参数不加入计算签名
                    continue;
                }
                value = values[0];
            } else if (item.getValue() instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<String> values = (List<String>) item.getValue();
                if (values.isEmpty() || StringUtils.isBlank(values.get(0)))
                    continue;
                // 签名的参数
                if (signResultParamName.equals(item.getKey())) {
                    uncheckedSign = values.get(0);
                    // 排除签名的参数不加入计算签名
                    continue;
                }
                value = values.get(0);
            }
            if (value == null) {
                value = item.getValue().toString();
            }
            if (sb.length() > 0)
                sb.append("&");
            sb.append(item.getKey() + "=" + value);

            // 要求有时间戳时判断是否收到此参数
            if (isAddTimeStamp) {
                if ("signTimestamp".equals(item.getKey())) {
                    hasTimeStamp = true;
                }
            }
        }
        if (uncheckedSign == null) {
            _log.error("验证签名失败: 没有签名参数");
            return false;
        }
        // 要求有时间戳时判断有没有收到此参数
        if (isAddTimeStamp && !hasTimeStamp) {
            _log.error("验证签名失败: 没有时间戳");
            return false;
        }
        sb.append("&" + signKeyParamName + "=" + signKey);
        String src = sb.toString();
        _log.info("需要签名的字符串: {}", src);
        String sign = DigestUtils.md5AsHexStr(src.getBytes()).toUpperCase();
        _log.info("计算出正确的签名: {}", sign);
        if (sign.equals(uncheckedSign)) {
            _log.error("签名正确");
            return true;
        } else {
            _log.error("签名不正确: {}", uncheckedSign);
            return false;
        }
    }

    /**
     * 签名1-通用签名
     * 在请求时，通过签名算法算出签名，并将其放入请求的Map中
     * 
     * @param requestParams
     *            请求的参数map
     * @param signKey
     *            签名key的值
     */
    public static void sign1(Map<String, Object> requestParams, String signKey) {
        sign(requestParams, "signKey", signKey, "signResult", true);
    }

    /**
     * 签名1-验证通用签名
     * 接收到的请求时，对请求的参数进行签名验证
     * 
     * @param requestParams
     *            接收到的请求参数map
     * @param signKey
     *            签名key的值
     */
    public static boolean verify1(Map<String, ?> requestParams, String signKey) {
        return verify(requestParams, "signKey", signKey, "signResult", true);
    }

    /**
     * 签名2-微信支付签名
     * 在请求时，通过签名算法算出签名，并将其放入请求的Map中
     * 
     * @param requestParams
     *            请求的参数map
     * @param signKey
     *            签名key的值
     */
    public static void sign2(Map<String, Object> requestParams, String signKey) {
        sign(requestParams, "key", signKey, "sign", false);
    }

    /**
     * 签名2-验证微信支付签名
     * 接收到的请求时，对请求的参数进行签名验证
     * 
     * @param requestParams
     *            接收到的请求参数map
     * @param signKey
     *            签名key的值
     */
    public static boolean verify2(Map<String, ?> requestParams, String signKey) {
        return verify(requestParams, "key", signKey, "sign", false);
    }
}

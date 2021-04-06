package rebue.wheel.turing;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 签名的相关应用
 */
@Slf4j
public class SignUtils {
    /**
     * 签名时间戳Key的名称
     */
    private final static String SIGN_TIMESTAMP_KEY_PARAM_NAME = "signTimestamp";

    /**
     * 拼接请求参数
     * 
     * 拼接步骤:
     * 1. 将所有参数排序生成新的Map
     * 2. 如果参数的值为空不参与签名
     * 3. 拼接成类似 name1=value1&amp;name2=value2....&amp;nameN=valueN 的字符串
     * 
     * @param requestParams 请求的参数map
     * 
     * @return 拼接完成后的字符串
     */
    public static StringJoiner concatRequestParams(final Map<String, Object> requestParams) {
        final StringBuilder sb1 = new StringBuilder();
        try {
            sb1.append("拼接请求参数:");
            requestParams.forEach((key, value) -> sb1.append("\r\n*        ").append(key).append(": ").append(value));
            // 1. 将所有参数排序生成新的Map
            final Map<String, Object> sortMap = new TreeMap<>(requestParams);
            // 2. 参数列表添加key一起拼接成name1=value1&name2=value2....&key=xxx的字符串
            final StringJoiner        sj      = new StringJoiner("&");
            for (final Entry<String, Object> item : sortMap.entrySet()) {
                // 排除值为null或为空字符串的参数
                if (item.getValue() == null && StringUtils.isBlank(item.getValue().toString())) {
                    continue;
                }
                sj.add(item.getKey() + "=" + item.getValue());
            }
            return sj;
        } finally {
            log.debug(sb1.toString());
        }
    }

    /**
     * 签名-获取签名的结果
     *
     * 签名算法步骤:
     * 1. 将所有参数排序生成新的Map
     * 2. 参数列表添加key后，一起拼接成name1=value1&amp;name2=value2....&amp;key=xxx的字符串
     * 3. 将字符串摘要并大写生成签名: MD5-md5hex;SM3_WITH_SM2-Sm3WithSm2
     *
     * @param requestParams       请求的参数map
     * @param signKeyParamName    记录签名key的参数的名称
     * @param signKey             签名key的值
     * @param signResultParamName 记录签名结果的参数的名称
     * @param isAddTimeStamp      是否添加时间戳增加破解难度
     */
    public static String getSignValue(final Map<String, Object> requestParams, final String signKeyParamName, final String signKey,
            final String signResultParamName,
            final boolean isAddTimeStamp) {
        final StringBuilder sb1 = new StringBuilder();
        try {
            sb1.append("\r\n----------------------- 签名 -----------------------\r\n");
            sb1.append("* 参数:");
            sb1.append("\r\n*    signKeyParamName: ");
            sb1.append(signKeyParamName);
            sb1.append("\r\n*    signKey: ");
            sb1.append(signKey);
            sb1.append("\r\n*    signResultParamName: ");
            sb1.append(signResultParamName);
            sb1.append("\r\n*    requestParams: ");
            requestParams.forEach((key, value) -> sb1.append("\r\n*        ").append(key).append(": ").append(value));
            // 是否增加时间戳
            if (isAddTimeStamp && !requestParams.containsKey(SIGN_TIMESTAMP_KEY_PARAM_NAME)) {
                requestParams.put(SIGN_TIMESTAMP_KEY_PARAM_NAME, Long.valueOf(System.currentTimeMillis()));
            }
            // 1. 将所有参数排序生成新的Map
            final Map<String, Object> sortMap = new TreeMap<>(requestParams);
            // 2. 参数列表添加key一起拼接成name1=value1&name2=value2....&key=xxx的字符串
            final StringBuilder       sb2     = new StringBuilder();
            for (final Entry<String, Object> item : sortMap.entrySet()) {
                // 排除已经签过名的参数
                if (item.getKey().equals(signResultParamName)) {
                    continue;
                }
                // 排除值为null或为空字符串的参数
                if (item.getValue() == null && StringUtils.isBlank(item.getValue().toString())) {
                    continue;
                }
                if (sb2.length() > 0) {
                    sb2.append("&");
                }
                sb2.append(item.getKey() + "=" + item.getValue());
            }
            // 增加签名的key
            sb2.append("&" + signKeyParamName + "=" + signKey);
            final String src = sb2.toString();
            sb1.append("\r\n*    需要签名的字符串:");
            sb1.append(src);
            // 3. 将字符串md5hex并大写生成签名
            final String signResult = DigestUtils.md5AsHexStr(src.getBytes()).toUpperCase();
            sb1.append("\r\n*    生成的签名:");
            sb1.append(signResult);
            sb1.append(StringUtils.rightPad("\r\n---------------------------------------------------", 100));
            return signResult;
        } finally {
            log.debug(sb1.toString());
        }
    }

    /**
     * 签名-签名
     * 在请求时，通过签名算法算出签名，并将其放入请求的Map中
     *
     * @param signAlgorithm       签名算法
     * @param requestParams       请求的参数map
     * @param signKeyParamName    记录签名key的参数的名称(对称加密才需要此参数，非对称加密不需要)
     * @param signKey             签名key的值(非对称加密须传私钥)
     * @param signResultParamName 记录签名结果的参数的名称
     * @param isAddTimeStamp      是否添加时间戳增加破解难度
     * @param userId              用户ID(SM3_WITH_SM2需要此参数)
     */
    public static void sign(final String signAlgorithm, final Map<String, Object> requestParams, final String signKeyParamName,
            final String signKey, final String signResultParamName, final boolean isAddTimeStamp, final Long userId) {
        // 是否增加时间戳
        if (isAddTimeStamp) {
            requestParams.put(SIGN_TIMESTAMP_KEY_PARAM_NAME, Long.valueOf(System.currentTimeMillis()));
        }

        // 是否非对称加密(如果不是，则为对称加密)
        boolean isAsymmetricEncryption;
        switch (signAlgorithm) {
        case "MD5":
            isAsymmetricEncryption = false;
            break;
        case "SM3_WITH_SM2":
            isAsymmetricEncryption = true;
            break;
        default:
            throw new RuntimeException("不支持的签名算法");
        }

        // 拼接参数
        final StringJoiner sj                 = concatRequestParams(requestParams);
        // 对称加密须将key放入字符串中来签名，非对称加密则不用
        final String       concatenatedString = isAsymmetricEncryption ? sj.toString() : sj.add(signKeyParamName + "=" + signKey).toString();

        // 签名
        String signResult = null;
        try {
            if ("MD5".equals(signAlgorithm)) {
                signResult = DigestUtils.md5AsHexStr(concatenatedString.getBytes(StandardCharsets.UTF_8)).toUpperCase();
            }
            else if ("SM3_WITH_SM2".equals(signAlgorithm)) {
                signResult = new String(Sm2Utils.signSm3WithSm2(concatenatedString.getBytes(StandardCharsets.UTF_8),
                        userId.toString().getBytes(), Sm2Utils.getPrivateKeyFromString(signKey)),
                        StandardCharsets.UTF_8);
            }
            else {
                throw new RuntimeException("不支持的签名算法");
            }
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException("不支持的字符编码格式");
        } catch (final Exception e) {
            throw new RuntimeException("签名计算错误");
        }
        // 将生成的签名添加入参数map中
        requestParams.put(signResultParamName, signResult);
    }

    /**
     * 签名-验证签名
     * 接收到的请求时，对请求的参数进行签名校验
     *
     * @param requestParams
     *                            接收到的请求参数map
     * @param signKeyParamName
     *                            记录签名key的参数名称
     * @param signKey
     *                            签名key的值
     * @param signResultParamName
     *                            记录签名结果的参数名称
     * @param isAddTimeStamp
     *                            是否添加时间戳增加破解难度
     */
    public static boolean verify(final Map<String, Object> requestParams, final String signKeyParamName, final String signKey, final String signResultParamName,
            final boolean isAddTimeStamp) {
        if (requestParams == null || requestParams.isEmpty()) {
            log.warn("验证签名失败: 没有参数");
            return false;
        }
        final Object originSignResult = requestParams.get(signResultParamName);
        if (originSignResult == null || StringUtils.isBlank(originSignResult.toString())) {
            log.warn("验证签名失败: 没有签名参数");
            return false;
        }
        // 要求有时间戳时判断有没有收到此参数
        if (isAddTimeStamp) {
            final Object signTimestamp = requestParams.get(SIGN_TIMESTAMP_KEY_PARAM_NAME);
            if (signTimestamp == null || StringUtils.isBlank(signTimestamp.toString())) {
                log.warn("验证签名失败: 没有时间戳");
                return false;
            }
            final long longSignTimestamp = Long.parseLong(signTimestamp.toString());
            final long now               = System.currentTimeMillis();
            if (longSignTimestamp < now - 5 * 60 * 1000) {
                log.warn("验证签名失败: 时间戳太旧");
                return false;
            }
            if (longSignTimestamp > now + 1 * 60 * 1000) {
                log.warn("验证签名失败: 时间戳太新");
                return false;
            }
        }

        final String correctSignResult = getSignValue(requestParams, signKeyParamName, signKey, signResultParamName, isAddTimeStamp);
        log.debug("原始签名: {}，正确签名: {}", originSignResult, correctSignResult);
        if (correctSignResult.equals(originSignResult.toString())) {
            log.debug("签名正确");
            return true;
        }
        else {
            log.warn("签名不正确: {}", originSignResult);
            return false;
        }
    }

    /**
     * 签名1-通用签名
     * 在请求时，通过签名算法算出签名，并将其放入请求的Map中
     *
     * @param requestParams
     *                      请求的参数map
     * @param signKey
     *                      签名key的值
     */
    public static void sign1(final Map<String, Object> requestParams, final String signKey) {
        sign("MD5", requestParams, "signKey", signKey, "signResult", true, null);
    }

    /**
     * 签名1-验证通用签名
     * 接收到的请求时，对请求的参数进行签名验证
     *
     * @param requestParams
     *                      接收到的请求参数map
     * @param signKey
     *                      签名key的值
     */
    public static boolean verify1(final Map<String, Object> requestParams, final String signKey) {
        return verify(requestParams, "signKey", signKey, "signResult", true);
    }

    /**
     * 签名2-微信支付签名
     * 在请求时，通过签名算法算出签名，并将其放入请求的Map中
     *
     * @param requestParams
     *                      请求的参数map
     * @param signKey
     *                      签名key的值
     */
    public static void sign2(final Map<String, Object> requestParams, final String signKey, final Long userId) {
        sign("MD5", requestParams, "key", signKey, "sign", false, null);
    }

    /**
     * 签名2-验证微信支付签名
     * 接收到的请求时，对请求的参数进行签名验证
     *
     * @param requestParams
     *                      接收到的请求参数map
     * @param signKey
     *                      签名key的值
     */
    public static boolean verify2(final Map<String, Object> requestParams, final String signKey) {
        return verify(requestParams, "key", signKey, "sign", false);
    }

    public static void sign3(final Map<String, Object> requestParams, final String signKey, final Long userId) {
        sign("SM3_WITH_SM2", requestParams, "key", signKey, "sign", false, userId);
    }

}

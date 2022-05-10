package rebue.wheel.turing;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
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
     * 算法是否是非对称加密
     *
     * @param signAlgorithm 签名算法
     *
     * @return 返回算法是否是非对称加密，如果否则说明是对称加密
     */
    public static boolean isAsymmetricEncryption(final String signAlgorithm) {
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
        return isAsymmetricEncryption;
    }

    /**
     * 拼接请求参数
     *
     * 拼接步骤:
     * 1. 将所有参数排序生成新的Map
     * 2. 如果参数的值为空不参与签名
     * 3. 拼接成类似 name1=value1&amp;name2=value2....&amp;nameN=valueN 的字符串
     *
     * @param signAlgorithm    签名算法
     * @param requestParams    请求的参数map
     * @param signKeyParamName 记录签名key的参数的名称(对称加密才需要此参数，非对称加密不需要)
     * @param signKey          签名key的值(对称加密才需要此参数，非对称加密不需要；非对称加密须传私钥)
     *
     * @return 拼接完成后的字符串
     */
    public static String concatRequestParams(final String signAlgorithm, final Map<String, Object> requestParams,
            final String signKeyParamName, final String signKey) {
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
                if (item.getValue() == null || StringUtils.isBlank(item.getValue().toString())) {
                    continue;
                }
                sj.add(item.getKey() + "=" + item.getValue());
            }
            // 对称加密须将key放入字符串中来签名，非对称加密则不用
            return isAsymmetricEncryption(signAlgorithm) ? sj.toString() : sj.add(signKeyParamName + "=" + signKey).toString();
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
    @Deprecated
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
            final String signResult = DigestUtils.md5ToHexStr(src.getBytes()).toUpperCase();
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
     * 最后会以signResultParamName为key，生成的签名为value添加到requestParams中
     *
     * @param signAlgorithm       签名算法
     * @param requestParams       请求的参数map
     * @param keyParamName        请求参数中key的参数名(对称加密需要此参数，非对称加密不需要)
     * @param key                 签名key的值(对称加密需要此参数，非对称加密不需要)
     * @param privateKey          私钥(非对称加密需要此参数，对称加密不需要)
     * @param signResultParamName 记录签名结果的参数的名称
     * @param isAddTimeStamp      是否添加时间戳增加破解难度
     * @param userIdParamName     请求参数中用户ID的参数名(SM3_WITH_SM2需要此参数)
     *
     */
    public static void sign(final String signAlgorithm, final Map<String, Object> requestParams, final String keyParamName,
            final String key, final PrivateKey privateKey, final String signResultParamName,
            final boolean isAddTimeStamp, final String userIdParamName) {
        if (requestParams == null) {
            log.warn("签名失败: requestParams不能为空");
            throw new RuntimeException("签名失败: requestParams不能为空");
        }

        // 从请求参数中获取userId并检查是否为空
        String userId = null;
        if ("SM3_WITH_SM2".equals(signAlgorithm)) {
            final Object value = requestParams.get(userIdParamName);
            if (value == null) {
                log.warn("签名失败: requestParams中没有{}", userIdParamName);
                throw new RuntimeException("签名失败: requestParams没有" + userIdParamName);
            }
            userId = value.toString();
            if (StringUtils.isBlank(userId)) {
                log.warn("签名失败: requestParams中{}为空值", userIdParamName);
                throw new RuntimeException("签名失败: requestParams中" + userIdParamName + "为空值");
            }
        }

        // 是否增加时间戳
        if (isAddTimeStamp) {
            requestParams.put(SIGN_TIMESTAMP_KEY_PARAM_NAME, Long.valueOf(System.currentTimeMillis()));
        }

        // 拼接参数
        final String concatenatedString = concatRequestParams(signAlgorithm, requestParams, keyParamName, key);

        // 签名
        String signResult = null;
        try {
            if ("MD5".equals(signAlgorithm)) {
                signResult = DigestUtils.md5ToHexStr(concatenatedString.getBytes(StandardCharsets.UTF_8)).toUpperCase();
            }
            else if ("SM3_WITH_SM2".equals(signAlgorithm)) {
                signResult = Base64.getUrlEncoder().encodeToString(
                        Sm2Utils.signSm3WithSm2(concatenatedString.getBytes(StandardCharsets.UTF_8), userId.toString().getBytes(), privateKey));
            }
            else {
                throw new RuntimeException("不支持的签名算法");
            }
        } catch (final Exception e) {
            throw new RuntimeException("签名计算错误", e);
        }
        // 将生成的签名添加入参数map中
        requestParams.put(signResultParamName, signResult);
    }

    /**
     * 签名-验证签名
     * 接收到的请求时，对请求的参数进行签名校验
     *
     * @param signAlgorithm       签名算法
     * @param requestParams       请求的参数map
     * @param keyParamName        请求参数中key的参数名(对称加密需要此参数，非对称加密不需要)
     * @param key                 签名key的值(对称加密需要此参数，非对称加密不需要)
     * @param publicKey           公钥(非对称加密需要此参数，对称加密不需要)
     * @param signResultParamName 记录签名结果的参数的名称
     * @param isAddTimeStamp      是否添加时间戳增加破解难度
     * @param userIdParamName     请求参数中用户ID的参数名(SM3_WITH_SM2需要此参数)
     *
     * @return 返回签名是否正确
     */
    public static boolean verify(final String signAlgorithm, final Map<String, Object> requestParams, final String keyParamName,
            final String key, final PublicKey publicKey, final String signResultParamName,
            final boolean isAddTimeStamp, final String userIdParamName) {
        boolean result = false;

        if (requestParams == null || requestParams.isEmpty()) {
            log.warn("验证签名失败: 没有参数");
            return false;
        }

        // 从请求参数中获取userId并检查是否为空
        String userId = null;
        if ("SM3_WITH_SM2".equals(signAlgorithm)) {
            final Object value = requestParams.get(userIdParamName);
            if (value == null) {
                log.warn("验证签名失败: requestParams中没有{}", userIdParamName);
                throw new RuntimeException("验证签名失败: requestParams没有" + userIdParamName);
            }
            userId = value.toString();
            if (StringUtils.isBlank(userId)) {
                log.warn("验证签名失败: requestParams中{}为空值", userIdParamName);
                throw new RuntimeException("验证签名失败: requestParams中" + userIdParamName + "为空值");
            }
        }

        // 移除并获取原始的签名
        final Object originSignResult = requestParams.remove(signResultParamName);
        if (originSignResult == null || StringUtils.isBlank(originSignResult.toString())) {
            log.warn("验证签名失败: 没有签名参数");
            return false;
        }
        log.debug("原始签名: {}", originSignResult);

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
            if (longSignTimestamp > now + 5 * 60 * 1000) {
                log.warn("验证签名失败: 时间戳太新");
                return false;
            }
        }

        // 拼接参数
        final String concatenatedString = concatRequestParams(signAlgorithm, requestParams, keyParamName, key);

        String       correctSignResult  = null;
        try {
            if ("MD5".equals(signAlgorithm)) {
                correctSignResult = DigestUtils.md5ToHexStr(concatenatedString.getBytes(StandardCharsets.UTF_8)).toUpperCase();
                log.debug("正确签名: {}", correctSignResult);
                result = correctSignResult.equals(originSignResult.toString());
            }
            else if ("SM3_WITH_SM2".equals(signAlgorithm)) {
                result = Sm2Utils.verifySm3WithSm2(concatenatedString.getBytes(StandardCharsets.UTF_8),
                        userId.toString().getBytes(), Base64.getUrlDecoder().decode(originSignResult.toString()), publicKey);
            }
            else {
                throw new RuntimeException("不支持的签名算法");
            }
        } catch (final Exception e) {
            throw new RuntimeException("签名计算错误", e);
        }

        if (result) {
            log.debug("签名正确");
        }
        else {
            log.warn("签名不正确: {}", originSignResult);
        }
        return result;
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
        sign("MD5", requestParams, "signKey", signKey, null, "signResult", true, null);
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
        return verify("MD5", requestParams, "signKey", signKey, null, "signResult", true, null);
    }

    /**
     * 签名2-微信支付签名
     * 在请求时，通过签名算法算出签名，并将其放入请求的Map中
     *
     * @param requestParams 请求的参数map
     * @param signKey       签名key的值
     */
    public static void sign2(final Map<String, Object> requestParams, final String signKey, final Long userId) {
        sign("MD5", requestParams, "key", signKey, null, "sign", false, null);
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
        return verify("MD5", requestParams, "key", signKey, null, "sign", false, null);
    }

    /**
     * 签名3-国密签名
     * 在请求时，通过签名算法算出签名，并将其放入请求的Map中
     *
     * @param requestParams 请求的参数map
     * @param privateKey    私钥
     * @param userId        用户ID
     */
    public static void sign3(final Map<String, Object> requestParams, final PrivateKey privateKey) {
        sign("SM3_WITH_SM2", requestParams, null, null, privateKey, "signResult", true, "signId");
    }

    /**
     * 签名3-验证国密签名
     * 接收到的请求时，对请求的参数进行签名验证
     *
     * @param requestParams 接收到的请求参数map
     * @param publicKey     公钥
     * @param userId        用户ID
     */
    public static boolean verify3(final Map<String, Object> requestParams, final PublicKey publicKey) {
        return verify("SM3_WITH_SM2", requestParams, null, null, publicKey, "signResult", true, "signId");
    }

}

package rebue.wheel.api.desensitize;

import org.apache.commons.lang3.StringUtils;
import rebue.wheel.api.util.RegexUtils;

import java.util.function.Function;

/**
 * 脱敏策略
 */
public enum DesensitizeStrategy {
    /**
     * 自定义
     */
    CUSTOM(str -> null),
    /**
     * 名称
     */
    NAME(str -> {
        if (StringUtils.isBlank(str)) return "*";
        str = str.trim();
        return str.replaceAll("(\\S)\\S*", "$1*");
    }),

    /**
     * 电话号码
     */
    TEL(str -> {
        if (StringUtils.isBlank(str)) return "*";
        str = str.trim();
        return str.replaceAll("\\S*(\\d{3})", "****$1");
    }),
    /**
     * 手机号码
     */
    MOBILE(str -> {
        if (StringUtils.isBlank(str)) return "*";
        str = str.trim();
        if (str.length() > 8)
            return str.replaceAll("(\\d{3})\\d*(\\d{4})", "$1****$2");
        else
            return str.replaceAll("\\S*(\\d{3})", "$1****$1");
    }),
    /**
     * 电子邮箱
     */
    EMAIL(str -> {
        if (StringUtils.isBlank(str)) return "*";
        str = str.trim();
        if (!RegexUtils.matchEmail(str)) return "*";
        return str.replaceAll("(\\S{3})\\S*@(\\S*)", "$1***@$2");
    }),
    /**
     * 身份证号
     */
    ID_CARD(str -> {
        if (StringUtils.isBlank(str)) return "*";
        str = str.trim();
        if (!RegexUtils.matchIdCard(str)) return "*";
        return str.replaceAll("(\\d{3})\\d{12}(\\w{3})", "$1****$2");
    }),
    /**
     * 密钥(不序列化)
     */
    KEY(str -> null),
    /**
     * 车牌号码
     */
    CAR_LICENSE(str -> {
        if (StringUtils.isBlank(str)) return "********";
        str = str.trim();
        return str.replaceAll("(\\d{2})\\d{2}(\\d{3})", "$1**$2");
    }),
    /**
     * 银行卡号
     */
    BANK_CARD_NO(str -> {
        if (StringUtils.isBlank(str)) return "********";
        str = str.trim();
        return str.replaceAll("(\\d{4})\\d*(\\d{4})", "$1****$2");
    }),
    /**
     * 地址
     */
    ADDRESS(str -> "********");

    private Function<String, String> desensitizer;

    DesensitizeStrategy(Function<String, String> desensitizer) {
        this.desensitizer = desensitizer;
    }

    /**
     * Gets desensitizer.
     *
     * @return the desensitizer
     */
    public Function<String, String> getDesensitizer() {
        return desensitizer;
    }
}

package rebue.wheel.api.strategy;

import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import rebue.wheel.core.util.RegexUtils;

/**
 * 脱敏策略
 */
public enum DesensitizeStrategy {
    /**
     * Username sensitive strategy.
     */
    USERNAME(str -> {
        if (StringUtils.isBlank(str)) return "";
        str = str.trim();
        return str.replaceAll("(\\S)\\S*", "$1*");
    }),

    /**
     * Telephone sensitive type.
     */
    TEL(str -> {
        if (StringUtils.isBlank(str)) return "";
        str = str.trim();
        return str.replaceAll("\\S*(\\d{3})", "****$1");
    }),
    /**
     * Mobile sensitive type.
     */
    MOBILE(str -> {
        if (StringUtils.isBlank(str)) return "";
        str = str.trim();
        return str.replaceAll("(\\d{3})\\d{5}(\\d{3})", "$1*****$2");
    }),
    /**
     * Email sensitive type.
     */
    EMAIL(str -> {
        if (StringUtils.isBlank(str)) return "";
        str = str.trim();
        if (!RegexUtils.matchEmail(str)) return "*";
        return str.replaceAll("(\\S)\\S*@(\\S*)", "$1***@$2");
    }),
    /**
     * Id card sensitive type.
     */
    ID_CARD(str -> {
        if (StringUtils.isBlank(str)) return "";
        str = str.trim();
        if (!RegexUtils.matchIdCard(str)) return "*";
        return str.replaceAll("(\\d{3})\\d{12}(\\w{3})", "$1****$2");
    }),
    /**
     * Address sensitive type.
     */
    ADDRESS(str -> "********");

    private final Function<String, String> desensitizer;

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

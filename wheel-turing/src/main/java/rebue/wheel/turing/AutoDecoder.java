package rebue.wheel.turing;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import rebue.wheel.api.util.RegexUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 自动解码器
 */
@Slf4j
public class AutoDecoder {
    /**
     * 解码
     *
     * @param encode 密钥编码字符串(自动适配Hex或Base64编码，及先Hex再Base64编码的字符串)
     * @return 解码后的数据
     */
    public static byte[] decode(String encode) {
        if (RegexUtils.matchHex(encode)) {
            log.debug("编码为Hex格式");
            return Hex.decodeStrict(encode);
        } else if (RegexUtils.matchBase64(encode)) {
            byte[] key = Base64.getDecoder().decode(encode);
            if (RegexUtils.matchHex(new String(key, StandardCharsets.UTF_8))) {
                log.debug("编码为Hex_Base64格式");
                return Hex.decode(key);
            }
            log.debug("编码为Base64格式");
            return key;
        } else {
            return encode.getBytes();
        }
    }

}

package rebue.wheel.turing.jackson;

import java.security.PrivateKey;

import com.fasterxml.jackson.databind.util.StdConverter;

import rebue.wheel.turing.BcEcKeyUtils;

/**
 * EC算法私钥Jackson反序列化器
 *
 * @author zbz
 */
public class EcPrivateKeyJacksonDeserializer extends StdConverter<String, PrivateKey> {

    @Override
    public PrivateKey convert(final String value) {
        return BcEcKeyUtils.getPrivateKeyFromStr(value);
    }

}

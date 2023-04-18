package rebue.wheel.turing.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import rebue.wheel.turing.EcKeyUtils;

import java.security.PrivateKey;

/**
 * EC算法私钥Jackson反序列化器
 *
 * @author zbz
 */
public class EcPrivateKeyJacksonDeserializer extends StdConverter<String, PrivateKey> {

    @Override
    public PrivateKey convert(final String value) {
        return EcKeyUtils.getPrivateKeyFromString(value);
    }

}

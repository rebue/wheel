package rebue.wheel.turing.jackson;

import com.fasterxml.jackson.databind.util.StdConverter;
import rebue.wheel.turing.BcEcKeyUtils;

import java.security.PublicKey;

/**
 * EC算法公钥Jackson反序列化器
 *
 * @author zbz
 */
public class EcPublicKeyJacksonDeserializer extends StdConverter<String, PublicKey> {

    @Override
    public PublicKey convert(final String value) {
        return BcEcKeyUtils.getPublicKeyFromStr(value);
    }

}

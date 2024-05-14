package rebue.wheel.turing.jackson;

import java.security.PublicKey;

import com.fasterxml.jackson.databind.util.StdConverter;

import rebue.wheel.turing.BcEcKeyUtils;

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

package rebue.wheel.turing.json;

import java.security.PublicKey;

import com.fasterxml.jackson.databind.util.StdConverter;

import rebue.wheel.turing.Sm2Utils;

/**
 * Jackson的SM2算法公钥反序列化器
 *
 * @author zbz
 *
 */
public class JacksonSm2PublicKeyDeserializer extends StdConverter<String, PublicKey> {

    @Override
    public PublicKey convert(final String value) {
        return Sm2Utils.getPublicKeyFromString(value);
    }

}

package rebue.wheel.turing.json;

import java.security.PrivateKey;

import com.fasterxml.jackson.databind.util.StdConverter;

import rebue.wheel.turing.Sm2Utils;

/**
 * Jackson的SM2算法私钥反序列化器
 *
 * @author zbz
 *
 */
public class JacksonSm2PrivateKeyDeserializer extends StdConverter<String, PrivateKey> {

    @Override
    public PrivateKey convert(final String value) {
        return Sm2Utils.getPrivateKeyFromString(value);
    }

}

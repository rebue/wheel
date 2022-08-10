package rebue.wheel.serialization.jackson;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * LocalDate的扩展反序列化器，反序列化UTC格式(yyyy-MM-dd'T'HH:mm:ss)的字符串
 * 如果使用了默认的LocalDateDeserializer反序列化UTC格式的字符串，会报错 "Cannot deserialize value of type `java.time.LocalDate` from String "2020-11-03T20:07:55"
 * 所以使用此类来反序列化格式为UTC格式的字符串
 *
 * @author zbz
 *
 */
public class LocalDateUtcDeserializer extends JsonDeserializer<LocalDate> {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public LocalDate deserialize(final JsonParser jsoParser, final DeserializationContext ctx) throws IOException, JacksonException {
        final String value = jsoParser.getText();
        return LocalDate.parse(value, formatter);
    }

}

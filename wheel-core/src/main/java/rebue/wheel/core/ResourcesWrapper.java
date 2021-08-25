package rebue.wheel.core;

import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourcesWrapper {

    public static InputStream getInputStream(String fileName, Class<?> clazz) throws IOException
    {
        InputStream in;
        try {
            in = clazz.getClassLoader().getResourceAsStream(fileName);
            if (in == null) {
                throw new RuntimeException();
            }
            return in;
        } catch (Exception ignore) {
        }
        try {
            in = clazz.getResourceAsStream(fileName);
            if (in == null) {
                throw new RuntimeException();
            }
            return in;
        } catch (Exception ignore) {
            return new FileInputStream(ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + fileName));
        }
    }

    public static String fileStr(String fileName, Class<?> clazz) throws IOException
    {
        try (
                InputStream in = getInputStream(fileName, clazz);
                ByteArrayOutputStream o = new ByteArrayOutputStream()
        ) {
            int r;
            byte[] buffer = new byte[2048];
            while ((r = in.read(buffer)) != -1) {
                o.write(buffer, 0, r);
            }
            return new String(o.toByteArray(), StandardCharsets.UTF_8);
        }
    }

}

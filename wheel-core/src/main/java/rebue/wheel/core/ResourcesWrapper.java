package rebue.wheel.core;

import org.springframework.util.ResourceUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ResourcesWrapper {

    public static InputStream getInputStream(String fileName) throws Exception
    {
        try {
            return ResourcesWrapper.class.getClassLoader().getResourceAsStream(fileName);
        } catch (Exception e) {
            return new FileInputStream(ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX + fileName));
        }
    }

    public static String fileStr(String fileName) throws Exception
    {
        try (
                InputStream in = getInputStream(fileName);
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

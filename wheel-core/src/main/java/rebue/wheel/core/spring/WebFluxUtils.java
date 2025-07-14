package rebue.wheel.core.spring;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;

public class WebFluxUtils {
    public static Mono<InputStream> toInputStream(FilePart filePart) {
        return filePart.content()
                .reduce(new ByteArrayOutputStream(), (outputStream, dataBuffer) -> {
                    byte[] buffer = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(buffer);
                    DataBufferUtils.release(dataBuffer);
                    outputStream.write(buffer, 0, buffer.length);
                    return outputStream;
                })
                .map(ByteArrayOutputStream::toByteArray)
                .map(ByteArrayInputStream::new);
    }
}

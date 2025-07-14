package rebue.wheel.core.spring;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import cn.idev.excel.FastExcel;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class ExcelSpringUtils {
    /**
     * 下载excel格式的数据
     *
     * @param fileName 导出的文件名
     * @param voClazz  导出数据的视图类
     * @param dataList 导出的数据列表
     */
    public static ResponseEntity<Flux<DataBuffer>> download(String fileName, Class voClazz, List<?> dataList) {
        // URL编码文件名(防止中文乱码)
        String                     encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        // 设置响应头
        ResponseEntity.BodyBuilder ok              = ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .cacheControl(CacheControl.noCache());

        // 写入数据到Excel文件流中
        ByteArrayOutputStream      out             = new ByteArrayOutputStream();
        FastExcel.write(out, voClazz).sheet("Sheet1").doWrite(dataList);
        byte[] content = out.toByteArray();
        // 设置内容长度
        ok.contentLength(content.length);
        // 将数据写入响应流
        ByteArrayResource resource = new ByteArrayResource(content);
        return ok.body(DataBufferUtils.read(resource, new DefaultDataBufferFactory(), content.length).publishOn(Schedulers.boundedElastic()));
    }
}

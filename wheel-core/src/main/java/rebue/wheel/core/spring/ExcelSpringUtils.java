package rebue.wheel.core.spring;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Consumer;

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
import rebue.wheel.core.UriUtils;

public class ExcelSpringUtils {

    /**
     * 使用FastExcel以批处理方式读取Excel文件中的数据
     * 此方法适用于需要处理大量数据但只想批量处理特定数量的记录以减少内存消耗的场景
     *
     * @param fileInputStream Excel文件输入流，用于指定要读取的文件输入流
     * @param voClazz         数据模型类的Class对象，用于FastExcel反射生成对象
     * @param batchSize       每次批量处理的记录数，用于控制内存使用量
     * @param saveData        消费者接口，用于处理每一批读取的数据
     * @param errorDataList   错误数据列表，用于保存处理过程中遇到的错误数据(如果为null，一遇到错误就抛出异常)
     * @param <T>             数据模型类的泛型，表示可以处理任意类型的数据
     */
    public static <T> void readBatch(InputStream fileInputStream, Class voClazz, int batchSize, List<T> errorDataList, Consumer<T> saveData) throws IOException {
        FastExcel.read(fileInputStream, voClazz, new ExcelReadListener<>(batchSize, saveData, (vo, e) -> {
            if (errorDataList == null) {
                throw new IllegalArgumentException("批量导入失败: " + e.getMessage(), e);
            }
            errorDataList.add(vo);
        }))
                .sheet()            // 指定要读取的表格，默认第一个表格
                .doRead();          // 执行读取操作，开始批量读取Excel数据
        fileInputStream.close();
    }

    /**
     * 下载excel格式的数据
     *
     * @param fileName 导出的文件名
     * @param voClazz  导出数据的视图类
     * @param dataList 导出的数据列表
     */
    public static ResponseEntity<Flux<DataBuffer>> download(String fileName, Class voClazz, List<?> dataList) {
        // URL编码文件名(防止中文乱码)
        String                     encodedFileName = UriUtils.encode(fileName);
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

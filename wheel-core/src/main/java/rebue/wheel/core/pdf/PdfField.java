package rebue.wheel.core.pdf;

import com.itextpdf.kernel.font.PdfFont;
import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class PdfField {
    /**
     * 字段类型
     */
    @Builder.Default
    private PdfFieldType fieldType = PdfFieldType.TEXT;
    /**
     * 字段的值
     */
    @NonNull
    private Object       value;
    /**
     * 字体
     */
    private PdfFont      font;
    /**
     * 字体大小
     */
    private float        fontSize;
}


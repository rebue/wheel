package rebue.wheel.core.seal;

import lombok.*;

import java.awt.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class SealText {
    /**
     * 文本
     */
    @NonNull
    private String text;
    /**
     * 字体
     */
    @NonNull
    private Font   font;
    /**
     * 字之距
     */
    private Double space;
    /**
     * 上外边距
     */
    @Builder.Default
    private Double marginTop    = 0.0;
    /**
     * 左外边距
     */
    @Builder.Default
    private Double marginLeft   = 0.0;
    /**
     * 下外边距
     */
    @Builder.Default
    private Double marginBottom = 0.0;
    /**
     * X轴的放大缩小比例(大于1为放大，小于1缩小，等于1不变)
     */
    @Builder.Default
    private Double scaleX       = 1.0;
    /**
     * Y轴的放大缩小比例(大于1为放大，小于1缩小，等于1不变)
     */
    @Builder.Default
    private Double scaleY       = 1.0;
}

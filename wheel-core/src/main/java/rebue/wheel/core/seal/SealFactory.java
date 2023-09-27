package rebue.wheel.core.seal;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import java.awt.*;
import java.io.IOException;

/**
 * 印章工厂类
 */
public class SealFactory {
    public static ImageData create01(String topText, String captionText, String subcaptionText, String fontName) throws IOException {
        // 顶部文字字数
        int topTextLength = topText.length();
        // 顶部文字开始弧度
        double topBeginRadian;
        if (topTextLength <= 11) {
            topBeginRadian = (1.0 / 2 - 1.0 / 3) * 2 * Math.PI;
        } else if (topTextLength < 17) {
            topBeginRadian = Math.PI + 0.25;
        } else {
            topBeginRadian = (3.0 / 4 - 1.0 / 6) * 2 * Math.PI;
        }

        return ImageDataFactory.create(SealUtils.draw01(SealText.builder()
                        .text(topText)
                        .font(new Font(fontName, Font.BOLD, 100))
                        .marginTop(30.0)
                        .scaleY(2.0)
                        .build(),
                SealText.builder()
                        .text(captionText)
                        .font(new Font(fontName, Font.BOLD, 120))
                        .marginTop(120.0)
                        .build(),
                SealText.builder()
                        .text(subcaptionText)
                        .font(new Font(fontName, Font.BOLD, 120))
                        .marginTop(10.0)
                        .build(),
                1190, 28, 440, topBeginRadian, null));
    }
}

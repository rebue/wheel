package rebue.wheel.core;

import lombok.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SealUtils {

    /**
     * 绘制圆形(或椭圆形)公章
     *
     * @param title 公章标题(圆上部分弧形文字)
     * @param name  公章名称(五角星下方第一行文字)
     * @param date  公章时间(五角星下方第二行文字)
     * @return 图形的字节数组
     */
    public static byte[] draw01(String title, String name, String date) throws IOException {
        return draw01(
                SealText.builder()
                        .text(title)
                        .font(new Font("宋体", Font.PLAIN, 16))
                        .margin(10D)
                        .build(),
                SealText.builder()
                        .text(name)
                        .font(new Font("宋体", Font.PLAIN, 14))
                        .margin(15D)
                        .build(),
                new SealText(date, new Font("宋体", Font.PLAIN, 14)),
                150, 150, 8, 50);
    }

    /**
     * 绘制圆形(或椭圆形)公章
     *
     * @param title             公章标题(圆上部分弧形文字)
     * @param name              公章名称(五角星下方第一行文字)
     * @param date              公章时间(五角星下方第二行文字)
     * @param width             公章的宽度
     * @param height            公章的高度
     * @param circleBorderWidth 圆边框的宽度
     * @param starWidth         五角星宽度
     * @return 图形的字节数组
     */
    public static byte[] draw01(SealText title, SealText name, SealText date, int width, int height, int circleBorderWidth, int starWidth) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Create a Graphics2D object from the BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();
        try {
            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 设置公章的颜色
            g2d.setColor(Color.RED);

            // 中心点
            int centerX = width / 2;
            int centerY = height / 2;

            // 绘制圆
            int circleLeft   = circleBorderWidth / 2;               // 圆的左边距
            int circleTop    = circleBorderWidth / 2;               // 圆的上边距
            int circleWidth  = width - circleBorderWidth - 1;       // 圆的宽度
            int circleHeight = height - circleBorderWidth - 1;      // 圆的高度
            g2d.setStroke(new BasicStroke(circleBorderWidth));      // 设置圆边框的宽度
            g2d.draw(new Ellipse2D.Double(circleLeft, circleTop, circleWidth, circleHeight));

            // 绘制中间的五角星
            int    starRadius    = starWidth / 2;               // 五角星圆的半径
            int    starHornCount = 5;                           // 五角星角的数量
            double starAngle     = Math.PI / starHornCount;     // 五角星旋转角度
            int[]  xPoints       = new int[2 * starHornCount];
            int[]  yPoints       = new int[2 * starHornCount];
            for (int i = 0; i < 2 * starHornCount; i++) {
                double r = (i % 2 == 0) ? starRadius : starRadius * 0.4;
                xPoints[i] = (int) (centerX + r * Math.sin(i * starAngle));
                yPoints[i] = (int) (centerY - r * Math.cos(i * starAngle));
            }
            g2d.fill(new Polygon(xPoints, yPoints, 2 * starHornCount));

            // 绘制名称
            g2d.setFont(name.font);
            FontMetrics nameFontMetrics = g2d.getFontMetrics();
            int         nameY           = (int) (centerY + starRadius + name.margin);
            g2d.drawString(name.text, centerX - nameFontMetrics.stringWidth(name.text) / 2, nameY);

            // 绘制时间
            g2d.setFont(date.font);
            FontMetrics dateFontMetrics = g2d.getFontMetrics();
            g2d.drawString(date.text, centerX - dateFontMetrics.stringWidth(date.text) / 2, nameY + nameFontMetrics.getHeight());

            // 绘制标题
            drawArcTextForCircle(title, 0, 0, width / 2, true, g2d);
        } finally {
            // Dispose the Graphics2D object
            g2d.dispose();
        }

        // 将BufferedImage转换为字节数组
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 绘制圆弧形文字
     *
     * @param sealText     文本
     * @param left         弧形的左边距
     * @param top          弧形的上边距
     * @param circleRadius 圆半径
     * @param isTop        是在上部分还是下部分绘制文本
     * @param g2d          Graphic2D
     */
    private static void drawArcTextForCircle(SealText sealText, double left, double top,
                                             int circleRadius, boolean isTop, Graphics2D g2d) {
        if (sealText == null) {
            return;
        }

        //1.字体长度
        int textLen = sealText.getText().length();

        FontRenderContext context   = g2d.getFontRenderContext();
        Rectangle2D       rectangle = sealText.font.getStringBounds(sealText.getText(), context);

        //5.文字之间间距，默认动态调整
        Double space = sealText.getSpace();
        if (space == null) {
            space = (textLen == 1) ? 0D : rectangle.getWidth() / (textLen - 1) * 0.9;
        }

        // 6.距离外圈距离
        double margin = sealText.getMargin() == null ? left : sealText.getMargin();

        // 7.写字
        double newRadius         = circleRadius + rectangle.getY() - margin;
        double radianPerInterval = 2 * Math.asin(space / (2 * newRadius));

        double fix = 0.04;
        if (isTop) {
            fix = 0.18;
        }
        double firstAngle;
        if (!isTop) {
            if (textLen % 2 == 1) {
                firstAngle = Math.PI + Math.PI / 2 - (textLen - 1) * radianPerInterval / 2.0 - fix;
            } else {
                firstAngle = Math.PI + Math.PI / 2 - ((textLen / 2.0 - 0.5) * radianPerInterval) - fix;
            }
        } else {
            if (textLen % 2 == 1) {
                firstAngle = (textLen - 1) * radianPerInterval / 2.0 + Math.PI / 2 + fix;
            } else {
                firstAngle = (textLen / 2.0 - 0.5) * radianPerInterval + Math.PI / 2 + fix;
            }
        }

        for (int i = 0; i < textLen; i++) {
            double theta;
            double thetaX;
            double thetaY;

            if (!isTop) {
                theta = firstAngle + i * radianPerInterval;
                thetaX = newRadius * Math.sin(Math.PI / 2 - theta);
                thetaY = newRadius * Math.cos(theta - Math.PI / 2);
            } else {
                theta = firstAngle - i * radianPerInterval;
                thetaX = newRadius * Math.sin(Math.PI / 2 - theta);
                thetaY = newRadius * Math.cos(theta - Math.PI / 2);
            }

            AffineTransform transform;
            if (!isTop) {
                transform = AffineTransform.getRotateInstance(Math.PI + Math.PI / 2 - theta);
            } else {
                transform = AffineTransform.getRotateInstance(Math.PI / 2 - theta + Math.toRadians(8));
            }
            Font f2 = sealText.font.deriveFont(transform);
            g2d.setFont(f2);
            g2d.drawString(sealText.getText().substring(i, i + 1), (float) (circleRadius + thetaX + left), (float) (circleRadius - thetaY + top));
        }
    }

    @Data
    @NoArgsConstructor
    @RequiredArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SealText {
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
         * 外边距
         */
        private Double margin;
    }
}

package rebue.wheel.core.seal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 生成公章图像工具类
 * 参考了 <a href="https://github.com/niezhiliang/signature-utils">signature-utils</a> 项目
 */
public class SealUtils {
    /**
     * 绘制圆形公章(TODO 转成工厂模式)
     *
     * @param topText        公章上部分弧形文字
     * @param captionText    公章标题名称(五角星下方第一行文字)
     * @param subcaptionText 公章副标题名称(五角星下方第二行文字)
     * @return 公章图形的字节数组
     */
    public static byte[] draw01(String topText, String captionText, String subcaptionText) throws IOException {
        return draw01(
                SealText.builder()
                        .text(topText)
                        .font(new Font("宋体", Font.PLAIN, 16))
                        .marginTop(10D)
                        .build(),
                SealText.builder()
                        .text(captionText)
                        .font(new Font("宋体", Font.PLAIN, 14))
                        .marginTop(15D)
                        .build(),
                new SealText(subcaptionText, new Font("宋体", Font.PLAIN, 14)),
                150, 8, 50);
    }

    /**
     * 绘制圆形公章
     *
     * @param topText           公章上部分弧形文字
     * @param captionText       公章标题名称(五角星下方第一行文字)
     * @param subcaptionText    公章副标题名称(五角星下方第二行文字)
     * @param width             公章的宽度
     * @param circleBorderWidth 圆边框的宽度
     * @param starWidth         五角星宽度
     * @return 公章图形的字节数组
     */
    public static byte[] draw01(SealText topText, SealText captionText, SealText subcaptionText, int width, int circleBorderWidth, int starWidth) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        // Create a Graphics2D object from the BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();
        try {
            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 设置公章的颜色
            g2d.setColor(Color.RED);

            // 中心点
            int centerX = width / 2;
            int centerY = centerX;

            // 绘制圆
            int circleLeft  = circleBorderWidth / 2;                // 圆的左边坐标
            int circleTop   = circleLeft;                           // 圆的上边距
            int circleWidth = width - circleBorderWidth - 1;        // 圆的宽度
            g2d.setStroke(new BasicStroke(circleBorderWidth));      // 设置圆边框的宽度
            g2d.draw(new Ellipse2D.Double(circleLeft, circleTop, circleWidth, circleWidth));

            // 绘制公章上部分弧形文字
            drawArcTextForCircle(topText, 0, 0, width / 2, true, g2d);

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

            // 绘制公章标题名称
            g2d.setFont(captionText.getFont());
            FontMetrics captionFontMetrics = g2d.getFontMetrics();
            int         nameY              = (int) (centerY + starRadius + captionText.getMarginTop());
            g2d.drawString(captionText.getText(), centerX - captionFontMetrics.stringWidth(captionText.getText()) / 2, nameY);

            // 绘制公章副标题名称
            g2d.setFont(subcaptionText.getFont());
            FontMetrics subcaptionFontMetrics = g2d.getFontMetrics();
            g2d.drawString(subcaptionText.getText(), centerX - subcaptionFontMetrics.stringWidth(subcaptionText.getText()) / 2, nameY + captionFontMetrics.getHeight());
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
     * 绘制椭圆形公章
     *
     * @param topText                 公章上部分弧形文字
     * @param centerText              公章中间文字
     * @param bottomText              公章下部分弧形文字
     * @param width                   公章的宽度
     * @param height                  公章的高度
     * @param outerEllipseBorderWidth 外圈椭圆边框的宽度
     * @param innerEllipseBorderWidth 内圈椭圆边框的宽度
     * @return 公章图形的字节数组
     */
    public static byte[] draw02(SealText topText, SealText centerText, SealText bottomText, int width, int height,
                                double outerEllipseBorderWidth, double innerEllipseBorderWidth) throws IOException {
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

            // 绘制外圈椭圆
            double outerEllipseLeft   = outerEllipseBorderWidth / 2 + 1;        // 外圈椭圆的左边坐标
            double outerEllipseTop    = outerEllipseBorderWidth / 2 + 1;        // 外圈椭圆的上边坐标
            double outerEllipseWidth  = width - outerEllipseLeft * 2;           // 外圈椭圆的宽度
            double outerEllipseHeight = height - outerEllipseTop * 2;           // 外圈椭圆的高度
            g2d.setStroke(new BasicStroke((float) outerEllipseBorderWidth));    // 设置外圈椭圆边框的宽度
            Ellipse2D outerEllipse = new Ellipse2D.Double(outerEllipseLeft, outerEllipseTop, outerEllipseWidth, outerEllipseHeight);
            g2d.draw(outerEllipse);

            // 绘制内圈椭圆
            double innerEllipseLeft   = outerEllipseLeft + outerEllipseBorderWidth + 1;     // 内圈椭圆的左边坐标
            double innerEllipseTop    = outerEllipseTop + outerEllipseBorderWidth + 1;      // 内圈椭圆的上边坐标
            double innerEllipseWidth  = width - innerEllipseLeft * 2;                       // 内圈椭圆的宽度
            double innerEllipseHeight = height - innerEllipseTop * 2;                       // 内圈椭圆的高度
            g2d.setStroke(new BasicStroke((float) innerEllipseBorderWidth));                // 设置内圈椭圆边框的宽度
            Ellipse2D innerEllipse = new Ellipse2D.Double(innerEllipseLeft, innerEllipseTop, innerEllipseWidth, innerEllipseHeight);
            g2d.draw(innerEllipse);

            // 绘制公章上部分弧形文字
            double topTextLeft   = innerEllipseLeft + innerEllipseBorderWidth + topText.getMarginTop(); // 文字椭圆曲线的左边坐标
            double topTextTop    = innerEllipseTop + innerEllipseBorderWidth + topText.getMarginTop();  // 文字椭圆曲线的上边坐标
            double topTextWidth  = width - topTextLeft * 2;                                             // 文字椭圆曲线的宽度
            double topTextHeight = height - topTextTop * 2;                                             // 文字椭圆曲线的高度
            drawArcTextForEllipse(topText, topTextLeft, topTextTop, topTextWidth, topTextHeight, 1, true, g2d);

            // 绘制公章标题名称
            g2d.setFont(centerText.getFont());
            FontMetrics centerFontMetrics = g2d.getFontMetrics();
            g2d.drawString(centerText.getText(),
                    centerX - centerFontMetrics.stringWidth(centerText.getText()) / 2,
                    centerY + centerFontMetrics.getHeight() / 2 - centerFontMetrics.getDescent());

            // 绘制公章下部分弧形文字
            double bottomTextLeft   = innerEllipseLeft + innerEllipseBorderWidth + topText.getMarginTop();  // 文字椭圆曲线的左边坐标
            double bottomTextTop    = innerEllipseTop + innerEllipseBorderWidth + topText.getMarginTop();   // 文字椭圆曲线的上边坐标
            double bottomTextWidth  = width - bottomTextLeft * 2;                                           // 文字椭圆曲线的宽度
            double bottomTextHeight = height - bottomTextTop * 2;                                           // 文字椭圆曲线的高度
            drawArcTextForEllipse(bottomText, bottomTextLeft, bottomTextTop, bottomTextWidth, bottomTextHeight, 0, false, g2d);
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
     * 绘制横向矩形公章
     *
     * @param sealText   文本
     * @param paddingX   X轴内边框
     * @param paddingY   Y轴内边框
     * @param borderSize 边框宽度
     * @return 公章图形的字节数组
     */
    public static byte[] draw03(SealText sealText, int paddingX, int paddingY, int borderSize) throws IOException {
        // Create a temporary Graphics object to get the FontMetrics
        Graphics    graphics    = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
        FontMetrics fontMetrics = graphics.getFontMetrics(sealText.getFont());
        int         fontWidth   = fontMetrics.stringWidth(sealText.getText());
        int         fontHeight  = fontMetrics.getHeight(); // Get the height of the font
        int         width       = paddingX * 2 + borderSize * 2 + fontWidth;
        int         height      = paddingY * 2 + borderSize * 2 + fontHeight;

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

            // 绘制矩形边框
            g2d.setStroke(new BasicStroke(borderSize));
            g2d.drawRect(borderSize / 2, borderSize / 2, width - borderSize, height - borderSize);

            // 绘制公章文本
            g2d.setFont(sealText.getFont());
            g2d.drawString(sealText.getText(),
                    centerX - fontWidth / 2,
                    centerY + fontHeight / 2 - fontMetrics.getDescent());
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
     * @param left         弧形的左边坐标
     * @param top          弧形的上边坐标
     * @param circleRadius 圆半径
     * @param isTop        是在上部分还是下部分绘制文本
     * @param g2d          Graphic2D
     */
    @SuppressWarnings("all")
    private static void drawArcTextForCircle(SealText sealText, double left, double top,
                                             int circleRadius, boolean isTop, Graphics2D g2d) {
        if (sealText == null) {
            return;
        }

        //1.字体长度
        int textLen = sealText.getText().length();

        FontRenderContext context   = g2d.getFontRenderContext();
        Rectangle2D       rectangle = sealText.getFont().getStringBounds(sealText.getText(), context);

        //5.文字之间间距，默认动态调整
        Double space = sealText.getSpace();
        if (space == null) {
            space = (textLen == 1) ? 0D : rectangle.getWidth() / (textLen - 1) * 0.9;
        }

        // 7.写字
        double newRadius         = circleRadius + rectangle.getY() - sealText.getMarginTop();
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
            } else {
                theta = firstAngle - i * radianPerInterval;
            }
            thetaX = newRadius * Math.sin(Math.PI / 2 - theta);
            thetaY = newRadius * Math.cos(theta - Math.PI / 2);

            AffineTransform transform;
            if (!isTop) {
                transform = AffineTransform.getRotateInstance(Math.PI + Math.PI / 2 - theta);
            } else {
                transform = AffineTransform.getRotateInstance(Math.PI / 2 - theta + Math.toRadians(8));
            }
            Font f2 = sealText.getFont().deriveFont(transform);
            g2d.setFont(f2);
            g2d.drawString(sealText.getText().substring(i, i + 1), (float) (circleRadius + thetaX + left), (float) (circleRadius - thetaY + top));
        }
    }

    /**
     * 绘制椭圆弧形文字
     * FIXME 圆弧形文字分布均匀
     *
     * @param sealText 文本
     * @param left     左边坐标
     * @param top      上边坐标
     * @param width    宽度
     * @param height   高度
     * @param lineSize 行高
     * @param isTop    是否顶部圆弧(不是则为底部圆弧)
     * @param g2d      Graphics2D
     */
    @SuppressWarnings("all")
    private static void drawArcTextForEllipse(SealText sealText,
                                              double left, double top, double width, double height, int lineSize,
                                              boolean isTop, Graphics2D g2d) {
        float radiusX      = (float) width / 2;
        float radiusY      = (float) height / 2;
        float radiusWidth  = radiusX + (float) lineSize;
        float radiusHeight = radiusY + (float) lineSize;
        int   fontTextLen  = sealText.getText().length();

        g2d.setFont(sealText.getFont());
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int         charWidth   = fontMetrics.stringWidth(sealText.getText()) / sealText.getText().length();
        int         charHeight  = fontMetrics.getHeight();
        if (isTop) {
            left += sealText.getMarginLeft();
            top += sealText.getMarginTop();
        } else {
            left += sealText.getMarginLeft();
            top += charHeight - sealText.getMarginBottom();
        }

        float totalArcAng = 180.0F;
        if (!isTop) {
            totalArcAng = 120.0F;
        }

        float    minRat     = 0.9F;
        double   startAngle = isTop ? (double) (-90.0F - totalArcAng / 2.0F) : (double) (90.0F - totalArcAng / 2.0F);
        double   step       = 0.5;
        int      alCount    = (int) Math.ceil((double) totalArcAng / step) + 1;
        double[] angleArr   = new double[alCount];
        double[] arcLenArr  = new double[alCount];
        int      num        = 0;
        double   accArcLen  = 0.0;
        angleArr[num] = startAngle;
        arcLenArr[num] = accArcLen;
        ++num;
        double angR  = startAngle * Math.PI / 180.0;
        double lastX = (double) radiusX * Math.cos(angR) + (double) radiusWidth;
        double lastY = (double) radiusY * Math.sin(angR) + (double) radiusHeight;

        double arcPer;
        for (arcPer = startAngle + step; num < alCount; arcPer += step) {
            angR = arcPer * Math.PI / 180.0;
            double x = (double) radiusX * Math.cos(angR) + (double) radiusWidth;
            double y = (double) radiusY * Math.sin(angR) + (double) radiusHeight;
            accArcLen += Math.sqrt((lastX - x) * (lastX - x) + (lastY - y) * (lastY - y));
            angleArr[num] = arcPer;
            arcLenArr[num] = accArcLen;
            lastX = x;
            lastY = y;
            ++num;
        }

        arcPer = accArcLen / (double) fontTextLen;

        for (int i = 0; i < fontTextLen; ++i) {
            double arcL = (double) i * arcPer + arcPer / 2.0;
            double ang  = 0.0;

            for (int p = 0; p < arcLenArr.length - 1; ++p) {
                if (arcLenArr[p] <= arcL && arcL <= arcLenArr[p + 1]) {
                    ang = arcL >= (arcLenArr[p] + arcLenArr[p + 1]) / 2.0 ? angleArr[p + 1] : angleArr[p];
                    break;
                }
            }

            angR = ang * Math.PI / 180.0;
            float  x        = radiusX * (float) Math.cos(angR) + radiusWidth;
            float  y        = radiusY * (float) Math.sin(angR) + radiusHeight;
            double qxang    = Math.atan2((double) radiusY * Math.cos(angR), (double) (-radiusX) * Math.sin(angR));
            double fxang    = qxang + 1.5707963267948966;
            int    subIndex = isTop ? i : fontTextLen - 1 - i;
            String c        = sealText.getText().substring(subIndex, subIndex + 1);
            if (isTop) {
                x = x + (float) charHeight * minRat * (float) Math.cos(fxang);
                y = y + (float) charHeight * minRat * (float) Math.sin(fxang);
                x = x + (float) (-charWidth) / 2.0F * (float) Math.cos(qxang);
                y = y + (float) (-charWidth) / 2.0F * (float) Math.sin(qxang);
            } else {
                x = x + (float) charHeight * minRat * (float) Math.cos(fxang);
                y = y + (float) charHeight * minRat * (float) Math.sin(fxang);
                x = x + (float) charWidth / 2.0F * (float) Math.cos(qxang);
                y = y + (float) charWidth / 2.0F * (float) Math.sin(qxang);
            }

            AffineTransform affineTransform = new AffineTransform();
            affineTransform.scale(0.8, 1.0);
            if (isTop) {
                affineTransform.rotate(Math.toRadians(fxang * 180.0 / Math.PI - 90.0), 0.0, 0.0);
            } else {
                affineTransform.rotate(Math.toRadians(fxang * 180.0 / Math.PI + 180.0 - 90.0), 0.0, 0.0);
            }

            Font f2 = sealText.getFont().deriveFont(affineTransform);
            g2d.setFont(f2);
            g2d.drawString(c, (float) (x + left), (float) (y + top));
        }
    }

}

package rebue.wheel.core.seal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
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
                        .font(new Font("STSong", Font.PLAIN, 16))
                        .marginTop(10D)
                        .build(),
                SealText.builder()
                        .text(captionText)
                        .font(new Font("STSong", Font.PLAIN, 14))
                        .marginTop(15D)
                        .build(),
                new SealText(subcaptionText, new Font("STSong", Font.PLAIN, 14)),
                150, 8, 50, false);
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
     * @param subcaptionIsArc   公章副标题是否圆弧
     * @return 公章图形的字节数组
     */
    public static byte[] draw01(SealText topText, SealText captionText, SealText subcaptionText, int width, double circleBorderWidth, float starWidth, boolean subcaptionIsArc) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        // Create a Graphics2D object from the BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();
        try {
            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 设置公章的颜色
            g2d.setColor(Color.RED);

            // 中心点
            double centerX = width / 2.0;
            double centerY = centerX;

            // 绘制圆
            double circleLeft  = circleBorderWidth / 2;                     // 圆的左边坐标
            double circleTop   = circleLeft;                                // 圆的上边距
            double circleWidth = width - circleBorderWidth - 1;             // 圆的宽度
            g2d.setStroke(new BasicStroke((float) circleBorderWidth));      // 设置圆边框的宽度
            g2d.draw(new Ellipse2D.Double(circleLeft, circleTop, circleWidth, circleWidth));

            // 绘制公章上部分弧形文字
            drawArcTextForCircle1(topText, centerX, centerY, width / 2.0 - circleBorderWidth,
                    (3.0 / 4 - 1.0 / 6) * 2 * Math.PI, true, g2d);

            // 绘制中间的五角星
            float  starRadius    = starWidth / 2;               // 五角星圆的半径
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
            double         captionTextTop        = (centerY - starRadius * Math.cos((3.0 / 2 - 1.0 / 5) * Math.PI));
            TextDimensions captionTextDimensions = drawCenterText(captionText, centerX, captionTextTop, g2d);

            // 绘制公章副标题名称
            if (subcaptionIsArc) {
                drawArcTextForCircle1(subcaptionText, centerX, centerY, width / 2.0 - circleBorderWidth,
                        (3.0 / 4 - 1.0 / 6) * 2 * Math.PI, false, g2d);
            } else {
                drawCenterText(subcaptionText, centerX,
                        captionTextTop + captionText.getMarginTop() + captionTextDimensions.getHeight(), g2d);
            }
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
            TextDimensions centerTextDimensions = getTextDimensions(centerText.getText(), centerText.getFont(), g2d);
            g2d.drawString(centerText.getText(),
                    centerX - centerTextDimensions.getWidth() / 2,
                    centerY + centerTextDimensions.getHeight() / 2 - centerTextDimensions.getDescent());

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
     * 绘制横向矩形方框公章
     *
     * @param sealText     文本
     * @param paddingX     X轴内边距
     * @param paddingY     Y轴内边距
     * @param borderSize   边框宽度
     * @param cornerRadius 圆角角度
     * @return 公章图形的字节数组
     */
    public static byte[] draw03(SealText sealText, int paddingX, int paddingY, int borderSize, int cornerRadius) throws IOException {
        // Create a temporary Graphics object to get the FontMetrics
        Graphics       graphics       = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
        TextDimensions textDimensions = getTextDimensions(sealText.getText(), sealText.getFont(), graphics);
        int            width          = paddingX * 2 + borderSize * 2 + textDimensions.getWidth();
        int            height         = paddingY * 2 + borderSize * 2 + textDimensions.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Create a Graphics2D object from the BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();
        try {
            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 设置公章的颜色
            g2d.setColor(Color.RED);

            // 绘制矩形边框
            drawRectBorder(width, height, borderSize, cornerRadius, g2d);

            // 计算中心点
            int centerX = width / 2;
            int centerY = height / 2;

            // 绘制公章文本
            g2d.setFont(sealText.getFont());
            g2d.drawString(sealText.getText(),
                    centerX - textDimensions.getWidth() / 2,
                    centerY + textDimensions.getHeight() / 2 - textDimensions.getLeading() / 2 - textDimensions.getDescent());
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
     * 绘制正方形印章
     *
     * @param sealText     文本(字数只能在2(含)~4(含)之间)
     * @param paddingX     X轴内边距
     * @param paddingY     Y轴内边距
     * @param borderSize   边框宽度
     * @param cornerRadius 圆角宽度
     * @return 印章图形的字节数组
     */
    public static byte[] draw04(SealText sealText, int paddingX, int paddingY, int borderSize, int cornerRadius) throws IOException {
        String text = sealText.getText();
        switch (text.length()) {
            case 2:
                text += "之印";
                break;
            case 3:
                text += "印";
                break;
            case 4:
                break;
            default:
                throw new IllegalArgumentException("文本的字数只能在2(含)~4(含)之间");
        }
        String char0 = text.substring(2, 3);
        String char1 = text.substring(0, 1);
        String char2 = text.substring(3, 4);
        String char3 = text.substring(1, 2);

        // Create a temporary Graphics object to get the FontMetrics
        Graphics       graphics       = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).getGraphics();
        TextDimensions textDimensions = getTextDimensions(char0, sealText.getFont(), graphics);
        int            width          = paddingX * 2 + borderSize * 2 + textDimensions.getWidth() * 2 + (int) Math.ceil(sealText.getSpace());
        int            height         = paddingY * 2 + borderSize * 2 + textDimensions.getHeight() * 2 + (int) Math.ceil(sealText.getSpace());

        if (width < height) width = height;
        else if (height < width) height = width;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // Create a Graphics2D object from the BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();
        try {
            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 设置公章的颜色
            g2d.setColor(Color.RED);

            // 绘制矩形边框
            drawRectBorder(width, height, borderSize, cornerRadius, g2d);

            // 计算中心点
            int centerX = width / 2;
            int centerY = height / 2;

            // 绘制公章文本
            g2d.setFont(sealText.getFont());
            int halfSpace = (int) (sealText.getSpace() / 2);
            int x1        = centerX - halfSpace - textDimensions.getWidth();
            int x2        = centerX + halfSpace;
            int y1        = centerY - halfSpace - textDimensions.getDescent();
            int y2        = centerY + halfSpace + textDimensions.getAscent();
            g2d.drawString(char0, x1, y1);
            g2d.drawString(char1, x2, y1);
            g2d.drawString(char2, x1, y2);
            g2d.drawString(char3, x2, y2);
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
     * 绘制文本并水平居中
     *
     * @param sealText 文本
     * @param centerX  中心点X坐标
     * @param top      顶部Y轴坐标
     * @param g2d      Graphics2D
     * @return 文本各项指标
     */
    private static TextDimensions drawCenterText(SealText sealText, double centerX, double top, Graphics2D g2d) {
        // 字体变形
        AffineTransform transform      = AffineTransform.getScaleInstance(sealText.getScaleX(), sealText.getScaleY());
        Font            scaleFont      = sealText.getFont().deriveFont(transform);
        String          text           = sealText.getText();
        TextDimensions  textDimensions = getTextDimensions(text, scaleFont, g2d);
        // 文本的宽度
        double textWidth = textDimensions.getWidth();
        // 一个字的宽度
        double charWidth = textWidth / text.length();
        // 字间距
        double space = sealText.getSpace() == null ? 0 : sealText.getSpace();
        // 总字间距
        double totalSpace = space * (text.length() - 1);
        // 行宽度
        double lineWidth = textWidth + totalSpace;
        // 上边距
        double marginTop = sealText.getMarginTop() == null ? 0.0 : sealText.getMarginTop();
        // 设置变形字体
        g2d.setFont(scaleFont);

        // 一个一个字的绘制
        float x = (float) ((centerX - lineWidth / 2));
        float y = (float) (top + marginTop + textDimensions.getLeading() / 2 + textDimensions.getAscent());
        for (int i = 0; i < text.length(); i++) {
            g2d.drawString(String.valueOf(text.charAt(i)), x, y);
            x += charWidth + space;
        }

        return textDimensions;
    }

    /**
     * 获取文本的宽高及高的各项指标
     *
     * @param text     文本
     * @param font     字体
     * @param graphics graphics
     * @return 文本各项指标
     */
    private static TextDimensions getTextDimensions(String text, Font font, Graphics graphics) {
        FontMetrics fontMetrics = graphics.getFontMetrics(font);
        return TextDimensions.builder()
                .width(fontMetrics.stringWidth(text))
                .height(fontMetrics.getHeight())
                .ascent(fontMetrics.getAscent())
                .descent(fontMetrics.getDescent())
                .leading(fontMetrics.getLeading())
                .build();
    }

    /**
     * 绘制矩形边框
     *
     * @param width        矩形宽度
     * @param height       矩形高度
     * @param borderSize   矩形边框宽度
     * @param cornerRadius 矩形圆角角度
     * @param g2d          Graphics2D
     */
    private static void drawRectBorder(int width, int height, int borderSize, int cornerRadius, Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(borderSize));
        int rectX      = borderSize / 2;
        int rectY      = borderSize / 2;
        int rectWidth  = width - borderSize;
        int rectHeight = height - borderSize;
        if (cornerRadius == 0) {
            g2d.drawRect(rectX, rectY, rectWidth, rectHeight);
        } else {
            // Create a rounded rectangle shape
            RoundRectangle2D roundedRect = new RoundRectangle2D.Float(rectX, rectY, rectWidth, rectHeight, cornerRadius, cornerRadius);
            g2d.draw(roundedRect);
        }
    }

    private static void drawArcTextForCircle1(SealText sealText, double centerX, double centerY,
                                              double circleRadius, double beginRadian, boolean isTop, Graphics2D g2d) {
        // 字体变形
        AffineTransform scaleAffineTransform = AffineTransform.getScaleInstance(sealText.getScaleX(), sealText.getScaleY());
        Font            scaleFont            = sealText.getFont().deriveFont(scaleAffineTransform);
        g2d.setFont(scaleFont);

        String         text           = sealText.getText();
        TextDimensions textDimensions = getTextDimensions(text, scaleFont, g2d);
        // 文本的宽度
        double textWidth = textDimensions.getWidth();
        // 字体的宽度
        double fontWidth = textWidth / text.length();
        // 上边距
        double marginTop = sealText.getMarginTop() == null ? 0.0 : sealText.getMarginTop();

        // 将坐标原点移动到圆弧的中心
        g2d.translate(centerX, centerY);

        // 计算文本圆弧的半径
        double textRadius = circleRadius - marginTop;
        if (isTop) {
            textRadius = textRadius - textDimensions.getHeight()
                    + textDimensions.getLeading() + textDimensions.getDescent();
        } else {
            textRadius = textRadius
                    - textDimensions.getLeading() - textDimensions.getDescent();
        }
        // 计算每个字符的弧度
        double charRadian;
        if (isTop) {
            charRadian = (beginRadian - Math.PI / 2) * 2 / text.length();
        } else {
            charRadian = (3.0 / 2 * Math.PI - beginRadian) * 2 / text.length();
        }
//        // 字体的弧度
//        double fontRadian = 2 * Math.asin(fontWidth / 2 / textRadius);
//        // 字间距的弧度
//        double spaceRadian = charRadian - fontRadian;
        // 计算第一个字旋转的弧度
        double firstRotateRadian;
        if (isTop) {
            firstRotateRadian = -(beginRadian - charRadian / 2 - Math.PI / 2);
        } else {
            firstRotateRadian = 3.0 / 2 * Math.PI - beginRadian - charRadian / 2;
        }
        g2d.rotate(firstRotateRadian);
        // 在圆周上绘制文本
        for (int i = 0; i < text.length(); i++) {
            // 计算位置
            float x = -(float) fontWidth / 2 - 1;
            float y = (isTop ? -1 : 1) * (float) textRadius;
            // 绘制当前字符
            g2d.drawString(String.valueOf(text.charAt(i)), x, y);
            // 继续旋转
            g2d.rotate((isTop ? 1 : -1) * charRadian);
        }

        // 重置旋转和中心
        g2d.setTransform(new AffineTransform());
    }

    @SuppressWarnings("all")
    private static void drawArcTextForCircle2(SealText sealText, double centerX, double centerY, double circleRadius, Graphics2D g2d) {
        String message = sealText.getText();
        //根据输入字符串得到字符数组
        String[] messages2 = message.split("", 0);
        String[] messages  = new String[messages2.length];
        System.arraycopy(messages2, 0, messages, 0, messages2.length);

        //输入的字数
        int ilength = messages.length;

        //设置字体属性
        int fontsize = sealText.getFont().getSize();
        //字体大小适配
        int len = 0;
        if (message.length() <= 11) {
            fontsize = 48;
            len = 70;
        } else if (message.length() < 15) {
            fontsize = 42;
            len = 90;
        } else if (message.length() < 17) {
            fontsize = 39;
            len = 130;
        } else if (message.length() < 20) {
            fontsize = 35;
            len = 148;
        } else {
            fontsize = 31;
            len = 160;
        }
        Font f = new Font("STSong", Font.PLAIN, fontsize);

        FontRenderContext context = g2d.getFontRenderContext();
        Rectangle2D       bounds  = f.getStringBounds(message, context);

        //字符宽度＝字符串长度/字符数
        double char_interval = ((bounds.getWidth() - len) / ilength);
        //上坡度
        double ascent = -bounds.getY() + 15;

        int     first = 0, second = 0;
        boolean odd   = false;
        if (ilength % 2 == 1) {
            first = (ilength - 1) / 2;
            odd = true;
        } else {
            first = (ilength) / 2 - 1;
            second = (ilength) / 2;
            odd = false;
        }

        double radius2 = circleRadius - ascent;
        double x0      = centerX;
        double y0      = centerY - circleRadius + ascent;
        //旋转角度
        double a = 2 * Math.asin(char_interval / (2 * radius2));

        if (odd) {
            g2d.setFont(f);
            g2d.drawString(messages[first], (float) (x0 - char_interval / 2), (float) y0);

            //中心点的右边
            for (int i = first + 1; i < ilength; i++) {
                double          aa        = (i - first) * a;
                double          ax        = radius2 * Math.sin(aa);
                double          ay        = radius2 - radius2 * Math.cos(aa);
                AffineTransform transform = AffineTransform.getRotateInstance(aa);//,x0 + ax, y0 + ay);
                Font            f2        = f.deriveFont(transform);
                g2d.setFont(f2);
                g2d.drawString(messages[i], (float) (x0 + ax - char_interval / 2 * Math.cos(aa)), (float) (y0 + ay - char_interval / 2 * Math.sin(aa)));
            }
            //中心点的左边
            for (int i = first - 1; i > -1; i--) {
                double          aa        = (first - i) * a;
                double          ax        = radius2 * Math.sin(aa);
                double          ay        = radius2 - radius2 * Math.cos(aa);
                AffineTransform transform = AffineTransform.getRotateInstance(-aa);//,x0 + ax, y0 + ay);
                Font            f2        = f.deriveFont(transform);
                g2d.setFont(f2);
                g2d.drawString(messages[i], (float) (x0 - ax - char_interval / 2 * Math.cos(aa)), (float) (y0 + ay + char_interval / 2 * Math.sin(aa)));
            }

        } else {
            //中心点的右边
            for (int i = second; i < ilength; i++) {
                double          aa        = (i - second + 0.5) * a;
                double          ax        = radius2 * Math.sin(aa);
                double          ay        = radius2 - radius2 * Math.cos(aa);
                AffineTransform transform = AffineTransform.getRotateInstance(aa);//,x0 + ax, y0 + ay);
                Font            f2        = f.deriveFont(transform);
                g2d.setFont(f2);
                g2d.drawString(messages[i], (float) (x0 + ax - char_interval / 2 * Math.cos(aa)), (float) (y0 + ay - char_interval / 2 * Math.sin(aa)));
            }

            //中心点的左边
            for (int i = first; i > -1; i--) {
                double          aa        = (first - i + 0.5) * a;
                double          ax        = radius2 * Math.sin(aa);
                double          ay        = radius2 - radius2 * Math.cos(aa);
                AffineTransform transform = AffineTransform.getRotateInstance(-aa);//,x0 + ax, y0 + ay);
                Font            f2        = f.deriveFont(transform);
                g2d.setFont(f2);
                g2d.drawString(messages[i], (float) (x0 - ax - char_interval / 2 * Math.cos(aa)), (float) (y0 + ay + char_interval / 2 * Math.sin(aa)));
            }
        }
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
    private static void drawArcTextForCircle3(SealText sealText, double left, double top,
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
        TextDimensions textDimensions = getTextDimensions(sealText.getText(), sealText.getFont(), g2d);
        int            charWidth      = textDimensions.getWidth() / sealText.getText().length();
        int            charHeight     = textDimensions.getHeight();
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

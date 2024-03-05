package rebue.wheel.core;

import rebue.wheel.core.seal.TextDimensions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    /**
     * 获取文本的宽高及高的各项指标
     *
     * @param text     文本
     * @param font     字体
     * @param graphics graphics
     * @return 文本各项指标
     */
    public static TextDimensions getTextDimensions(String text, Font font, Graphics graphics) {
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
     * 平铺水印(沿倾斜弧度方向横排水印)
     *
     * @param waterMarkText 水印的文字
     * @param font          字体
     * @param rotateRadian  旋转弧度
     * @param spaceX        X轴方向的间隔
     * @param spaceY        Y轴方向的间隔
     * @param canvasWidth   画布的宽度
     * @param canvasHeight  画布的高度
     * @return 水印图形的字节数组
     */
    public static byte[] tileWaterMark1(String waterMarkText, Font font, double rotateRadian,
                                        double spaceX, double spaceY,
                                        int canvasWidth, int canvasHeight) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        // Create a Graphics2D object from the BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();
        try {
            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 设置水印的颜色
            g2d.setColor(Color.BLACK);
            // 设置字体
            g2d.setFont(font);
            // 获取文本的宽高及高的各项指标
            TextDimensions textDimensions = getTextDimensions(waterMarkText, font, g2d);
            // 计算中心点
            double centerX = canvasWidth / 2.0;
            double centerY = canvasHeight / 2.0;
            // 移动到中心点
            g2d.translate(centerX, centerY);
            // 旋转弧度
            g2d.rotate(rotateRadian);
            // 计算旋转后的宽和高
            double targetWidth = Math.cos(-rotateRadian) * canvasWidth
                    - Math.sin(rotateRadian) * canvasHeight;
            double targetHeight = Math.sin(-rotateRadian) * canvasWidth
                    + Math.cos(rotateRadian) * canvasHeight;
            // X轴递增
            double stepX = textDimensions.getWidth() + spaceX;
            // Y轴递增
            double stepY = textDimensions.getDescent() + textDimensions.getDescent() + spaceY;
            // 初始化第一个文本的的x轴坐标
            double x = -targetWidth / 2;
            // 开始平铺
            for (double y = -targetHeight / 2 - spaceY; y < targetHeight / 2; y += stepY) {
                for (; x < targetWidth / 2; x += stepX) {
                    // 绘制文字
                    g2d.drawString(waterMarkText, (float) x, (float) y);
                }
                x -= targetWidth;
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
     * 平铺水印((沿水平方向横排水印))
     *
     * @param waterMarkText 水印的文字
     * @param font          字体
     * @param rotateRadian  旋转弧度
     * @param marginX       X轴外边距
     * @param marginY       Y轴外边距
     * @param spaceX        X轴方向的间隔
     * @param spaceY        Y轴方向的间隔
     * @param canvasWidth   画布的宽度
     * @param canvasHeight  画布的高度
     * @return 水印图形的字节数组
     */
    public static byte[] tileWaterMark2(String waterMarkText, Font font, double rotateRadian,
                                        double marginX, double marginY,
                                        double spaceX, double spaceY,
                                        int canvasWidth, int canvasHeight) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);
        // Create a Graphics2D object from the BufferedImage
        Graphics2D g2d = bufferedImage.createGraphics();
        try {
            // 设置抗锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 设置水印的颜色
            g2d.setColor(Color.BLACK);
            // 设置字体
            g2d.setFont(font);
            // 获取文本的宽高及高的各项指标
            TextDimensions textDimensions = getTextDimensions(waterMarkText, font, g2d);
            // 计算画水印完整的宽和高
            double targetWidth  = canvasWidth - marginX * 2;
            double targetHeight = canvasHeight - marginY * 2;
            // 计算旋转后的文本高度
            double rotateHeight = Math.sin(-rotateRadian) * textDimensions.getWidth() + Math.sin(Math.PI / 2.0 + rotateRadian) * textDimensions.getHeight();
            // X轴递增
            double stepX = Math.cos(-rotateRadian) * textDimensions.getWidth() + spaceX;
            // Y轴递增
            double stepY = rotateHeight + spaceY;
            // 移动坐标原点到完整图形的左上角
            g2d.translate(marginX, marginY + rotateHeight);
            // 开始平铺
            for (double y = 0; y < targetHeight; y += stepY) {
                for (double x = 0; x < targetWidth; x += stepX) {
                    // 字体旋转
                    AffineTransform rotateAffineTransform = AffineTransform.getRotateInstance(rotateRadian);
                    Font            rotateFont            = font.deriveFont(rotateAffineTransform);
                    g2d.setFont(rotateFont);
                    // 绘制文字
                    g2d.drawString(waterMarkText, (float) x, (float) y);
                }
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
}

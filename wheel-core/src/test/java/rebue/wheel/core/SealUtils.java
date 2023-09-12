package rebue.wheel.core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SealUtils {

    /**
     * 绘制圆形(或椭圆形)公章
     *
     * @param width  公章的宽度
     * @param height 公章的高度
     * @return 图形的字节数组
     */
    public static byte[] draw01(String title, String name, String date, int width, int height) throws IOException {
        return draw01(title, name, date, width, height, 8, 150);
    }

    /**
     * 绘制圆形(或椭圆形)公章
     *
     * @param width             公章的宽度
     * @param height            公章的高度
     * @param circleBorderWidth 圆边框的宽度
     * @param starWidth         五角星宽度
     * @return 图形的字节数组
     */
    public static byte[] draw01(String title, String name, String date, int width, int height, int circleBorderWidth, int starWidth) throws IOException {
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
            int circleHeight = width - circleBorderWidth - 1;       // 圆的高度
            g2d.setStroke(new BasicStroke(circleBorderWidth));      // 设置圆边框的宽度
            g2d.draw(new Ellipse2D.Double(circleLeft, circleTop, circleWidth, circleHeight));

            // 绘制中间的五角星
            int    radius    = starWidth / 2;           // 半径
            int    hornCount = 5;                       // 角的数量
            double angle     = Math.PI / hornCount;     // 旋转角度
            int[]  xPoints   = new int[2 * hornCount];
            int[]  yPoints   = new int[2 * hornCount];
            for (int i = 0; i < 2 * hornCount; i++) {
                double r = (i % 2 == 0) ? radius : radius * 0.4;
                xPoints[i] = (int) (centerX + r * Math.sin(i * angle));
                yPoints[i] = (int) (centerY - r * Math.cos(i * angle));
            }
            g2d.fill(new Polygon(xPoints, yPoints, 2 * hornCount));

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
     * 计算五角星10个坐标位置
     *
     * @param centerX  五角星中心点X轴
     * @param centerY  五角星中心点Y轴
     * @param x2Points 存储五角星10个点X轴
     * @param y2Points 存储五角星10个点Y轴
     *                 需要注意的是 第一个坐标必须指定
     * @return void
     * @author liuchao
     * @date 2023/3/14
     */
    private static void calculateFivePointedStarPoint(double centerX, double centerY, double x2Points[], double y2Points[]) {
        //圆周率
        float pi = 3.141592653F;


        x2Points[1] = centerX - (float) 10 / Math.cos((float) 36 / 360 * 2 * pi) *
                Math.cos((float) 36 / 360 * 2 * pi + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));
        y2Points[1] = centerY + (float) 10 / Math.cos((float) 36 / 360 * 2 * pi) *
                Math.sin((float) 36 / 360 * 2 * pi + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));

        x2Points[2] = centerX - (float) 30 *
                Math.cos((float) 36 / 360 * 2 * pi * 2 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));
        y2Points[2] = centerY + (float) 30 *
                Math.sin((float) 36 / 360 * 2 * pi * 2 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));

        x2Points[3] = centerX - (float) 10 / Math.cos((float) 36 / 360 * 2 * pi) *
                Math.cos((float) 36 / 360 * 2 * pi * 3 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));
        y2Points[3] = centerY + (float) 10 / Math.cos((float) 36 / 360 * 2 * pi) *
                Math.sin((float) 36 / 360 * 2 * pi * 3 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));

        x2Points[4] = centerX - (float) 30 *
                Math.cos((float) 36 / 360 * 2 * pi * 4 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));
        y2Points[4] = centerY + (float) 30 *
                Math.sin((float) 36 / 360 * 2 * pi * 4 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));

        x2Points[5] = centerX - (float) 10 / Math.cos((float) 36 / 360 * 2 * pi) *
                Math.cos((float) 36 / 360 * 2 * pi * 5 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));
        y2Points[5] = centerY + (float) 10 / Math.cos((float) 36 / 360 * 2 * pi) *
                Math.sin((float) 36 / 360 * 2 * pi * 5 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));

        x2Points[6] = centerX - (float) 30 *
                Math.cos((float) 36 / 360 * 2 * pi * 6 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));
        y2Points[6] = centerY + (float) 30 *
                Math.sin((float) 36 / 360 * 2 * pi * 6 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));

        x2Points[7] = centerX - (float) 10 / Math.cos((float) 36 / 360 * 2 * pi) *
                Math.cos((float) 36 / 360 * 2 * pi * 7 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));
        y2Points[7] = centerY + (float) 10 / Math.cos((float) 36 / 360 * 2 * pi) *
                Math.sin((float) 36 / 360 * 2 * pi * 7 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));

        x2Points[8] = centerX - (float) 30 *
                Math.cos((float) 36 / 360 * 2 * pi * 8 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));
        y2Points[8] = centerY + (float) 30 *
                Math.sin((float) 36 / 360 * 2 * pi * 8 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));

        x2Points[9] = centerX - (float) 10 / Math.cos((float) 36 / 360 * 2 * pi) *
                Math.cos((float) 36 / 360 * 2 * pi * 9 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));
        y2Points[9] = centerY + (float) 10 / Math.cos((float) 36 / 360 * 2 * pi) *
                Math.sin((float) 36 / 360 * 2 * pi * 9 + Math.atan((float) Math.abs(y2Points[0] - centerY)
                        / (float) Math.abs(x2Points[0] - centerX)));
    }
}

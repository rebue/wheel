package rebue.wheel.core;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QrcodeUtils {
    /**
     * 生成二维码
     *
     * @param text                 二维码中的文本内容
     * @param width                二维码的宽度
     * @param borderSize           二维码边框的大小(为0则没有边框)
     * @param padding              二维码的内边距
     * @param errorCorrectionLevel 二维码的容错率
     * @return 二维码图形的字节数组
     */
    public static byte[] genQrcode(String text, int width, int borderSize, int padding, ErrorCorrectionLevel errorCorrectionLevel) throws IOException, WriterException {
        // 设置二维码参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");                   // 防止乱码
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);   // 设置二维码的容错率
        // 生成二维码矩阵
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, width, hints);
        // 获取实际的二维码边界
        int[] enclosingRectangle = bitMatrix.getEnclosingRectangle();
        // 计算二维码裁剪白边后的图像尺寸
        int croppedLeft   = enclosingRectangle[0];
        int croppedTop    = enclosingRectangle[1];
        int croppedWidth  = enclosingRectangle[2];
        int croppedHeight = enclosingRectangle[3];

        // 计算图像的宽度(默认等于二维码裁剪白边后的宽度)
        width = croppedWidth;
        // 开始绘制二维码左上角的x,y坐标的初始值
        int init_value = borderSize + padding;
        // 如果有边框
        if (borderSize > 0) {
            width += 2 * borderSize;
        }
        if (padding > 0) {
            width += 2 * padding;
        }

        // 创建BufferedImage对象，并根据BitMatrix绘制二维码
        BufferedImage qrCodeImage = new BufferedImage(width, width, BufferedImage.TYPE_INT_ARGB);
        // 画边框
        if (borderSize > 0) {
            Graphics2D g2d = qrCodeImage.createGraphics();
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(borderSize));
            g2d.drawRect(borderSize / 2, borderSize / 2, width - borderSize, width - borderSize);
        }

        // 绘制二维码
        for (int x = 0; x < croppedWidth; x++) {
            for (int y = 0; y < croppedHeight; y++) {
                if (bitMatrix.get(croppedLeft + x, croppedTop + y))
                    qrCodeImage.setRGB(x + init_value, y + init_value, Color.BLACK.getRGB());
            }
        }

        // 将BufferedImage转换为字节数组
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(qrCodeImage, "png", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}

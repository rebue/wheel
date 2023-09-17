package rebue.wheel.core.pdf;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.PrivateKeySignature;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import rebue.wheel.core.QrcodeUtils;
import rebue.wheel.core.seal.SealText;
import rebue.wheel.core.seal.SealUtils;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@TestMethodOrder(MethodOrderer.MethodName.class)
public class PdfUtilsTests {
    public static final String SRC   = "pdf/测试模板.pdf";
    public static final String TEMP  = "target/pdf/temp.pdf";
    public static final String DEST  = "target/pdf/result.pdf";
    public static final String SIGN1 = "target/pdf/result-signed1.pdf";
    public static final String SIGN2 = "target/pdf/result-signed2.pdf";
    public static final String FONT1 = "pdf/FreeSans.ttf";
    public static final String FONT2 = "pdf/AlimamaFangYuanTiVF-Thin.ttf";
    public static final String FONT3 = "pdf/AlimamaShuHeiTi-Bold.ttf";

    public static final String SEAL1 = "src/test/resources/pdf/seal1.png";
    public static final String SEAL2 = "src/test/resources/pdf/seal2.png";
    public static final String SEAL3 = "src/test/resources/pdf/seal3.png";

    /**
     * 用下面命令可以生成密钥库文件
     * keytool -genkeypair -alias demo -validity 365 -keyalg RSA -keysize 2048 -keystore keystore.jks
     */
    public static final String KEYSTORE = "src/test/resources/pdf/ks.jks";
    public static final char[] PASSWORD = "password".toCharArray();

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void test01() throws Exception {
        File tempFile = new File(TEMP);
        log.debug("mkdirs tempFile: {}", tempFile.getParentFile().mkdirs());
        File dstFile = new File(DEST);
        log.debug("mkdirs dstFile: {}", dstFile.getParentFile().mkdirs());

        PdfDocument pdfDoc = PdfUtils.createPdfDoc(SRC, TEMP);

        PdfFont pdfFont1 = PdfFontFactory.createFont(FONT1, PdfEncodings.IDENTITY_H);
        PdfFont pdfFont2 = PdfFontFactory.createFont(FONT2, PdfEncodings.IDENTITY_H);
        PdfFont pdfFont3 = PdfFontFactory.createFont(FONT3, PdfEncodings.IDENTITY_H);

        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("param1", PdfField.builder().value("1234567890一二三四五六七八九十").font(pdfFont1).fontSize(12).build());   // 字体不对，乱码
        fields.put("param2", PdfField.builder().value("2234567890二二三四五六七八九十").font(pdfFont2).fontSize(12).build());   // 字体正确，不乱码
        fields.put("param3", "男");
        fields.put("param4", "1234567890一二三四五六七八九十");
        fields.put("param5", PdfField.builder().value(LocalDateTime.now().format(dateTimeFormatter)).font(pdfFont3).fontSize(12).build());   // 时间
        fields.put("param6", true);
        fields.put("param7", "1234567890一二三四五六七八九十");
        fields.put("param8", "1234567890一二三四五六七八九十");
        fields.put("param9", "1234567890一二三四五六七八九十");
        fields.put("param10", "1234567890一二三四五六七八九十");
        fields.put("param11", "1234567890一二三四五六七八九十");
        fields.put("param12", "1234567890一二三四五六七八九十");
        fields.put("qrcode1", PdfField.builder().fieldType(PdfFieldType.QRCODE).value("2234567890二二三四五六七八九十").build());  // 二维码
        PdfUtils.fillForm(pdfDoc, fields);

        pdfDoc.close();

        pdfDoc = PdfUtils.createPdfDoc(TEMP, DEST);
        PdfAcroForm pdfAcroForm = PdfFormCreator.getAcroForm(pdfDoc, true);

        // Being set as true, this parameter is responsible to generate an appearance Stream
        // while flattening for all form fields that don't have one. Generating appearances will
        // slow down form flattening, but otherwise Acrobat might render the pdf on its own rules.
        pdfAcroForm.setGenerateAppearance(true);

        // 展平表单的所有字段，这样图片才能覆盖在这些字段的上面
        // If no fields have been explicitly included, then all fields are flattened.
        // Otherwise, only the included fields are flattened.
        pdfAcroForm.flattenFields();

        // 用zxing库展示二维码
        byte[] qrcodeBytes = QrcodeUtils.genQrcode("1234567890一二三四五六七八九十", 50, 1, 1, ErrorCorrectionLevel.L);
        PdfUtils.addImage(pdfDoc, ImageDataFactory.create(qrcodeBytes), 250, 200);

        PdfUtils.addImage(pdfDoc, SEAL1, 250, 400);
        PdfUtils.showImage1(pdfAcroForm, "seal1", SEAL1);
        PdfUtils.showImage2(pdfDoc, pdfAcroForm, "seal2", SEAL2);
        PdfUtils.showImage3(pdfDoc, pdfAcroForm, "seal3", SEAL3);

        // 添加水印
        PdfUtils.addWaterMask1(pdfDoc, 2, SEAL1, 250, 500, 0.9f);
        PdfUtils.addWaterMask1(pdfDoc, 2, SEAL2, 250, 400, 0.9f);
        PdfUtils.addWaterMask1(pdfDoc, 2, ImageDataFactory.create(SEAL2), new Rectangle(250, 300, 100, 100), 0.9f);
        PdfUtils.addWaterMask1(pdfDoc, 2, SEAL2, new Rectangle(250, 200, 100, 100), 0.9f);
        PdfUtils.addWaterMask2(pdfDoc, 2, SEAL3, 250, 100, 0.9f);
        PdfUtils.addWaterMask2(pdfDoc, 2, SEAL3, new Rectangle(250, 0, 100, 100), 0.9f);

        ImageData imageData = ImageDataFactory.create(SealUtils.draw03(SealText.builder()
                        .text("中国很行")
                        .font(new Font("宋体", Font.PLAIN, 36))
                        .build(),
                30, 20, 8));
        PdfUtils.addWaterMask1(pdfDoc, 1, imageData, 50, 50, 0.9f);

        imageData = ImageDataFactory.create(SealUtils.draw03(SealText.builder()
                        .text("中    国")
                        .font(new Font("宋体", Font.PLAIN, 36))
                        .build(),
                50, 10, 12));
        PdfUtils.addWaterMask1(pdfDoc, 1, imageData, 300, 50, 0.9f);

        String topText        = "中国电子公章测试有限责任公司";
        String captionText    = "电子公章演示";
        String subcaptionText = "演示专用章";
        imageData = ImageDataFactory.create(SealUtils.draw01(SealText.builder()
                        .text(topText)
                        .font(new Font("宋体", Font.PLAIN, 36))
                        .marginTop(10D)
                        .build(),
                SealText.builder()
                        .text(captionText)
                        .font(new Font("宋体", Font.PLAIN, 24))
                        .marginTop(15D)
                        .build(),
                SealText.builder()
                        .text(subcaptionText)
                        .font(new Font("宋体", Font.BOLD, 24))
                        .build(),
                300, 10, 150));
//        imageData = ImageDataFactory.create(SealUtils.draw01(title, name, date));
        PdfUtils.addWaterMask1(pdfDoc, 3, imageData, 0, 500, 0.9f);

        imageData = ImageDataFactory.create(SealUtils.draw01(SealText.builder()
                        .text(topText)
                        .font(new Font("宋体", Font.PLAIN, 36))
                        .marginTop(10.0)
                        .build(),
                SealText.builder()
                        .text(captionText)
                        .font(new Font("宋体", Font.PLAIN, 24))
                        .marginTop(15.0)
                        .build(),
                SealText.builder()
                        .text(subcaptionText)
                        .font(new Font("宋体", Font.PLAIN, 24))
                        .build(),
                300, 10, 150));
        PdfUtils.addWaterMask1(pdfDoc, 3, imageData, 0, 300, 0.9f);

        imageData = ImageDataFactory.create(SealUtils.draw02(SealText.builder()
                        .text(topText)
                        .font(new Font("宋体", Font.PLAIN, 20))
                        .build(),
                SealText.builder()
                        .text(captionText)
                        .font(new Font("宋体", Font.BOLD, 16))
                        .build(),
                SealText.builder()
                        .text(subcaptionText)
                        .font(new Font("宋体", Font.BOLD, 16))
                        .marginBottom(4.0)
                        .marginLeft(2.0)
                        .build(),
                300, 180, 10, 2));
        PdfUtils.addWaterMask1(pdfDoc, 4, imageData, 50, 500, 0.9f);

        pdfDoc.close();

        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(Files.newInputStream(Paths.get(KEYSTORE)), PASSWORD);
        String              alias               = keyStore.aliases().nextElement();
        PrivateKey          privateKey          = (PrivateKey) keyStore.getKey(alias, PASSWORD);
        Certificate[]       chain               = keyStore.getCertificateChain(alias);
        PrivateKeySignature privateKeySignature = new PrivateKeySignature(privateKey, DigestAlgorithms.SHA256, provider.getName());

        String reason   = "reason 1";
        String location = "location 1";
        imageData = ImageDataFactory.create(SEAL1);
        PdfUtils.sign(new PdfReader(DEST), Files.newOutputStream(Paths.get(SIGN1)), reason, location, privateKeySignature, chain,
                4, new Rectangle(300, 300, imageData.getWidth(), imageData.getHeight()), 0.9f, imageData);

        reason = "reason 2";
        location = "location 2";
        imageData = ImageDataFactory.create(SealUtils.draw01(topText, captionText, subcaptionText));
        PdfUtils.sign(new PdfReader(SIGN1), Files.newOutputStream(Paths.get(SIGN2)), reason, location, privateKeySignature, chain,
                4, new Rectangle(100, 300, imageData.getWidth(), imageData.getHeight()), 0.7f, imageData);

    }
}
package rebue.wheel.core;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.signatures.DigestAlgorithms;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public static final String KEYSTORE = "src/test/resources/pdf/ks";
    public static final char[] PASSWORD = "password".toCharArray();

    public static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void test01() throws IOException, GeneralSecurityException {
        File tempFile = new File(TEMP);
        log.debug("mkdirs tempFile: {}", tempFile.getParentFile().mkdirs());
        File dstFile = new File(DEST);
        log.debug("mkdirs dstFile: {}", dstFile.getParentFile().mkdirs());

        PdfWriter   writer      = new PdfWriter(TEMP);
        PdfDocument pdfDoc      = new PdfDocument(new PdfReader(SRC), writer);
        PdfAcroForm pdfAcroForm = PdfFormCreator.getAcroForm(pdfDoc, true);

        // Being set as true, this parameter is responsible to generate an appearance Stream
        // while flattening for all form fields that don't have one. Generating appearances will
        // slow down form flattening, but otherwise Acrobat might render the pdf on its own rules.
        pdfAcroForm.setGenerateAppearance(true);

        PdfFont pdfFont1 = PdfFontFactory.createFont(FONT1, PdfEncodings.IDENTITY_H);
        PdfFont pdfFont2 = PdfFontFactory.createFont(FONT2, PdfEncodings.IDENTITY_H);
        PdfFont pdfFont3 = PdfFontFactory.createFont(FONT3, PdfEncodings.IDENTITY_H);
        pdfAcroForm.getField("param1").setValue("1234567890一二三四五六七八九十", pdfFont1, 12f);// 字体不对，乱码
        pdfAcroForm.getField("param2").setValue("2234567890二二三四五六七八九十", pdfFont2, 12f);// 字体正确，不乱码
        pdfAcroForm.getField("param3").setValue("男");// 未设置字体，只读下没有乱码

        // 换行
        PdfTextFormField param4 = (PdfTextFormField) pdfAcroForm.getField("param4");
        param4.setMultiline(true);
        param4.setValue("2234567890二二三四\n五六七八九十", pdfFont3, 12f);

        pdfAcroForm.getField("param5").setValue(LocalDateTime.now().format(dateTimeFormatter), pdfFont1, 12f);// 时间
        pdfAcroForm.getField("param6").setValue("true");//checkbox
        pdfAcroForm.getField("param7").setValue("2234567890二二三四五六七八九十", pdfFont2, 12f);// 字体正确，不乱码
        pdfAcroForm.getField("param8").setValue("2234567890二二三四五六七八九十", pdfFont2, 12f);// 字体正确，不乱码
        pdfAcroForm.getField("param9").setValue("2234567890二二三四五六七八九十", pdfFont2, 12f);// 字体正确，不乱码
        pdfAcroForm.getField("param10").setValue("2234567890二二三四五六七八九十", pdfFont2, 12f);// 字体正确，不乱码
        pdfAcroForm.getField("param11").setValue("2234567890二二三四五六七八九十", pdfFont2, 12f);// 字体正确，不乱码
        // 换行
        PdfTextFormField param12 = (PdfTextFormField) pdfAcroForm.getField("param12");
        param12.setMultiline(true);
        param12.setValue("2234567890\n二二三四五六七八九十", pdfFont2, 12f);// 字体正确，不乱码

        // 二维码
        PdfUtils.showQrcode(pdfDoc, pdfAcroForm, "qrcode1", "2234567890二二三四五六七八九十");

        pdfDoc.close();

        WriterProperties writerProperties = new WriterProperties();
        // 设置只读
//        writerProperties.setStandardEncryption(null, null,
//                EncryptionConstants.ALLOW_PRINTING,
//                EncryptionConstants.STANDARD_ENCRYPTION_128);
        writer = new PdfWriter(DEST, writerProperties);
        pdfDoc = new PdfDocument(new PdfReader(TEMP), writer);
        pdfAcroForm = PdfFormCreator.getAcroForm(pdfDoc, true);

        // Being set as true, this parameter is responsible to generate an appearance Stream
        // while flattening for all form fields that don't have one. Generating appearances will
        // slow down form flattening, but otherwise Acrobat might render the pdf on its own rules.
        pdfAcroForm.setGenerateAppearance(true);

        // 展平表单的所有字段，这样图片才能覆盖在这些字段的上面
        // If no fields have been explicitly included, then all fields are flattened.
        // Otherwise, only the included fields are flattened.
        pdfAcroForm.flattenFields();

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

        String title = "中国电子公章测试有限责任公司";
        String name  = "电子公章演示";
        String date  = "专用章";
        ImageData imageData = ImageDataFactory.create(SealUtils.draw01(SealUtils.SealText.builder()
                        .text(title)
                        .font(new Font("宋体", Font.PLAIN, 36))
                        .margin(10D)
                        .build(),
                SealUtils.SealText.builder()
                        .text(name)
                        .font(new Font("宋体", Font.PLAIN, 24))
                        .margin(15D)
                        .build(),
                new SealUtils.SealText(date, new Font("宋体", Font.PLAIN, 24)),
                300, 300, 10, 150));
//        imageData = ImageDataFactory.create(SealUtils.draw01(title, name, date));
        PdfUtils.addWaterMask1(pdfDoc, 3, imageData, 0, 500, 0.9f);

        imageData = ImageDataFactory.create(SealUtils.draw01(SealUtils.SealText.builder()
                        .text(title)
                        .font(new Font("宋体", Font.PLAIN, 36))
                        .margin(10D)
                        .build(),
                SealUtils.SealText.builder()
                        .text(name)
                        .font(new Font("宋体", Font.PLAIN, 24))
                        .margin(15D)
                        .build(),
                new SealUtils.SealText(date, new Font("宋体", Font.PLAIN, 24)),
                300, 150, 10, 150));
        PdfUtils.addWaterMask1(pdfDoc, 3, imageData, 0, 300, 0.9f);

        pdfDoc.close();

        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(Files.newInputStream(Paths.get(KEYSTORE)), PASSWORD);
        String        alias      = ks.aliases().nextElement();
        PrivateKey    privateKey = (PrivateKey) ks.getKey(alias, PASSWORD);
        Certificate[] chain      = ks.getCertificateChain(alias);

        String reason   = "reason 1";
        String location = "location 1";
        imageData = ImageDataFactory.create(SEAL1);
        PdfUtils.sign(new PdfReader(DEST), Files.newOutputStream(Paths.get(SIGN1)), reason, location, privateKey,
                DigestAlgorithms.SHA256, provider.getName(), chain,
                4, new Rectangle(300, 300, imageData.getWidth(), imageData.getHeight()), 0.9f, imageData);

        reason = "reason 2";
        location = "location 2";
        imageData = ImageDataFactory.create(SealUtils.draw01(title, name, date));
        PdfUtils.sign(new PdfReader(SIGN1), Files.newOutputStream(Paths.get(SIGN2)), reason, location, privateKey,
                DigestAlgorithms.SHA256, provider.getName(), chain,
                4, new Rectangle(100, 300, imageData.getWidth(), imageData.getHeight()), 0.7f, imageData);

    }
}
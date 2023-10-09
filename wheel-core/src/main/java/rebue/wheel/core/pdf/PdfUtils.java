package rebue.wheel.core.pdf;

import com.itextpdf.barcodes.BarcodeQRCode;
import com.itextpdf.barcodes.qrcode.EncodeHintType;
import com.itextpdf.barcodes.qrcode.ErrorCorrectionLevel;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.*;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.signatures.*;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class PdfUtils {

    /**
     * 创建PDF文档
     *
     * @param srcPath 源文件路径
     * @param dstPath 输出文件路径
     * @return PDF文档
     */
    @SneakyThrows
    public static PdfDocument createPdfDoc(String srcPath, String dstPath) {
        return createPdfDoc(srcPath, dstPath, false, null);
    }

    /**
     * 创建PDF文档
     *
     * @param srcPath       源文件路径
     * @param dstPath       输出文件路径
     * @param isReadOnly    输出文件是否只读
     * @param ownerPassword (如果isReadOnly为false，此值被忽略)设置只读后，想修改时需要用到的密码，如果为null则自动生成一个随机密码
     * @return PDF文档
     */
    @SneakyThrows
    public static PdfDocument createPdfDoc(String srcPath, String dstPath, boolean isReadOnly, byte[] ownerPassword) {
        PdfWriter writer;
        if (isReadOnly) {
            writer = new PdfWriter(dstPath, createReadOnlyWriterProperties(ownerPassword));
        } else {
            writer = new PdfWriter(dstPath);
        }
        return new PdfDocument(new PdfReader(srcPath), writer);
    }

    /**
     * 创建PDF文档
     *
     * @param srcPath      源文件路径
     * @param outputStream 输出流
     * @return PDF文档
     */
    @SneakyThrows
    @SuppressWarnings("unused")
    public static PdfDocument createPdfDoc(String srcPath, OutputStream outputStream) {
        return createPdfDoc(srcPath, outputStream, false, null);
    }

    /**
     * 创建PDF文档
     *
     * @param srcPath       源文件路径
     * @param outputStream  输出流
     * @param isReadOnly    输出文件是否只读
     * @param ownerPassword (如果isReadOnly为false，此值被忽略)设置只读后，想修改时需要用到的密码，如果为null则自动生成一个随机密码
     * @return PDF文档
     */
    @SneakyThrows
    public static PdfDocument createPdfDoc(String srcPath, OutputStream outputStream,
                                           boolean isReadOnly, byte[] ownerPassword) {
        PdfWriter writer;
        if (isReadOnly) {
            writer = new PdfWriter(outputStream, createReadOnlyWriterProperties(ownerPassword));
        } else {
            writer = new PdfWriter(outputStream);
        }
        return new PdfDocument(new PdfReader(srcPath), writer);
    }

    /**
     * 创建只读的写属性
     *
     * @param ownerPassword 设置只读后，想修改时需要用到的密码，如果为null则自动生成一个随机密码
     * @return 写属性
     */
    @NonNull
    private static WriterProperties createReadOnlyWriterProperties(byte[] ownerPassword) {
        WriterProperties writerProperties = new WriterProperties();
        // 设置只读
        writerProperties.setStandardEncryption(null, ownerPassword,
                EncryptionConstants.ALLOW_PRINTING,
                EncryptionConstants.STANDARD_ENCRYPTION_128);
        return writerProperties;
    }

    public static PdfAcroForm fillForm(PdfDocument pdfDoc, Map<String, ?> fields) {
        PdfAcroForm pdfAcroForm = PdfFormCreator.getAcroForm(pdfDoc, false);

        // Being set as true, this parameter is responsible to generate an appearance Stream
        // while flattening for all form fields that don't have one. Generating appearances will
        // slow down form flattening, but otherwise Acrobat might render the pdf on its own rules.
        pdfAcroForm.setGenerateAppearance(true);

        for (Map.Entry<String, ?> field : fields.entrySet()) {
            PdfFormField pdfFormField = pdfAcroForm.getField(field.getKey());
            if (pdfFormField == null) continue;
            if (field.getValue() instanceof PdfField) {
                PdfField pdfField = (PdfField) field.getValue();
                switch (pdfField.getFieldType()) {
                    case TEXT:
                        String text = pdfField.getValue().toString();
                        if (StringUtils.isBlank(text)) continue;
                        // 计算是否要折行
                        float fontWidth = pdfField.getFont().getWidth(text.charAt(0), pdfField.getFontSize());
                        PdfArray fieldRect = pdfFormField.getWidgets().get(0).getRectangle();
                        float fieldWidth = ((PdfNumber) fieldRect.get(2)).floatValue()
                                - ((PdfNumber) fieldRect.get(0)).floatValue();
                        int count = (int) (fieldWidth / fontWidth);
                        // 如果需要折行
                        if (count < text.length()) {
                            ((PdfTextFormField) pdfFormField).setMultiline(true);
                            final StringJoiner sj = new StringJoiner("\n");
                            int beginIndex = 0;
                            int endIndex = count;
                            while (endIndex < text.length()) {
                                sj.add(text.substring(beginIndex, endIndex));
                                beginIndex = endIndex;
                                endIndex += count;
                            }
                        }
                        pdfFormField.setValue(text, pdfField.getFont(), pdfField.getFontSize());
                        break;
                    case QRCODE:
                        String qrCodeContent = pdfField.getValue().toString();
                        if (StringUtils.isBlank(qrCodeContent)) continue;
                        showQrcode(pdfDoc, pdfAcroForm, field.getKey(), qrCodeContent);
                        break;
                    default:
                        throw new RuntimeException("不会运行到这里");
                }
            } else {
                pdfFormField.setValue(field.getValue().toString());
            }
        }

        return pdfAcroForm;
    }

    /**
     * 展示图片(直接使用 setImage 设置)
     *
     * @param form      表单
     * @param fieldName 字段名
     * @param imagePath 图片路径
     */
    public static void showImage1(PdfAcroForm form, String fieldName, String imagePath) throws IOException {
        PdfButtonFormField buttonFormField = (PdfButtonFormField) form.getField(fieldName);
        buttonFormField.setImage(imagePath);
//        PdfFormAnnotation  formAnnotation  = buttonFormField.getFirstFormAnnotation();
//        formAnnotation.setBackgroundColor(WebColors.getCMYKColor("transparent"));
//        PdfDictionary pdfObject    = buttonFormField.getPdfObject();
//        PdfDictionary dictionaryMk = pdfObject.getAsDictionary(PdfName.MK);
//        PdfObject     pdfObjectBg = dictionaryMk.get(PdfName.BG);
//        dictionaryMk.entrySet().
//        new Cell().setBackgroundColor(WebColors.getCMYKColor("transparent"), 0.1f);
    }

    /**
     * 展示图片(直接使用 setImageAsForm 设置)
     *
     * @param doc       pdf文档
     * @param form      表单
     * @param fieldName 字段名
     * @param imagePath 图片路径
     */
    public static void showImage2(PdfDocument doc, PdfAcroForm form, String fieldName, String imagePath) throws IOException {
        PdfButtonFormField buttonFormField = (PdfButtonFormField) form.getField(fieldName);
        ImageData imageData = ImageDataFactory.create(imagePath);
        Image image = new Image(imageData, 0, 0);
//        image.setOpacity(0.1f);
        PdfFormXObject pdfFormXObject = new PdfFormXObject(new Rectangle(image.getImageWidth(), image.getImageHeight()));
        Canvas canvas = new Canvas(pdfFormXObject, doc);
//        canvas.setBackgroundColor(ColorConstants.WHITE, 0.1f);
        canvas.add(image);
        buttonFormField.setImageAsForm(pdfFormXObject);
    }

    /**
     * 展示图片(先新建一个 PdfButtonFormField，然后替换)
     *
     * @param doc       pdf文档
     * @param form      表单
     * @param fieldName 字段名
     * @param imagePath 图片路径
     */
    public static void showImage3(PdfDocument doc, PdfAcroForm form, String fieldName, String imagePath) throws IOException {
        PdfButtonFormField buttonFormField = (PdfButtonFormField) form.getField(fieldName);
        PdfWidgetAnnotation widget = buttonFormField.getFirstFormAnnotation().getWidget();
        Rectangle rectangle = widget.getRectangle().toRectangle();
        PdfPage page = widget.getPage();
        PdfButtonFormField pushButton = new PushButtonFormFieldBuilder(doc, fieldName)
                .setWidgetRectangle(rectangle)
                .setPage(page)
                .createPushButton();

//        // 设置图片背景透明(设置成功但是不起作用)
//        PdfDictionary pdfDictionaryMk = new PdfDictionary();
//        pdfDictionaryMk.put(PdfName.TP, new PdfNumber(1));
//        pushButton.getPdfObject().put(PdfName.MK, pdfDictionaryMk);

        ImageData imageData = ImageDataFactory.create(imagePath);
        Image image = new Image(imageData, 0, 0);
//        image.setOpacity(0.1f);
        PdfFormXObject pdfFormXObject = new PdfFormXObject(new Rectangle(image.getImageWidth(), image.getImageHeight()));
        Canvas canvas = new Canvas(pdfFormXObject, doc);
//        canvas.setBackgroundColor(ColorConstants.WHITE, 0.1f);
        canvas.add(image);
        pushButton.setImageAsForm(pdfFormXObject);
//        pushButton.setImage(imagePath);

        form.replaceField(fieldName, pushButton);
    }

    /**
     * 添加图片
     *
     * @param pdfDoc    pdf文档
     * @param imageData 图像数据
     * @param left      图片左边坐标(当前页面)
     * @param bottom    图片底边坐标(当前页面)
     */
    public static void addImage(PdfDocument pdfDoc, ImageData imageData, float left, float bottom) {
        Image image = new Image(imageData, left, bottom);
        Document doc = new Document(pdfDoc);
        doc.add(image);
    }

    /**
     * 添加图片
     *
     * @param pdfDoc    pdf文档
     * @param imagePath 图片路径
     * @param left      图片左边坐标(当前页面)
     * @param bottom    图片底边坐标(当前页面)
     */
    @SneakyThrows
    public static void addImage(PdfDocument pdfDoc, String imagePath, float left, float bottom) {
        addImage(pdfDoc, ImageDataFactory.create(imagePath), left, bottom);
    }

    /**
     * 添加水印(直接添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imageData   图像数据
     * @param rectangle   显示位置和范围
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    private static void addWaterMask0(PdfDocument doc, int pageNum, ImageData imageData, Rectangle rectangle, float fillOpacity) {
        PdfPage page = doc.getPage(pageNum);
        if (rectangle == null) {
            rectangle = page.getPageSize();
        }
        PdfCanvas canvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), doc);
        canvas.saveState();
        PdfExtGState state = new PdfExtGState().setFillOpacity(fillOpacity);    // 设置填充的透明度
        canvas.setExtGState(state);
        canvas.addImageFittedIntoRectangle(imageData, rectangle, false);
        canvas.restoreState();
    }

    /**
     * 添加水印(直接添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imageData   图像数据
     * @param left        水印的左边坐标
     * @param bottom      水印的底边坐标
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    @SneakyThrows
    private static void addWaterMask0(PdfDocument doc, int pageNum, ImageData imageData, float left, float bottom, float fillOpacity) {
        addWaterMask0(doc, pageNum, imageData, new Rectangle(left, bottom, imageData.getWidth(), imageData.getHeight()), fillOpacity);
    }

    /**
     * 添加水印(直接添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imagePath   图片路径
     * @param rectangle   显示位置和范围
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    @SneakyThrows
    private static void addWaterMask0(PdfDocument doc, int pageNum, String imagePath, Rectangle rectangle, float fillOpacity) {
        addWaterMask0(doc, pageNum, ImageDataFactory.create(imagePath), rectangle, fillOpacity);
    }

    /**
     * 添加水印(直接添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imagePath   图片路径
     * @param left        水印的左边坐标
     * @param bottom      水印的底边坐标
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    @SneakyThrows
    private static void addWaterMask0(PdfDocument doc, int pageNum, String imagePath, float left, float bottom, float fillOpacity) {
        ImageData imageData = ImageDataFactory.create(imagePath);
        addWaterMask0(doc, pageNum, imageData, new Rectangle(left, bottom, imageData.getWidth(), imageData.getHeight()), fillOpacity);
    }

    /**
     * 添加水印(直接添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imageData   图像数据
     * @param rectangle   显示位置和范围
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    public static void addWaterMask1(PdfDocument doc, int pageNum, ImageData imageData, Rectangle rectangle, float fillOpacity) {
        addWaterMask0(doc, pageNum, imageData, rectangle, fillOpacity);
    }

    /**
     * 添加水印(直接添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imageData   图像数据
     * @param left        水印的左边坐标
     * @param bottom      水印的底边坐标
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    public static void addWaterMask1(PdfDocument doc, int pageNum, ImageData imageData, float left, float bottom, float fillOpacity) {
        addWaterMask0(doc, pageNum, imageData, left, bottom, fillOpacity);
    }

    /**
     * 添加水印(直接添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imagePath   图片路径
     * @param rectangle   显示位置和范围
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    public static void addWaterMask1(PdfDocument doc, int pageNum, String imagePath, Rectangle rectangle, float fillOpacity) {
        addWaterMask0(doc, pageNum, imagePath, rectangle, fillOpacity);
    }

    /**
     * 添加水印(直接添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imagePath   图片路径
     * @param left        水印的左边坐标
     * @param bottom      水印的底边坐标
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    public static void addWaterMask1(PdfDocument doc, int pageNum, String imagePath, float left, float bottom, float fillOpacity) {
        addWaterMask0(doc, pageNum, imagePath, left, bottom, fillOpacity);
    }

    /**
     * 添加水印(在结束绘制页面事件时添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imageData   图像数据
     * @param rectangle   显示位置和范围
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    public static void addWaterMask2(PdfDocument doc, int pageNum, ImageData imageData, Rectangle rectangle, float fillOpacity) {
        // 监听结束绘制每一个页面的事件，在结束时再绘制图片，可使图片在顶层
        doc.addEventHandler(PdfDocumentEvent.END_PAGE, event -> {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdfDoc = docEvent.getDocument();
            PdfPage curPage = docEvent.getPage();
            int curPageNum = pdfDoc.getPageNumber(curPage);
            if (curPageNum != pageNum) return;

            addWaterMask0(doc, pageNum, imageData, rectangle, fillOpacity);
        });
    }

    /**
     * 添加水印(在结束绘制页面事件时添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imageData   图像数据
     * @param left        水印的左边坐标
     * @param bottom      水印的底边坐标
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    @SneakyThrows
    public static void addWaterMask2(PdfDocument doc, int pageNum, ImageData imageData, float left, float bottom, float fillOpacity) {
        addWaterMask2(doc, pageNum, imageData, new Rectangle(left, bottom, imageData.getWidth(), imageData.getHeight()), fillOpacity);
    }

    /**
     * 添加水印(在结束绘制页面事件时添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imagePath   图片路径
     * @param rectangle   显示位置和范围
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    @SneakyThrows
    public static void addWaterMask2(PdfDocument doc, int pageNum, String imagePath, Rectangle rectangle, float fillOpacity) {
        addWaterMask2(doc, pageNum, ImageDataFactory.create(imagePath), rectangle, fillOpacity);
    }

    /**
     * 添加水印(在结束绘制页面事件时添加)
     *
     * @param doc         pdf文档
     * @param pageNum     页码(从1开始)
     * @param imagePath   图片路径
     * @param left        水印的左边坐标
     * @param bottom      水印的底边坐标
     * @param fillOpacity 填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     */
    @SneakyThrows
    public static void addWaterMask2(PdfDocument doc, int pageNum, String imagePath, float left, float bottom, float fillOpacity) {
        addWaterMask2(doc, pageNum, ImageDataFactory.create(imagePath), left, bottom, fillOpacity);
    }

    /**
     * 展示二维码
     *
     * @param doc       pdf文档
     * @param form      pdf表单
     * @param fieldName 字段名称
     * @param content   二维码内容
     */
    public static void showQrcode(PdfDocument doc, PdfAcroForm form, String fieldName, String content) {
        PdfFormField qrcode1Field = form.getField(fieldName);
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");   // 设置UTF-8， 防止中文乱码
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L); // 设置二维码的容错性
        BarcodeQRCode qrcode1 = new BarcodeQRCode(content, hints);
        PdfWidgetAnnotation annotation = qrcode1Field.getWidgets().get(0);
        annotation.setAppearance(PdfName.N, qrcode1.createFormXObject(doc).getPdfObject());
    }

    /**
     * 数字签章
     *
     * @param reader              读取器
     * @param outputStream        输出流
     * @param reason              原因(一般写明此章的用途)
     * @param location            位置(一般为发布者的地址，不用很详细，省或市就可以了)
     * @param privateKeySignature 私钥签名器
     * @param chain               证书链
     * @param pageNum             页码(签章的页面)
     * @param rectangle           签章的位置和范围
     * @param fillOpacity         填充的透明度(0-1之间，0为完全透明，1为完全不透明)
     * @param imageData           签章的图像
     */
    public static void sign(PdfReader reader, OutputStream outputStream, String reason, String location,
                            PrivateKeySignature privateKeySignature, Certificate[] chain,
                            int pageNum, Rectangle rectangle, float fillOpacity, ImageData imageData)
            throws GeneralSecurityException, IOException {
        PdfSigner pdfSigner = new PdfSigner(reader, outputStream, new StampingProperties());
        pdfSigner.setCertificationLevel(PdfSigner.CERTIFIED_NO_CHANGES_ALLOWED); // 设置鉴定级别为不允许修改

        // Create the signature appearance
        PdfSignatureAppearance appearance = pdfSigner.getSignatureAppearance();
        appearance.setReason(reason);
        appearance.setLocation(location);
        appearance.setPageNumber(pageNum); // 签名放到第几页
        appearance.setPageRect(rectangle);

        // 将图片绘制到图层2(绘制文本的那一层，官方示例文档: https://kb.itextpdf.com/home/it7kb/examples/digital-signing-with-itext/part-iv-appearances#PartIVAppearances-CompletelyCustomAppearancesLayers)
        PdfFormXObject layer2 = appearance.getLayer2();
        Rectangle imageRectangle = layer2.getBBox().toRectangle();
        PdfCanvas canvas = new PdfCanvas(layer2, pdfSigner.getDocument());
        canvas.saveState();
        PdfExtGState state = new PdfExtGState().setFillOpacity(fillOpacity);    // 设置填充的透明度
        canvas.setExtGState(state);
        canvas.addImageFittedIntoRectangle(imageData, imageRectangle, false);
        canvas.restoreState();

        IExternalDigest digest = new BouncyCastleDigest();
        // Sign the document using the detached mode, CMS or CAdES equivalent.
        pdfSigner.signDetached(digest, privateKeySignature, chain, null, null, null, 0, PdfSigner.CryptoStandard.CMS);
    }


}

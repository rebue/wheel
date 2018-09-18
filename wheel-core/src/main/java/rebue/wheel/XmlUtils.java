package rebue.wheel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class XmlUtils {
    private static final Logger _log = LoggerFactory.getLogger(XmlUtils.class);

    public static Document getDocumentFromInputStream(InputStream inputStream) throws DocumentException, SAXException {
        SAXReader saxReader = new SAXReader();
        saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        return saxReader.read(inputStream);
    }

    public static String getXmlFromRequest(ServletRequest servletRequest) throws DocumentException, SAXException, IOException {
        Document document = getDocumentFromInputStream(servletRequest.getInputStream());
        return document.asXML();
    }

    public static Map<String, Object> xmlToMap(InputStream inputStream) throws DocumentException, SAXException {
        return xmlToMap(getDocumentFromInputStream(inputStream));
    }

    public static Map<String, Object> xmlToMap(String xmlText) throws DocumentException {
        return xmlToMap(DocumentHelper.parseText(xmlText));
    }

    private static Map<String, Object> xmlToMap(Document document) {
        // 将解析结果存储在HashMap中
        Map<String, Object> map = new HashMap<>();

        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        @SuppressWarnings("unchecked")
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList)
            map.put(e.getName(), e.getText());

        return map;
    }

    public static String mapToXml(Map<String, Object> map) {
        String xmlResult = "";

        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (String key : map.keySet()) {
            sb.append("<" + key + ">" + "<![CDATA[" + map.get(key) + "]]>" + "</" + key + ">");
        }
        sb.append("</xml>");
        xmlResult = sb.toString();

        _log.debug("map to xml:" + xmlResult);
        return xmlResult;
    }

}

package rebue.wheel.serialization.xml;

import java.io.File;
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

    private static SAXReader getReader() throws SAXException {
        final SAXReader saxReader = new SAXReader();
        saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        return saxReader;
    }

    public static Document getDocument(final File file) throws DocumentException, SAXException {
        return getReader().read(file);
    }

    public static Document getDocument(final InputStream inputStream) throws DocumentException, SAXException {
        return getReader().read(inputStream);
    }

    public static String getXmlFromRequest(final ServletRequest servletRequest) throws DocumentException, SAXException, IOException {
        return getDocument(servletRequest.getInputStream()).asXML();
    }

    public static Map<String, Object> xmlToMap(final InputStream inputStream) throws DocumentException, SAXException {
        return xmlToMap(getDocument(inputStream));
    }

    public static Map<String, Object> xmlToMap(final String xmlText) throws DocumentException {
        return xmlToMap(DocumentHelper.parseText(xmlText));
    }

    private static Map<String, Object> xmlToMap(final Document document) {
        // 将解析结果存储在HashMap中
        final Map<String, Object> map = new HashMap<>();

        // 得到xml根元素
        final Element root = document.getRootElement();
        // 得到根元素的所有子节点
        final List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (final Element e : elementList) {
            map.put(e.getName(), e.getText());
        }

        return map;
    }

    public static String mapToXml(final Map<String, Object> map) {
        String xmlResult = "";

        final StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (final String key : map.keySet()) {
            sb.append("<" + key + ">" + "<![CDATA[" + map.get(key) + "]]>" + "</" + key + ">");
        }
        sb.append("</xml>");
        xmlResult = sb.toString();

        _log.debug("map to xml:" + xmlResult);
        return xmlResult;
    }

}

package rebue.wheel;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlUtils {
    private static final Logger _log = LoggerFactory.getLogger(XmlUtils.class);

    public static Map<String, Object> xmlToMap(InputStream inputStream) throws DocumentException {
        return xmlToMap(new SAXReader().read(inputStream));
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

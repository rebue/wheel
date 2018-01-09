package rebue.wheel;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlUtils {
    private static final Logger _log = LoggerFactory.getLogger(XmlUtils.class);

    public static Map<String, String> xmlToMap(InputStream inputStream) throws IOException, DocumentException {
        // 将解析结果存储在HashMap中
        Map<String, String> map = new HashMap<String, String>();

        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        @SuppressWarnings("unchecked")
        List<Element> elementList = root.elements();

        // 遍历所有子节点
        for (Element e : elementList)
            map.put(e.getName(), e.getText());

        // 释放资源
        inputStream.close();
        inputStream = null;

        return map;
    }

    public static String mapToXml(Map<String, String> map) {
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

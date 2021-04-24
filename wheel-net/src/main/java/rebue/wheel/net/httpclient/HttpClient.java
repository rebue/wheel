package rebue.wheel.net.httpclient;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.dom4j.DocumentException;

import rebue.wheel.serialization.xml.XmlUtils;

public interface HttpClient {

    /**
     * 发出GET请求
     *
     * @param url 请求的地址
     */
    String get(String url) throws IOException;

    /**
     * 发出带参数的GET请求
     *
     * @param url           请求的地址
     * @param requestParams 请求的参数
     * 
     * @return 响应的字符串
     */
    default String get(final String url, final Map<String, Object> requestParams) throws IOException {
        final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final StringBuilder    sb      = new StringBuilder();
        sb.append(url);
        sb.append("?");
        for (final Map.Entry<String, Object> item : requestParams.entrySet()) {
            String value = null;
            if (item.getValue() instanceof Date) {
                if (item.getKey().endsWith("Date")) {
                    value = sdfDate.format((Date) item.getValue());
                }
                else if (item.getKey().endsWith("Time")) {
                    value = sdfTime.format((Date) item.getValue());
                }
            }
            else {
                value = item.getValue().toString();
            }
            sb.append(item.getKey());
            sb.append("=");
            sb.append(URLEncoder.encode(value, "utf-8"));
            sb.append("&");
        }
        return get(sb.substring(0, sb.length() - 1));
    }

    /**
     * 发出POST请求
     *
     * @param url 请求的地址
     * 
     * @return 响应的字符串
     */
    String post(String url) throws IOException;

    /**
     * 发出POST请求(将Map对象转为请求的FORM参数)
     *
     * @param url           请求的地址
     * @param requestParams 请求的参数
     * 
     * @return 响应的字符串
     */
    String postByFormParams(String url, Map<String, Object> requestParams) throws IOException;

    /**
     * 发出POST请求(参数为json形式的字符串)
     *
     * @param url        请求的地址
     * @param jsonParams 请求的参数
     * 
     * @return 响应的字符串
     */
    String postByJsonParams(String url, String jsonParams) throws IOException;

    /**
     * 发出POST请求(参数为json形式的字符串)
     *
     * @param url           请求的地址
     * @param requestParams 请求的参数(一个Bean或Map&lt;String,Object&gt;)
     * 
     * @return 响应的字符串
     */
    String postByJsonParams(String url, Object requestParams) throws IOException;

    /**
     * 发出POST请求(将Map对象转为请求的XML参数)
     *
     * @param url       请求的地址
     * @param xmlParams 请求的参数
     * 
     * @return 响应的字符串
     */
    Map<String, Object> postByXmlParams(String url, String xmlParams) throws IOException, DocumentException;

    /**
     * 发出POST请求(将Map对象转为请求的XML参数)
     *
     * @param url           请求的地址
     * @param requestParams 请求的参数
     * 
     * @return 响应的字符串
     */
    default Map<String, Object> postByXmlParams(final String url, final Map<String, Object> requestParams) throws IOException, DocumentException {
        return postByXmlParams(url, XmlUtils.mapToXml(requestParams));
    }

    /**
     * 发出PUT请求
     *
     * @param url 请求的地址
     * 
     * @return 响应的字符串
     */
    String put(String url) throws IOException;

    /**
     * 发出PUT请求(将Map对象转为请求的FORM参数)
     *
     * @param url           请求的地址
     * @param requestParams 请求的参数
     * 
     * @return 响应的字符串
     */
    String putByFormParams(String url, Map<String, Object> requestParams) throws IOException;

    /**
     * 发出PUT请求(参数为json形式的字符串)
     *
     * @param url        请求的地址
     * @param jsonParams 请求的参数
     * 
     * @return 响应的字符串
     */
    String putByJsonParams(String url, String jsonParams) throws IOException;

    /**
     * 发出PUT请求(参数为json形式的字符串)
     *
     * @param url           请求的地址
     * @param requestParams 请求的参数(一个Bean或Map&lt;String,Object&gt;)
     * 
     * @return 响应的字符串
     */
    String putByJsonParams(String url, Object requestParams) throws IOException;

    /**
     * 发出DELETE请求
     *
     * @param url 请求的地址
     * 
     * @return 响应的字符串
     */
    String delete(String url) throws IOException;

    /**
     * 发出DELETE请求(将Map对象转为请求的FORM参数)
     *
     * @param url           请求的地址
     * @param requestParams 请求的参数
     * 
     * @return 响应的字符串
     */
    String deleteByFormParams(String url, Map<String, Object> requestParams) throws IOException;

}
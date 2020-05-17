package rebue.wheel.http;

import java.io.IOException;
import java.util.Map;

import org.dom4j.DocumentException;

import rebue.wheel.XmlUtils;

public interface HttpClient {

    /**
     * 发出GET请求
     * 
     * @param url
     *            请求的地址
     */
    String get(String url) throws IOException;

    /**
     * 发出POST请求
     * 
     * @param url
     *            请求的地址
     * @return 响应的字符串
     */
    String post(String url) throws IOException;

    /**
     * 发出POST请求(将Map对象转为请求的FORM参数)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数
     * @return 响应的字符串
     */
    String postByFormParams(String url, Map<String, Object> requestParams) throws IOException;

    /**
     * 发出POST请求(参数为json形式的字符串)
     * 
     * @param url
     *            请求的地址
     * @param jsonParams
     *            请求的参数
     * @return 响应的字符串
     */
    String postByJsonParams(String url, String jsonParams) throws IOException;

    /**
     * 发出POST请求(参数为json形式的字符串)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数(一个Bean或Map&lt;String,Object&gt;)
     * @return 响应的字符串
     */
    String postByJsonParams(String url, Object requestParams) throws IOException;

    /**
     * 发出POST请求(将Map对象转为请求的XML参数)
     * 
     * @param url
     *            请求的地址
     * @param xmlParams
     *            请求的参数
     * @return 响应的字符串
     */
    Map<String, Object> postByXmlParams(String url, String xmlParams) throws IOException, DocumentException;

    /**
     * 发出POST请求(将Map对象转为请求的XML参数)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数
     * @return 响应的字符串
     */
    default Map<String, Object> postByXmlParams(final String url, final Map<String, Object> requestParams) throws IOException, DocumentException {
        return postByXmlParams(url, XmlUtils.mapToXml(requestParams));
    }

    /**
     * 发出PUT请求
     * 
     * @param url
     *            请求的地址
     * @return 响应的字符串
     */
    String put(String url) throws IOException;

    /**
     * 发出PUT请求(将Map对象转为请求的FORM参数)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数
     * @return 响应的字符串
     */
    String putByFormParams(String url, Map<String, Object> requestParams) throws IOException;

    /**
     * 发出PUT请求(参数为json形式的字符串)
     * 
     * @param url
     *            请求的地址
     * @param jsonParams
     *            请求的参数
     * @return 响应的字符串
     */
    String putByJsonParams(String url, String jsonParams) throws IOException;

    /**
     * 发出PUT请求(参数为json形式的字符串)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数(一个Bean或Map&lt;String,Object&gt;)
     * @return 响应的字符串
     */
    String putByJsonParams(String url, Object requestParams) throws IOException;

    /**
     * 发出DELETE请求
     * 
     * @param url
     *            请求的地址
     * @return 响应的字符串
     */
    String delete(String url) throws IOException;

    /**
     * 发出DELETE请求(将Map对象转为请求的FORM参数)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数
     * @return 响应的字符串
     */
    String deleteByFormParams(String url, Map<String, Object> requestParams) throws IOException;

}
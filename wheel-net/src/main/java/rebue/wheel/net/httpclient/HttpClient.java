package rebue.wheel.net.httpclient;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;

import com.fasterxml.jackson.core.type.TypeReference;

import lombok.SneakyThrows;
import rebue.wheel.core.MapUtils;
import rebue.wheel.core.util.OrikaUtils;
import rebue.wheel.serialization.jackson.JacksonUtils;
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
        final String requestParamsStr = MapUtils.map2UrlParams(requestParams);
        String       fullUrl;
        if (url.contains("%s")) {
            fullUrl = String.format(url, requestParamsStr);
        }
        else {
            final StringBuilder sb = new StringBuilder(url);
            if (StringUtils.isBlank(requestParamsStr)) {
                fullUrl = url;
            }
            else {
                if (url.contains("?")) {
                    sb.append("&");
                }
                else {
                    sb.append("?");
                }
                sb.append(requestParamsStr);
                fullUrl = sb.substring(0, sb.length() - 1);
            }
        }
        return get(fullUrl);
    }

    /**
     * 发出带参数的GET请求
     *
     * @param url           请求的地址
     * @param requestParams 请求的参数
     *
     * @return 响应的字符串
     */
    default String get(final String url, final Object requestParams) throws IOException {
        return get(url, OrikaUtils.mapToMap(requestParams));
    }

    /**
     * 发出带参数的GET请求，并将JSON格式的响应转成对象
     * 
     * @param url           请求的地址
     * @param requestParams 请求的参数
     * @param valueTypeRef  要转换对象的泛型引用
     * @param <T>           要转换对象的泛型
     * 
     */
    @SneakyThrows
    default <T> T getWithJsonResponse(final String url, final Map<String, Object> requestParams, final TypeReference<T> valueTypeRef) {
        String resp = get(url, requestParams);
        return JacksonUtils.deserialize(resp, valueTypeRef);
    }

    /**
     * 发出带参数的GET请求，并将JSON格式的响应转成对象
     *
     * @param url           请求的地址
     * @param requestParams 请求的参数
     * @param valueTypeRef  要转换对象的泛型引用
     * @param <T>           要转换对象的泛型
     *
     */
    @SneakyThrows
    default <T> T getWithJsonResponse(final String url, final Map<String, Object> requestParams, final Class<T> clazz) {
        String resp = get(url, requestParams);
        return JacksonUtils.deserialize(resp, clazz);
    }

    /**
     * 
     * 发出带参数的GET请求，并将JSON格式的响应转成对象(主要解决微信乱码的问题)
     *
     * @param url           请求的地址
     * @param requestParams 请求的参数
     * @param valueTypeRef  要转换对象的泛型引用
     * @param <T>           要转换对象的泛型
     * @param encoding      转码
     */
    @SneakyThrows
    default <T> T getWithJsonResponse(final String url, final Map<String, Object> requestParams, final Class<T> clazz, final String encoding) {
        String resp = get(url, requestParams);
        // 转码
        resp = new String(resp.getBytes(encoding), "UTF-8");
        return JacksonUtils.deserialize(resp, clazz);
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
package rebue.wheel;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Consts;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * HttpClient工具库
 * 
 */
public class HttpClientUtils {
    private final static Logger _log           = LoggerFactory.getLogger(HttpClientUtils.class);

    private static ObjectMapper _objejctMapper = new ObjectMapper();

    /**
     * 发出GET请求
     * 
     * @param url
     *            请求的地址
     */
    public static String get(String url) throws IOException {
        _log.info("准备发出只有URL的GET请求: {}", url);
        try {
            String result = Request.Get(url).execute().returnContent().asString();
            _log.info("接收到response的信息: {}", result);
            return result;
        } catch (IOException e) {
            _log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发出带参数的GET请求
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数
     * @return 响应的字符串
     */
    public static String get(String url, Map<String, Object> requestParams) throws IOException {
        _log.info("准备发出带参数的GET请求: {} {}", url, requestParams);
        Form form = Form.form();
        for (Entry<String, Object> requestParam : requestParams.entrySet()) {
            form.add(requestParam.getKey(), requestParam.getValue().toString());
        }
        try {
            String result = Request.Get(url).bodyForm(form.build()).execute().returnContent().asString();
            _log.info("接收到response的信息: {}", result);
            return result;
        } catch (IOException e) {
            _log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }

    }

    /**
     * 发出POST请求
     * 
     * @param url
     *            请求的地址
     * @return 响应的字符串
     */
    public static String post(String url) throws IOException {
        _log.info("准备发出只有URL的POST请求: {}", url);
        try {
            String result = Request.Post(url).execute().returnContent().asString();
            _log.info("接收到response的信息: {}", result);
            return result;
        } catch (IOException e) {
            _log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发出POST请求(将Map对象转为请求的FORM参数)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数
     * @return 响应的字符串
     */
    public static String postByFormParams(String url, Map<String, Object> requestParams) throws IOException {
        _log.info("准备发出带FORM参数的POST请求: {}", url);
        Form form = Form.form();
        for (Entry<String, Object> requestParam : requestParams.entrySet()) {
            form.add(requestParam.getKey(), requestParam.getValue().toString());
        }
        try {
            String result = Request.Post(url).bodyForm(form.build()).execute().returnContent().asString();
            _log.info("接收到response的信息: {}", result);
            return result;
        } catch (IOException e) {
            _log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发出POST请求(参数为json形式的字符串)
     * 
     * @param url
     *            请求的地址
     * @param jsonParams
     *            请求的参数
     * @return 响应的字符串
     */
    public static String postByJsonParams(String url, String jsonParams) throws IOException {
        _log.info("准备发出带json参数的POST请求: {} {}", url, jsonParams);
        try {
            String result = Request.Post(url).bodyString(jsonParams, ContentType.APPLICATION_JSON).execute().returnContent().asString();
            _log.info("接收到response的信息: {}", result);
            return result;
        } catch (IOException e) {
            _log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发出POST请求(参数为json形式的字符串)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数(一个Bean或Map<String,Object>)
     * @return 响应的字符串
     */
    public static String postByJsonParams(String url, Object requestParams) throws IOException {
        return postByJsonParams(url, _objejctMapper.writeValueAsString(requestParams));
    }

    /**
     * 发出POST请求(将Map对象转为请求的XML参数)
     * 
     * @param url
     *            请求的地址
     * @param xmlParams
     *            请求的参数
     * @return 响应的字符串
     */
    public static Map<String, Object> postByXmlParams(String url, String xmlParams) throws IOException, DocumentException {
        _log.info("准备发出带XML参数的POST请求: {} {}", url, xmlParams);
        try {
            String result = Request.Post(url).bodyString(xmlParams, ContentType.create("text/xml", Consts.UTF_8)).execute().returnContent().asString();
            _log.info("接收到response的信息: {}", result);
            return XmlUtils.xmlToMap(result);
        } catch (IOException e) {
            _log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发出POST请求(将Map对象转为请求的XML参数)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数
     * @return 响应的字符串
     */
    public static Map<String, Object> postByXmlParams(String url, Map<String, Object> requestParams) throws IOException, DocumentException {
        return postByXmlParams(url, XmlUtils.mapToXml(requestParams));
    }

    /**
     * 发出PUT请求
     * 
     * @param url
     *            请求的地址
     * @return 响应的字符串
     */
    public static String put(String url) throws IOException {
        _log.info("准备发出只有URL的PUT请求: {}", url);
        try {
            String result = Request.Put(url).execute().returnContent().asString();
            _log.info("接收到response的信息: {}", result);
            return result;
        } catch (IOException e) {
            _log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发出PUT请求(将Map对象转为请求的FORM参数)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数
     * @return 响应的字符串
     */
    public static String putByFormParams(String url, Map<String, Object> requestParams) throws IOException {
        _log.info("准备发出带FORM参数的PUT请求: {}", url);
        Form form = Form.form();
        for (Entry<String, Object> requestParam : requestParams.entrySet()) {
            form.add(requestParam.getKey(), requestParam.getValue().toString());
        }
        try {
            String result = Request.Put(url).bodyForm(form.build()).execute().returnContent().asString();
            _log.info("接收到response的信息: {}", result);
            return result;
        } catch (IOException e) {
            _log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }

    }

    /**
     * 发出PUT请求(参数为json形式的字符串)
     * 
     * @param url
     *            请求的地址
     * @param jsonParams
     *            请求的参数
     * @return 响应的字符串
     */
    public static String putByJsonParams(String url, String jsonParams) throws IOException {
        _log.info("准备发出带json参数的PUT请求: {} {}", url, jsonParams);
        try {
            String result = Request.Put(url).bodyString(jsonParams, ContentType.APPLICATION_JSON).execute().returnContent().asString();
            _log.info("接收到response的信息: {}", result);
            return result;
        } catch (IOException e) {
            _log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发出PUT请求(参数为json形式的字符串)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数(一个Bean或Map<String,Object>)
     * @return 响应的字符串
     */
    public static String putByJsonParams(String url, Object requestParams) throws IOException {
        return putByJsonParams(url, _objejctMapper.writeValueAsString(requestParams));
    }

    /**
     * 发出DELETE请求
     * 
     * @param url
     *            请求的地址
     * @return 响应的字符串
     */
    public static String delete(String url) throws IOException {
        _log.info("准备发出只有URL的DELETE请求: {}", url);
        try {
            String result = Request.Delete(url).execute().returnContent().asString();
            _log.info("接收到response的信息: {}", result);
            return result;
        } catch (IOException e) {
            _log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 发出DELETE请求(将Map对象转为请求的FORM参数)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数
     * @return 响应的字符串
     */
    public static String deleteByFormParams(String url, Map<String, Object> requestParams) throws IOException {
        _log.info("准备发出带FORM参数的DELETE请求: {}", url);
        Form form = Form.form();
        for (Entry<String, Object> requestParam : requestParams.entrySet()) {
            form.add(requestParam.getKey(), requestParam.getValue().toString());
        }
        try {
            String result = Request.Delete(url).bodyForm(form.build()).execute().returnContent().asString();
            _log.info("接收到response的信息: {}", result);
            return result;
        } catch (IOException e) {
            _log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

}
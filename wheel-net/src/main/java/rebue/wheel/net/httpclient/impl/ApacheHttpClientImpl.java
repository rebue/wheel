package rebue.wheel.net.httpclient.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.dom4j.DocumentException;
import rebue.wheel.net.httpclient.HttpClient;
import rebue.wheel.serialization.jackson.JacksonUtils;
import rebue.wheel.serialization.xml.XmlUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <pre>
 *
 * Apache的HttpClient工具库
 * 要使用此类，请在pom.xml的依赖中添加apache的GAV
 *
 * &lt;dependency&gt;
 * &lt;groupId&gt;org.apache.httpcomponents&lt;/groupId&gt;
 * &lt;artifactId&gt;fluent-hc&lt;/artifactId&gt;
 * &lt;/dependency&gt;
 * </pre>
 */
@Slf4j
public class ApacheHttpClientImpl implements HttpClient {
    private static ObjectMapper _objejctMapper = JacksonUtils.getObjectMapper();

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#get(java.lang.String)
     */
    @Override
    public String get(final String url) throws IOException {
        log.info("发送GET请求: {}", url);
        try {
            final String result = Request.Get(url).execute().returnContent().asString();
            log.info("接收到response的信息: {}", result);
            return result;
        } catch (final IOException e) {
            log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#post(java.lang.String)
     */
    @Override
    public String post(final String url) throws IOException {
        log.info("发送只有URL的POST请求: {}", url);
        try {
            final String result = Request.Post(url).execute().returnContent().asString();
            log.info("接收到response的信息: {}", result);
            return result;
        } catch (final IOException e) {
            log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#postByFormParams(java.lang.String, java.util.Map)
     */
    @Override
    public String postByFormParams(final String url, final Map<String, Object> requestParams) throws IOException {
        log.info("发送带FORM参数的POST请求: {}", url);
        final Form form = Form.form();
        for (final Entry<String, Object> requestParam : requestParams.entrySet()) {
            form.add(requestParam.getKey(), requestParam.getValue().toString());
        }
        try {
            final String result = Request.Post(url).bodyForm(form.build()).execute().returnContent().asString();
            log.info("接收到response的信息: {}", result);
            return result;
        } catch (final IOException e) {
            log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#postByJsonParams(java.lang.String, java.lang.String)
     */
    @Override
    public String postByJsonParams(final String url, final String jsonParams) throws IOException {
        log.info("发送带JSON_BODY的POST请求: {} {}", url, jsonParams);
        try {
            final String result = Request.Post(url).bodyString(jsonParams, ContentType.APPLICATION_JSON).execute().returnContent().asString();
            log.info("接收到response的信息: {}", result);
            return result;
        } catch (final IOException e) {
            log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#postByJsonParams(java.lang.String, java.lang.Object)
     */
    @Override
    public String postByJsonParams(final String url, final Object requestParams) throws IOException {
        return postByJsonParams(url, _objejctMapper.writeValueAsString(requestParams));
    }

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#postByXmlParams(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, Object> postByXmlParams(final String url, final String xmlParams) throws IOException, DocumentException {
        log.info("发送带XML_BODY的POST请求: {} {}", url, xmlParams);
        try {
            final String result = Request.Post(url).bodyString(xmlParams, ContentType.create("text/xml", Consts.UTF_8)).execute().returnContent().asString();
            log.info("接收到response的信息: {}", result);
            return XmlUtils.xmlToMap(result);
        } catch (final IOException e) {
            log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#put(java.lang.String)
     */
    @Override
    public String put(final String url) throws IOException {
        log.info("发送只有URL的PUT请求: {}", url);
        try {
            final String result = Request.Put(url).execute().returnContent().asString();
            log.info("接收到response的信息: {}", result);
            return result;
        } catch (final IOException e) {
            log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#putByFormParams(java.lang.String, java.util.Map)
     */
    @Override
    public String putByFormParams(final String url, final Map<String, Object> requestParams) throws IOException {
        log.info("发送带FORM_BODY的PUT请求: {}", url);
        final Form form = Form.form();
        for (final Entry<String, Object> requestParam : requestParams.entrySet()) {
            form.add(requestParam.getKey(), requestParam.getValue().toString());
        }
        try {
            final String result = Request.Put(url).bodyForm(form.build()).execute().returnContent().asString();
            log.info("接收到response的信息: {}", result);
            return result;
        } catch (final IOException e) {
            log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#putByJsonParams(java.lang.String, java.lang.String)
     */
    @Override
    public String putByJsonParams(final String url, final String jsonParams) throws IOException {
        log.info("发送带JSON_BODY的PUT请求: {} {}", url, jsonParams);
        try {
            final String result = Request.Put(url).bodyString(jsonParams, ContentType.APPLICATION_JSON).execute().returnContent().asString();
            log.info("接收到response的信息: {}", result);
            return result;
        } catch (final IOException e) {
            log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#putByJsonParams(java.lang.String, java.lang.Object)
     */
    @Override
    public String putByJsonParams(final String url, final Object requestParams) throws IOException {
        return putByJsonParams(url, _objejctMapper.writeValueAsString(requestParams));
    }

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#delete(java.lang.String)
     */
    @Override
    public String delete(final String url) throws IOException {
        log.info("发送只有URL的DELETE请求: {}", url);
        try {
            final String result = Request.Delete(url).execute().returnContent().asString();
            log.info("接收到response的信息: {}", result);
            return result;
        } catch (final IOException e) {
            log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see rebue.wheel.http.impl.HttpClient#deleteByFormParams(java.lang.String, java.util.Map)
     */
    @Override
    public String deleteByFormParams(final String url, final Map<String, Object> requestParams) throws IOException {
        log.info("准备发出带FORM参数的DELETE请求: {}", url);
        final Form form = Form.form();
        for (final Entry<String, Object> requestParam : requestParams.entrySet()) {
            form.add(requestParam.getKey(), requestParam.getValue().toString());
        }
        try {
            final String result = Request.Delete(url).bodyForm(form.build()).execute().returnContent().asString();
            log.info("接收到response的信息: {}", result);
            return result;
        } catch (final IOException e) {
            log.error("HTTP请求出现异常:" + e.getMessage(), e);
            throw e;
        }
    }

}
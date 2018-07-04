package rebue.wheel;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkhttpUtils {
    private final static Logger _log = LoggerFactory.getLogger(OkhttpUtils.class);

//    private static OkHttpClient _client = new OkHttpClient();
    private static OkHttpClient _client        = new OkHttpClient().newBuilder().hostnameVerifier(new HostnameVerifier() {
                                                   @Override
                                                   public boolean verify(String hostname, SSLSession session) {
                                                       _log.debug("强行返回true 即验证成功");
                                                       return true;
                                                   }
                                               }).build();

    private static ObjectMapper _objejctMapper = new ObjectMapper();

    /**
     * 设置读数据的超时时间（目前专门给微信沙箱测试用）
     * 
     * @param minutes
     *            超时时间(分钟)
     */
    public static void setReadTimeout(int minutes) {
        _client = new OkHttpClient.Builder().readTimeout(minutes, TimeUnit.MINUTES).build();
    }

    /**
     * 发出GET请求
     * 
     * @param url
     *            请求的地址
     */
    public static String get(String url) throws IOException {
        _log.debug("发送请求：{}", url);
        Request request = new Request.Builder().url(url).build();
        Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            String msg = response.body().string();
            _log.info("接收到response的信息：{}", msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
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
        _log.debug("发送请求：{}", url);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, Object> item : requestParams.entrySet()) {
            urlBuilder.addQueryParameter(item.getKey(), URLEncoder.encode(item.getValue().toString(), "utf-8"));
        }
        Request request = new Request.Builder().url(urlBuilder.build()).build();
        Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            String msg = response.body().string();
            _log.debug(msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
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
        _log.debug("发送请求：{}", url);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Request request = new Request.Builder().url(url).post(formBodyBuilder.build()).build();
        Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            String msg = response.body().string();
            _log.debug(msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
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
        _log.debug("发送请求：{}: {}", url, jsonParams);
        Request request = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonParams)).build();
        Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            String msg = response.body().string();
            _log.debug("接收到response的信息：{}", msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
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
     * 发出POST请求(将Map对象转为请求的FORM参数)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数
     * @return 响应的字符串
     */
    public static String postByFormParams(String url, Map<String, Object> requestParams) throws IOException {
        _log.debug("发送请求：{}", url);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, Object> item : requestParams.entrySet()) {
            formBodyBuilder.add(item.getKey(), item.getValue().toString());
        }
        Request request = new Request.Builder().url(url).post(formBodyBuilder.build()).build();
        Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            String msg = response.body().string();
            _log.debug(msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
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
    public static Map<String, Object> postByXmlParams(String url, Map<String, Object> requestParams) throws IOException, DocumentException, SAXException {
        _log.debug("发送请求：{}", url);
        Request request = new Request.Builder().url(url).post(RequestBody.create(MediaType.parse("text/xml; charset=utf-8"), XmlUtils.mapToXml(requestParams))).build();
        Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            return XmlUtils.xmlToMap(response.body().byteStream());
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
        }
    }

    /**
     * 发出PUT请求
     * 
     * @param url
     *            请求的地址
     * @return 响应的字符串
     */
    public static String put(String url) throws IOException {
        _log.debug("发送请求：{}", url);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Request request = new Request.Builder().url(url).put(formBodyBuilder.build()).build();
        Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            String msg = response.body().string();
            _log.debug(msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
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
        _log.debug("发送请求：{}", url);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, Object> item : requestParams.entrySet()) {
            formBodyBuilder.add(item.getKey(), item.getValue().toString());
        }
        Request request = new Request.Builder().url(url).put(formBodyBuilder.build()).build();
        Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            String msg = response.body().string();
            _log.debug(msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
        }
    }

    /**
     * 发出DELETE请求
     * 
     * @param url
     *            请求的地址
     * @return 响应的字符串
     */
    public static String delete(String url) throws IOException {
        _log.debug("发送请求：{}", url);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        Request request = new Request.Builder().url(url).delete(formBodyBuilder.build()).build();
        Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            String msg = response.body().string();
            _log.debug(msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
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
        _log.debug("发送请求：{}", url);
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (Map.Entry<String, Object> item : requestParams.entrySet()) {
            formBodyBuilder.add(item.getKey(), item.getValue().toString());
        }
        Request request = new Request.Builder().url(url).delete(formBodyBuilder.build()).build();
        Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            String msg = response.body().string();
            _log.debug(msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
        }
    }
}

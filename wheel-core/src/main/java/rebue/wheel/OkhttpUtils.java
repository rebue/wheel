package rebue.wheel;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private final static Logger    _log            = LoggerFactory.getLogger(OkhttpUtils.class);

    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final MediaType XML_MEDIA_TYPE  = MediaType.get("text/xml; charset=utf-8");

//    private static OkHttpClient _client = new OkHttpClient();
    private static OkHttpClient _client        = new OkHttpClient().newBuilder().hostnameVerifier((hostname, session) -> {
                                                   _log.debug("强行返回true 即验证成功");
                                                   return true;
                                               }).build();

    private static ObjectMapper _objejctMapper = new ObjectMapper();

    /**
     * 设置读数据的超时时间（目前专门给微信沙箱测试用）
     * 
     * @param minutes
     *            超时时间(分钟)
     */
    public static void setReadTimeout(final int minutes) {
        _client = new OkHttpClient.Builder().readTimeout(minutes, TimeUnit.MINUTES).build();
    }

    /**
     * 发出GET请求
     * 
     * @param url
     *            请求的地址
     */
    public static String get(final String url) throws IOException {
        _log.debug("发送请求：{}", url);
        final Request request = new Request.Builder().url(url).build();
        final Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            final String msg = response.body().string();
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
    public static String get(final String url, final Map<String, Object> requestParams) throws IOException {
        _log.debug("发送请求：{}", url);
        final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (final Map.Entry<String, Object> item : requestParams.entrySet()) {
            String value = null;
            if (item.getValue() instanceof Date) {
                if (item.getKey().endsWith("Date")) {
                    value = sdfDate.format((Date) item.getValue());
                } else if (item.getKey().endsWith("Time")) {
                    value = sdfTime.format((Date) item.getValue());
                }
            } else {
                value = item.getValue().toString();
            }
            urlBuilder.addQueryParameter(item.getKey(), URLEncoder.encode(value, "utf-8"));
//			urlBuilder.addQueryParameter(item.getKey(), value);
        }
        final Request request = new Request.Builder().url(urlBuilder.build()).build();
        final Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            final String msg = response.body().string();
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
    public static String post(final String url) throws IOException {
        _log.debug("发送请求：{}", url);
        final FormBody.Builder formBodyBuilder = new FormBody.Builder();
        final Request request = new Request.Builder().url(url).post(formBodyBuilder.build()).build();
        final Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            final String msg = response.body().string();
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
    public static String postByJsonParams(final String url, final String jsonParams) throws IOException {
        _log.debug("发送请求：{}: {}", url, jsonParams);
        final Request request = new Request.Builder().url(url).post(RequestBody.Companion.create(jsonParams, JSON_MEDIA_TYPE)).build();
        final Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            final String msg = response.body().string();
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
     *            请求的参数(一个Bean或Map&lt;String,Object$gt;)
     * @return 响应的字符串
     */
    public static String postByJsonParams(final String url, final Object requestParams) throws IOException {
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
    public static String postByFormParams(final String url, final Map<String, Object> requestParams) throws IOException {
        _log.debug("发送请求：{}", url);
        final FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (final Map.Entry<String, Object> item : requestParams.entrySet()) {
            formBodyBuilder.add(item.getKey(), item.getValue().toString());
        }
        final Request request = new Request.Builder().url(url).post(formBodyBuilder.build()).build();
        final Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            final String msg = response.body().string();
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
    public static Map<String, Object> postByXmlParams(final String url, final Map<String, Object> requestParams) throws IOException, DocumentException, SAXException {
        _log.debug("发送请求：{}", url);
        final Request request = new Request.Builder().url(url).post(RequestBody.Companion.create(XmlUtils.mapToXml(requestParams), XML_MEDIA_TYPE)).build();
        final Response response = _client.newCall(request).execute();
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
    public static String put(final String url) throws IOException {
        _log.debug("发送请求：{}", url);
        final FormBody.Builder formBodyBuilder = new FormBody.Builder();
        final Request request = new Request.Builder().url(url).put(formBodyBuilder.build()).build();
        final Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            final String msg = response.body().string();
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
    public static String putByFormParams(final String url, final Map<String, Object> requestParams) throws IOException {
        _log.debug("发送请求：{}", url);
        final FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (final Map.Entry<String, Object> item : requestParams.entrySet()) {
            formBodyBuilder.add(item.getKey(), item.getValue().toString());
        }
        final Request request = new Request.Builder().url(url).put(formBodyBuilder.build()).build();
        final Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            final String msg = response.body().string();
            _log.debug(msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
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
    public static String putByJsonParams(final String url, final String jsonParams) throws IOException {
        _log.debug("发送请求：{}: {}", url, jsonParams);
        final Request request = new Request.Builder().url(url).put(RequestBody.Companion.create(jsonParams, JSON_MEDIA_TYPE)).build();
        final Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            final String msg = response.body().string();
            _log.debug("接收到response的信息：{}", msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
        }
    }

    /**
     * 发出PUT请求(参数为json形式的字符串)
     * 
     * @param url
     *            请求的地址
     * @param requestParams
     *            请求的参数(一个Bean或Map&lt;String,Object&gt;)
     * @return 响应的字符串
     */
    public static String putByJsonParams(final String url, final Object requestParams) throws IOException {
        return putByJsonParams(url, _objejctMapper.writeValueAsString(requestParams));
    }

    /**
     * 发出DELETE请求
     * 
     * @param url
     *            请求的地址
     * @return 响应的字符串
     */
    public static String delete(final String url) throws IOException {
        _log.debug("发送请求：{}", url);
        final FormBody.Builder formBodyBuilder = new FormBody.Builder();
        final Request request = new Request.Builder().url(url).delete(formBodyBuilder.build()).build();
        final Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            final String msg = response.body().string();
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
    public static String deleteByFormParams(final String url, final Map<String, Object> requestParams) throws IOException {
        _log.debug("发送请求：{}", url);
        final FormBody.Builder formBodyBuilder = new FormBody.Builder();
        for (final Map.Entry<String, Object> item : requestParams.entrySet()) {
            formBodyBuilder.add(item.getKey(), item.getValue().toString());
        }
        final Request request = new Request.Builder().url(url).delete(formBodyBuilder.build()).build();
        final Response response = _client.newCall(request).execute();
        if (response.isSuccessful()) {
            final String msg = response.body().string();
            _log.debug(msg);
            return msg;
        } else {
            _log.error("服务器返回错误: " + response);
            throw new HttpClientErrorException(HttpStatus.valueOf(response.code()));
        }
    }
}

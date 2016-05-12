package com.nit.weixi.study_c_system.tools;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RestClient {

    public static final String BASE_URL = "http://127.0.0.1:8090";
    public static int HTTP_Timeout = 12 * 1000;
    public static Context context;

    public static AsyncHttpClient client;

    public static AsyncHttpClient getHttpClient() {
        if (client == null) {
            client = new AsyncHttpClient();
        }
        return client;
    }

    /**
     * @param context Activity or Application context
     */
    public static void init(Context context) {
        RestClient.context = context;
        client = getHttpClient();
    }


    /**
     * get method
     *
     * @param url             相对的url
     * @param params  请求参数
     * @param responseHandler 响应事件
     */
    public static void get(String url, RequestParams params,
                           AsyncHttpResponseHandler responseHandler) {
        get(BASE_URL, url, params, responseHandler);
    }

    public static void get(String baseUrl, String url, RequestParams params,
                             AsyncHttpResponseHandler responseHandler) {
        initClient();
        client.get(baseUrl + url, params, responseHandler);
    }

    /**
     * post method
     *
     * @param url url
     * @param params 参数
     * @param responseHandler 响应事件
     */
    public static void post(String url, RequestParams params,
                            AsyncHttpResponseHandler responseHandler) {
        initClient();
        client.post(getAbsoluteUrl(url), params, responseHandler);

    }

    /**
     * 请求前初始化<br>
     * 必须在请求之前初始化
     */
    private static void initClient() {
        if (context != null)
        client.setTimeout(HTTP_Timeout);
        client.setEnableRedirects(true);
    }

    /**
     * 获得绝对url
     * @param relativeUrl 相对路径url
     * @return 绝对路径
     */
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}

package com.yubin.simpleapp.net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yubin.simpleapp.base.BaseApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpRequestUtils {
    private static OkHttpClient okHttpClient;

    //定义一个信任所有证书的TrustManager
    static final X509TrustManager trustAllCert = new X509TrustManager() {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }
    };

    //设置OkHttpClient
    private static OkHttpClient getOkHttp() {
        if (okHttpClient == null) {
            OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
            okhttpBuilder.connectTimeout(30, TimeUnit.SECONDS);
            okhttpBuilder.writeTimeout(50, TimeUnit.SECONDS);
            okhttpBuilder.readTimeout(50, TimeUnit.SECONDS);
            okHttpClient = okhttpBuilder.sslSocketFactory(new SSL(trustAllCert), trustAllCert).build();
        }
        return okHttpClient;
    }

    private static boolean isNewWork(String url) {

        boolean b = NetUtils.isConnect(BaseApplication.getAppContext());
        if (!b) {
            Toast.makeText(BaseApplication.getAppContext(),"当前没有网络，请先检查网络环境", Toast.LENGTH_SHORT);
        }
        return b;
    }

    /**
     * get接口用这个
     *
     * @param url 接口
     * @param map 参数
     */
    public static void getRequestAsyn(String url, Map<String, String> map, final NetworkCallback callback) {
        Log.d("HttpRequestUtils","getRequestAsyn url = " +url);
        getRequest(url, map, callback);
    }

    private static void getRequest(String url, Map<String, String> map, final NetworkCallback callback) {
        okHttpClient = getOkHttp();
        if (url == null || "".equals(url)) {
            sendFailResult(callback, 404, "URL无效");
            return;
        }
        if (!isNewWork(url)) {
            sendFailResult(callback, 404, "当前没有网络，请先检查网络环境");
            return;
        }

        Request.Builder builder = new Request.Builder();
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null)
                    builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        Request request = builder.url(url)
                .get()
                .build();

        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                sendFailResult(callback, 404, "网络请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                dealRequestResponse(response, callback);
            }
        });
    }

    public static void postRequestAsyn(String url, String token, Map<String, Object> map, final NetworkCallback callback) {
        postRequest(url, token, map, callback);
    }

    private static void postRequest(String url, String token, Map<String, Object> map, final NetworkCallback callback) {
        okHttpClient = getOkHttp();
        if (url == null || "".equals(url)) {
            sendFailResult(callback, 404, "URL无效");
            return;
        }
        if (!isNewWork(url)) {
            sendFailResult(callback, 404, "当前没有网络，请先检查网络环境");
            return;
        }
        Request.Builder builder = new Request.Builder();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Gson gson = new Gson();
        RequestBody body = RequestBody.create(mediaType, gson.toJson(map));

        Request request = builder.url(url)
                .post(body)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                sendFailResult(callback, 404, "网络请求失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                dealRequestResponse(response, callback);
            }
        });
    }


    private static void dealRequestResponse(Response response, final NetworkCallback callback) throws IOException {
        LoadDialog.dismiss();
        if (response != null) {
            int code = response.code();
            if (code == 200) {
                String result = response.body().string();
                try {
                    JSONObject resultObj = new JSONObject(result);
                    String     error     = resultObj.getString("code");
                    if ("0".equals(error)) {
                        sendSuccessResult(callback, result);
                    } else {
                        sendFailResult(callback, Integer.parseInt(error), resultObj.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendFailResult(callback, code, response.message());
                }

            } else {
                sendFailResult(callback, code, response.message());
            }
            response.close();
        } else {
            sendFailResult(callback, 0, "");
        }
    }

    private static void sendSuccessResult(final NetworkCallback callback, final String response) {
        LoadDialog.dismiss();
        if (callback != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    callback.completed(response);
                }
            });
        }
    }



    private static <T> void sendFailResult(final NetworkCallback callback, final int errorCode, final String errorString) {
        LoadDialog.dismiss();
        if (callback != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    callback.failed(errorCode, errorString);
                }
            });
        }
    }
}

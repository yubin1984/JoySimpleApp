package com.yubin.simpleapp.DownloadService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface DownloadServiceApi {
    @Streaming
    @GET
    Call<ResponseBody> download(@Url String url);
}

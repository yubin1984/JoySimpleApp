package com.yubin.simpleapp.DownloadService;

/*
 *@filename DownloadListener
 *@author ch
 *@date 2020/4/28
 */
public interface DownloadListener {
    void onStart();//下载开始

    void onProgress(int progress);//下载进度

    void onFinish(String path);//下载完成

    void onFail(String errorInfo);//下载失败
}

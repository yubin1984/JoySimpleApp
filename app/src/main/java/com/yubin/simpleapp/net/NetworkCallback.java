package com.yubin.simpleapp.net;

public abstract class NetworkCallback {
    public abstract void completed(String response);

    /**
     * @param httpStatusCode
     * @param error
     */
    public abstract void failed(int httpStatusCode, String error);
}

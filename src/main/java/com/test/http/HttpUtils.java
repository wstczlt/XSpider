package com.test.http;

import java.util.concurrent.TimeUnit;

import com.test.tools.RetryOnceInterceptor;

import okhttp3.OkHttpClient;

public class HttpUtils {
  // 超时时间
  private static final long DEFAULT_TIMEOUT_MILLS = 5000L;

  public static OkHttpClient newHttpClient() {
    return newHttpClient(false);
  }

  public static OkHttpClient newHttpClient(boolean autoProxy) {
    return newClientBuilder().proxySelector(new HttpProxy(autoProxy)).build();
  }

  private static OkHttpClient.Builder newClientBuilder() {
    return new OkHttpClient.Builder()
        // .addInterceptor(new CurlInterceptor(Logger.SYSTEM))
        .addInterceptor(new RetryOnceInterceptor())
        .retryOnConnectionFailure(true)
        .connectTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
        .readTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
        .writeTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);
  }
}

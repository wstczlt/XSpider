package com.test.http;

import java.util.concurrent.TimeUnit;

import com.test.tools.RetryOnceInterceptor;

import okhttp3.OkHttpClient;

public class HttpUtils {
  // 超时时间
  private static final long DEFAULT_TIMEOUT_MILLS = 2000L;

  public static OkHttpClient buildHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder()
        // .addInterceptor(new CurlInterceptor(Logger.SYSTEM))
        .addInterceptor(new RetryOnceInterceptor())
        .proxySelector(new HttpProxy())
        .retryOnConnectionFailure(true)
        .connectTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
        .readTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS)
        .writeTimeout(DEFAULT_TIMEOUT_MILLS, TimeUnit.MILLISECONDS);


    return builder.build();
  }
}

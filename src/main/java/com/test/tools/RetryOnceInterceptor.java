package com.test.tools;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryOnceInterceptor implements Interceptor {

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    int retryCount = 0;
    while (true) {
      try {
        return chain.proceed(request);
      } catch (SocketTimeoutException | ConnectException | ProtocolException e) {
        if (retryCount <= 1) {
          retryCount++;
          continue;
        }
        throw e;
      }
    }
  }
}

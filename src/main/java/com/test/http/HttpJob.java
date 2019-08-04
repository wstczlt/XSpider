package com.test.http;

import java.util.Map;

import com.test.Config;
import com.test.Keys;

import okhttp3.Request;

public abstract class HttpJob implements Keys {

  public final int mMatchID;

  public HttpJob(int matchID) {
    mMatchID = matchID;
  }

  public abstract Request.Builder newRequestBuilder();

  public abstract void onResponse(String text, Map<String, String> items) throws Exception;

  public boolean needRetry() {
    return true;
  }

  public void onFailed(Throwable t) {
    Config.LOGGER.log(String.format("[%s], Failed: %s", getClass().getSimpleName(), t.getMessage()));
  }
}

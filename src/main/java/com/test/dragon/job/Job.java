package com.test.dragon.job;

import java.util.Map;

import okhttp3.Request;

public abstract class Job {

  public static final String KEY_SKIP = "isSkip";

  public final int mMatchID;

  public Job(int matchID) {
    mMatchID = matchID;
  }

  public abstract Request.Builder newRequestBuilder();

  public abstract void handleResponse(String text,  Map<String, String> items);
}

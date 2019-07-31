package com.test.dragon.job;

import com.test.dragon.tools.Keys;

import java.util.Map;

import okhttp3.Request;

public abstract class Job implements Keys {

  public final int mMatchID;

  public Job(int matchID) {
    mMatchID = matchID;
  }

  public abstract Request.Builder newRequestBuilder();

  public abstract void handleResponse(String text,  Map<String, String> items);
}

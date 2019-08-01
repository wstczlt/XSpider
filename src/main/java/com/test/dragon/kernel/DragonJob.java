package com.test.dragon.kernel;

import java.util.Map;

import com.test.dragon.tools.Keys;
import com.test.tools.Logger;

import okhttp3.Request;

public abstract class DragonJob implements Keys {

  public final int mMatchID;
  public final Logger mLogger;

  public DragonJob(int matchID, Logger logger) {
    mMatchID = matchID;
    mLogger = logger;
  }

  public abstract Request.Builder newRequestBuilder();

  public abstract void handleResponse(String text, Map<String, String> items) throws Exception;
}

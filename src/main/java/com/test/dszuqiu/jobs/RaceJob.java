package com.test.dszuqiu.jobs;

import static com.test.tools.Utils.setSkip;

import java.util.Map;

import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.Config;
import com.test.http.HttpJob;

import okhttp3.Request;

public class RaceJob extends HttpJob {

  private static final String REQUEST_URL = "http://api.dszuqiu.com/v9/race/view?token=&race_id=%d";

  public RaceJob(int matchID) {
    super(matchID);
  }

  @Override
  public Request.Builder newRequestBuilder() {
    String requestUrl = String.format(REQUEST_URL, mMatchID);
    System.out.println(requestUrl);
    return new Request.Builder()
        .header("User-Agent", "Android 6.0.1/CLT-AL00/9")
        .url(requestUrl);
  }

  @Override
  public void onResponse(String text, Map<String, String> items) throws Exception {
    String error;
    JSONObject json = JSON.parseObject(text);
    if (json == null) {
      error = "Json Null";
    } else {
      error = json.getString("error");
    }
    if (!TextUtils.isEmpty(error)) {
      setSkip(items);
      Config.LOGGER.log(
          String.format("[%s], matchID=%d, Error=%s", getClass().getSimpleName(), mMatchID, error));
      return;
    }

    items.put(MATCH_ID, "" + mMatchID);
    items.put(RAW_TEXT, text);
  }
}

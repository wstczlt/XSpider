package com.test.dragon.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.test.tools.Utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RuntimeSupplier implements Supplier<List<Integer>> {

  private final OkHttpClient mClient;
  private List<Integer> mMatchIDs;

  public RuntimeSupplier(OkHttpClient client) {
    mClient = client;
  }

  @Override
  public List<Integer> get() {
    if (mMatchIDs != null) {
      return mMatchIDs;
    }
    try {
      mMatchIDs = collectRealTimeMatchIds();
      if (mMatchIDs == null) {
        // 再次尝试
        Thread.sleep(1000L);
        mMatchIDs = collectRealTimeMatchIds();
      }
    } catch (Exception e) {
      e.printStackTrace();
      return Collections.emptyList();
    }

    return mMatchIDs;
  }

  private List<Integer> collectRealTimeMatchIds() throws Exception {
    final String requestUrl = "http://score.nowscore.com/data/sbOddsData.js";
    List<Integer> matchIds = new ArrayList<>();
    Request request = new Request.Builder().url(requestUrl).build();
    Response response = mClient.newCall(request).execute();
    if (!response.isSuccessful() || response.body() == null) {
      return Collections.emptyList();
    }

    String html = response.body().string();
    // System.out.println(html);
    Pattern pattern = Pattern.compile("sData\\[\\d+]");
    Matcher matcher = pattern.matcher(html);
    while (matcher.find()) {
      String matchString = matcher.group();
      matchString = matchString.replace("sData[", "").replace("]", "");
      matchIds.add(Utils.valueOfInt(matchString));
    }
    return matchIds;
  }
}

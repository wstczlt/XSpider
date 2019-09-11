package com.test.win007;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.test.http.HttpJob;
import com.test.http.HttpJobBuilder;
import com.test.http.HttpUtils;
import com.test.tools.Utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Win007JobFactory {

  private final OkHttpClient mClient;
  private final HttpJobBuilder mBuilder;

  private List<List<HttpJob>> mJobs;
  private List<Integer> mMatchIDs;

  public Win007JobFactory(HttpJobBuilder builder) {
    mBuilder = builder;
    mClient = HttpUtils.newHttpClient();
  }

  public List<List<HttpJob>> build() throws Exception {
    if (mJobs != null) {
      return mJobs;
    }

    mJobs = new ArrayList<>();
    mMatchIDs = collectRealTimeMatchIds();
    for (int matchID : mMatchIDs) {
      mJobs.add(mBuilder.buildJobs(matchID));
    }

    return mJobs;
  }

  public List<Integer> getMatchIDs() {
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

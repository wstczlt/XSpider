package com.test.dszuqiu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.test.dszuqiu.parser.ListParser;
import com.test.http.HttpJob;
import com.test.http.HttpJobBuilder;
import com.test.http.HttpUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DsJobFactory {

  private static final String REQUEST_URL = "http://api.dszuqiu.com/v7/score?mt=0&token=";

  private final OkHttpClient mClient;
  private final HttpJobBuilder mBuilder;

  private List<List<HttpJob>> mJobs;
  private List<Integer> mMatchIDs;

  public DsJobFactory(HttpJobBuilder builder) {
    mBuilder = builder;
    mClient = HttpUtils.buildHttpClient();
  }

  public List<List<HttpJob>> build() throws Exception {
    if (mJobs != null) {
      return mJobs;
    }

    mJobs = new ArrayList<>();
    mMatchIDs = realtime();
    System.out.println("扫描到的比赛列表:" + mMatchIDs);
    // mMatchIDs = Collections.singletonList(647274);
    for (int matchID : mMatchIDs) {
      mJobs.add(mBuilder.buildJobs(matchID));
    }

    return mJobs;
  }

  public List<Integer> getMatchIDs() {
    return mMatchIDs;
  }

  private List<Integer> realtime() throws Exception {
    return new ArrayList<>(request(REQUEST_URL));
  }

  private List<Integer> request(String requestUrl) throws Exception {
    Request request = new Request.Builder().url(requestUrl)
        .header("User-Agent", "Android 6.0.1/CLT-AL00/9")
        .build();
    Response response = mClient.newCall(request).execute();
    if (!response.isSuccessful() || response.body() == null) {
      System.out.println("Request Failed: " + response.code());
      return Collections.emptyList();
    }


    String html = response.body().string();
    return new ListParser(html, ListParser.TIME_FILTER).doParse();
  }
}

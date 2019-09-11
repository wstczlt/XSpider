package com.test.dszuqiu;

import static com.test.manual.HistoryTester.TEST_MATCHES;
import static com.test.tools.Utils.valueOfInt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.test.Config;
import com.test.dszuqiu.parser.ListParser;
import com.test.http.HttpJob;
import com.test.http.HttpJobBuilder;
import com.test.http.HttpUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DsHistoryJobFactory {

  private static final String REQUEST_URL =
      "http://api.dszuqiu.com/v6/diary?day=%s&page=1&token=&only_need=0&per_page=5000";

  private final int mLastDays;
  private final OkHttpClient mClient;
  private final HttpJobBuilder mBuilder;

  private List<List<HttpJob>> mJobs;
  private List<Integer> mMatchIDs;

  public DsHistoryJobFactory(int lastDays, HttpJobBuilder builder) {
    mLastDays = lastDays;
    mBuilder = builder;
    mClient = HttpUtils.newHttpClient();
  }

  public List<List<HttpJob>> build() throws Exception {
    if (mJobs != null) {
      return mJobs;
    }

    mJobs = new ArrayList<>();
    if (!TEST_MATCHES.isEmpty()) { // 测试指定
      mMatchIDs = TEST_MATCHES;
    } else {
      mMatchIDs = realtime();
    }
    for (int matchID : mMatchIDs) {
      mJobs.add(mBuilder.buildJobs(matchID));
    }

    return mJobs;
  }

  public List<Integer> getMatchIDs() {
    return mMatchIDs;
  }

  private List<Integer> realtime() throws Exception {
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    List<Integer> matchIDs = new ArrayList<>();
    for (int i = -mLastDays; i <= 0; i++) { // 过去到未来
      matchIDs.addAll(request(String.format(REQUEST_URL,
          sdf.format(new Date(System.currentTimeMillis() + 86400_000 * i)))));
    }

    return matchIDs;
  }

  private List<Integer> request(String requestUrl) throws Exception {
    Config.LOGGER.log(requestUrl);
    Request request = new Request.Builder().url(requestUrl)
        .header("User-Agent", "Android 6.0.1/CLT-AL00/9")
        .build();
    Response response = mClient.newCall(request).execute();
    if (!response.isSuccessful() || response.body() == null) {
      System.out.println("Request Failed: " + response.code());
      return Collections.emptyList();
    }


    String html = response.body().string();
    return new ListParser(html, race -> valueOfInt(race.getString("time_status")) == 3).doParse();
  }
}

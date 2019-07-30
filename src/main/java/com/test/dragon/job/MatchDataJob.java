package com.test.dragon.job;

import java.util.Map;

import okhttp3.Request;

// 获取场上基本数据情况
// curl -H 'Host: txt.win007.com' -H 'User-Agent: okhttp/3.10.0' --compressed
// 'http://txt.win007.com//phone/airlive/cn/1/75/1757870.htm?androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564455118725000'
public class MatchDataJob extends Job {

  private static final String REQUEST_URL_PREFIX =
      "http://txt.win007.com//phone/airlive/cn/%s/%s/%s.htm?";
  private static final String REQUEST_URL_POSTFIX =
      "androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564455118725000";

  public MatchDataJob(int matchID) {
    super(matchID);
  }

  @Override
  public Request.Builder newRequestBuilder() {
    String matchIDString = String.valueOf(mMatchID);
    String prefixUrl = String.format(REQUEST_URL_PREFIX,
        matchIDString.substring(0, 1), matchIDString.substring(1, 3), matchIDString);
    String newUrl = prefixUrl + REQUEST_URL_POSTFIX.replace("ran=1564488834189000",
        "ran=" + System.currentTimeMillis() * 1000);

    return new Request.Builder().url(newUrl);
  }

  @Override
  public void handleResponse(String text, Map<String, String> items) {
    // System.out.println(text);
  }
}

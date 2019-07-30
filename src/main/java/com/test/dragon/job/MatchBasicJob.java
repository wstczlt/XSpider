package com.test.dragon.job;

import java.util.Map;

import okhttp3.Request;

// 比赛基本信息
// curl -H 'Host: txt.win007.com' -H 'User-Agent: okhttp/3.10.0' --compressed
// 'http://txt.win007.com//phone/txt/analysisheader/cn/1/66/1662676.txt?androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564488834189000'

public class MatchBasicJob extends Job {

  private static final String REQUEST_URL_PREFIX =
      "http://txt.win007.com//phone/txt/analysisheader/cn/%s/%s/%s.txt?";

  private static final String REQUEST_URL_POSTFIX =
      "androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564488834189000";

  public MatchBasicJob(int matchID) {
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
    String[] attrs = text.split("\\^");

    // System.out.println(attrs[0]); // 主队
    // System.out.println(attrs[1]); // 客队
    // System.out.println(attrs[5]); // 比赛时间
    // System.out.println(attrs[15]); // 联赛名称
    // System.out.println(attrs[attrs.length - 12]); // 温度
    // System.out.println(attrs[attrs.length - 11]); // 天气
    // System.out.println(Arrays.asList(attrs));
  }
}

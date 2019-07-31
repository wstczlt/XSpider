package com.test.dragon.job;

import java.util.Map;

import com.test.dragon.tools.Job;

import okhttp3.Request;

// 半场让球
// curl -H 'Host: apk.win007.com' -H 'User-Agent: okhttp/3.10.0' --compressed
// 'http://apk.win007.com//phone/analyoddsdetail.aspx?scheid=1757870&oddstype=1&matchtime=20190730093000&ishalf=1&androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564455366550000'
public class HalfScoreOddJob extends Job {

  private static final String REQUEST_URL =
      "http://apk.win007.com//phone/analyoddsdetail.aspx?scheid=1757870&oddstype=1&matchtime=20190730093000&ishalf=1&androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564455366550000";

  public HalfScoreOddJob(int matchID) {
    super(matchID);
  }

  @Override
  public Request.Builder newRequestBuilder() {
    String newUrl = REQUEST_URL.replace("scheid=1757870", "scheid=" + mMatchID)
        .replace("ran=1564455366550000", "ran=" + System.currentTimeMillis() * 1000);

    return new Request.Builder().url(newUrl);
  }

  @Override
  public void handleResponse(String text, Map<String, String> items) {
    // System.out.println(text);
  }

}

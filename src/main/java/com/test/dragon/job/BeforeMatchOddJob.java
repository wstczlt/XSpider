package com.test.dragon.job;

import java.util.Map;

import okhttp3.Request;

// 赛前指数(包含竞彩、必发等指数)
// curl -H 'Host: txt.win007.com' -H 'User-Agent: okhttp/3.10.0' --compressed
// 'http://txt.win007.com//phone/analyoddstxt/1/75/1753506.htm?androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564455656781000'
public class BeforeMatchOddJob extends Job {

  public BeforeMatchOddJob(int matchID) {
    super(matchID);
  }

  @Override
  public Request.Builder newRequestBuilder() {
    return null;
  }

  @Override
  public void handleResponse(String text, Map<String, String> items) {

  }
}

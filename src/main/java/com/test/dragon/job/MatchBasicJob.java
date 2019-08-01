package com.test.dragon.job;

import static com.test.dragon.tools.DragonUtils.setSkip;

import java.util.Map;

import com.github.promeg.pinyinhelper.Pinyin;
import com.test.dragon.kernel.DragonJob;
import com.test.dragon.tools.DragonUtils;
import com.test.tools.Logger;

import okhttp3.Request;

// 比赛基本信息
// curl -H 'Host: txt.win007.com' -H 'User-Agent: okhttp/3.10.0' --compressed
// 'http://txt.win007.com//phone/txt/analysisheader/cn/1/66/1662676.txt?androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564488834189000'

public class MatchBasicJob extends DragonJob {

  private static final String REQUEST_URL_PREFIX =
      "http://txt.win007.com//phone/txt/analysisheader/cn/%s/%s/%s.txt?";

  private static final String REQUEST_URL_POSTFIX =
      "androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564488834189000";

  public MatchBasicJob(int matchID, Logger logger) {
    super(matchID, logger);
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
  public void handleResponse(String text, Map<String, String> items) throws Exception {
    String[] attrs = text.split("\\^");
    if (attrs.length != 31) {
      setSkip(items);
      return;
    }
    items.put(MATCH_ID, String.valueOf(mMatchID));
    items.put(HOST_NAME, attrs[0]);
    items.put(HOST_NAME_PINYIN, Pinyin.toPinyin(attrs[0], ""));
    items.put(CUSTOM_NAME, attrs[1]);
    items.put(CUSTOM_NAME_PINYIN, Pinyin.toPinyin(attrs[1], ""));
    items.put(MATCH_TIME, "" + DragonUtils.valueOfDate(attrs[5]));
    items.put(LEAGUE, attrs[15]);
    items.put(HOST_LEAGUE_RANK, attrs[6]);
    items.put(CUSTOM_LEAGUE_RANK, attrs[7]);
    items.put(HOST_SCORE, attrs[10]);
    items.put(CUSTOM_SCORE, attrs[11]);
    items.put(TEMPERATURE, attrs[19]);
    items.put(WEATHER, attrs[20]);
    items.put(MIDDLE_HOST_SCORE, attrs[26]);
    items.put(MIDDLE_CUSTOM_SCORE, attrs[27]);

    mLogger.log(
        String.format("Found Match: %d, %s VS %s, %s", mMatchID, attrs[0], attrs[1], attrs[5]));


    // System.out.println(attrs.length);
    // System.out.println(items);
    // System.out.println(Arrays.toString(attrs));
  }


}

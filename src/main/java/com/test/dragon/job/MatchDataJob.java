package com.test.dragon.job;

import static com.test.dragon.tools.DragonUtils.setSkip;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.dragon.DragonJob;
import com.test.tools.Logger;

import okhttp3.Request;

// 获取场上基本数据情况
// curl -H 'Host: txt.win007.com' -H 'User-Agent: okhttp/3.10.0' --compressed
// 'http://txt.win007.com//phone/airlive/cn/1/75/1757870.htm?androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564455118725000'
public class MatchDataJob extends DragonJob {

  private static final String REQUEST_URL_PREFIX =
      "http://txt.win007.com//phone/airlive/cn/%s/%s/%s.htm?";
  private static final String REQUEST_URL_POSTFIX =
      "androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564455118725000";

  public MatchDataJob(int matchID, Logger logger) {
    super(matchID, logger);
  }

  @Override
  public Request.Builder newRequestBuilder() {
    String matchIDString = String.valueOf(mMatchID);
    String prefixUrl = String.format(REQUEST_URL_PREFIX,
        matchIDString.substring(0, 1), matchIDString.substring(1, 3), matchIDString);
    String newUrl = prefixUrl + REQUEST_URL_POSTFIX;

    return new Request.Builder().url(newUrl);
  }

  @Override
  public void handleResponse(String text, Map<String, String> items) throws Exception {
    JSONObject json = JSON.parseObject(text);
    if (json == null) {
      mLogger
          .log(String.format("Skipped: %s [%d] \n %s", getClass().getSimpleName(), mMatchID, text));
      setSkip(items);
      return;
    }
    JSONArray listItemsTech = (JSONArray) json.get("listItemsTech");
    for (int i = 0; i < listItemsTech.size(); i++) {
      JSONObject item = listItemsTech.getJSONObject(i);
      if ("角球".equals(item.getString("Name"))) {
        items.put(HOST_CORNER_SCORE, item.getString("HomeData"));
        items.put(CUSTOM_CORNER_SCORE, item.getString("AwayData"));
      }
      if ("半场角球".equals(item.getString("Name"))) {
        items.put(MIDDLE_HOST_CORNER_SCORE, item.getString("HomeData"));
        items.put(MIDDLE_CUSTOM_CORNER_SCORE, item.getString("AwayData"));
      }
      if ("射正".equals(item.getString("Name"))) {
        items.put(HOST_BEST_SHOOT, item.getString("HomeData"));
        items.put(CUSTOM_BEST_SHOOT, item.getString("AwayData"));
      }
      if ("黄牌".equals(item.getString("Name"))) {
        items.put(HOST_YELLOW_CARD, item.getString("HomeData"));
        items.put(CUSTOM_YELLOW_CARD, item.getString("AwayData"));
      }
      if ("控球率".equals(item.getString("Name"))) {
        items.put(HOST_CONTROL_RATE, item.getString("HomeData").replace("%", ""));
        items.put(CUSTOM_CONTROL_RATE, item.getString("AwayData").replace("%", ""));
      }
    }

    JSONObject JTL = json.getJSONObject("JTL");
    if (JTL != null) {
      items.put(HOST_SCORE_OF_3, JTL.getString("Goals_h3"));
      items.put(CUSTOM_SCORE_OF_3, JTL.getString("Goals_g3"));
      items.put(HOST_LOSS_OF_3, JTL.getString("LossGoals_h3"));
      items.put(CUSTOM_LOSS_OF_3, JTL.getString("LossGoals_g3"));
      items.put(HOST_CORNER_OF_3, JTL.getString("Corner_h3"));
      items.put(CUSTOM_CORNER_OF_3, JTL.getString("Corner_g3"));
      items.put(HOST_YELLOW_CARD_OF_3, JTL.getString("Yellow_h3"));
      items.put(CUSTOM_YELLOW_CARD_OF_3, JTL.getString("Yellow_g3"));
      items.put(HOST_CONTROL_RATE_OF_3, JTL.getString("ControlPrecent_h3"));
      items.put(CUSTOM_CONTROL_RATE_OF_3, JTL.getString("ControlPrecent_g3"));
    }

    // System.out.println(text);
  }
}

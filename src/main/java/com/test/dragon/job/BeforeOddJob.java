package com.test.dragon.job;

import static com.test.dragon.tools.DragonUtils.setSkip;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.dragon.DragonJob;
import com.test.tools.Logger;

import okhttp3.Request;

// 赛前指数(包含竞彩、必发等指数)
// curl -H 'Host: txt.win007.com' -H 'User-Agent: okhttp/3.10.0' --compressed
// 'http://txt.win007.com//phone/analyoddstxt/1/75/1753506.htm?androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564455656781000'
public class BeforeOddJob extends DragonJob {

  private static final String REQUEST_URL_PREFIX =
      "http://txt.win007.com//phone/analyoddstxt/%s/%s/%s.htm?";
  private static final String REQUEST_URL_POSTFIX =
      "androidfrom=nowscore&fromkind=1&version=4.80&app_token=sOA9HfVbPo1ywNVYl1Hi9wypKCjh63cf7FXrAekSYYCzl2OzgGZulNovlmS2%2F5WSKkoi6v9DpusnDmFD379Pv%2F40uIfkowNb7vhleIPHPrmHzGv5gUg6zf%2F252R0BBIvMbrlYIc%2B4hI7Oj8hhMKW%2BZNW8lpH8N8PcPTE5XWQc5M%3D&ran=1564455656781000";

  public BeforeOddJob(int matchID, Logger logger) {
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
  public void handleResponse(String text, Map<String, String> items) {
    JSONObject json = JSON.parseObject(text);
    if (json == null) {
      // mLogger
      // .log(String.format("Skipped: %s [%d] \n %s", getClass().getSimpleName(), mMatchID, text));
      setSkip(items);
      return;
    }
    JSONObject BFOdds = json.getJSONObject("BFOdds");
    if (BFOdds != null) {
      items.put(BF_LR_HOST, BFOdds.getString("lr_home"));
      items.put(BF_LR_DREW, BFOdds.getString("lr_draw"));
      items.put(BF_LR_CUSTOM, BFOdds.getString("lr_guest"));
    }

    items.put(ORIGINAL_VICTORY_ODD, json.getString("FirstHomeWin"));
    items.put(ORIGINAL_DRAW_ODD, json.getString("FirstStandoff"));
    items.put(ORIGINAL_DEFEAT_ODD, json.getString("FirstAwayWin"));
    items.put(OPENING_VICTORY_ODD, json.getString("HomeWin"));
    items.put(OPENING_DRAW_ODD, json.getString("Standoff"));
    items.put(OPENING_DEFEAT_ODD, json.getString("AwayWin"));

    items.put(ORIGINAL_BIG_ODD, json.getString("FirstOU"));
    items.put(ORIGINAL_BIG_ODD_OF_VICTORY, json.getString("FirstOverOdds"));
    items.put(ORIGINAL_BIG_ODD_OF_DEFEAT, json.getString("FirstUnderOdds"));
    items.put(OPENING_BIG_ODD, json.getString("OU"));
    items.put(OPENING_BIG_ODD_OF_VICTORY, json.getString("OverOdds"));
    items.put(OPENING_BIG_ODD_OF_DEFEAT, json.getString("UnderOdds"));

    if (json.containsKey("FirstLetGoal")) {
      items.put(ORIGINAL_SCORE_ODD, -json.getFloatValue("FirstLetGoal") + "");
      items.put(ORIGINAL_SCORE_ODD_OF_VICTORY, json.getFloatValue("FirstHomeOdds") + "");
      items.put(ORIGINAL_SCORE_ODD_OF_DEFEAT, json.getFloatValue("FirstAwayOdds") + "");
    }
    if (json.containsKey("LetGoal")) {
      items.put(OPENING_SCORE_ODD, -json.getFloatValue("LetGoal") + "");
      items.put(OPENING_SCORE_ODD_OF_VICTORY, json.getFloatValue("HomeOdds") + "");
      items.put(OPENING_SCORE_ODD_OF_DEFEAT, json.getFloatValue("AwayOdds") + "");
    }

    JSONArray oddsCompare = json.getJSONArray("OddsCompare");
    if (oddsCompare != null) {
      for (int i = 0; i < oddsCompare.size(); i++) {
        JSONObject item = oddsCompare.getJSONObject(i);
        String companyID = item.getString("companyID");
        items.put(ODD_COMPANY_FIRST_VICTORY_ + companyID, item.getString("homeOddsFirst"));
        items.put(ODD_COMPANY_FIRST_DREW_ + companyID, item.getString("goalFirst"));
        items.put(ODD_COMPANY_FIRST_DEFEAT_ + companyID, item.getString("guestOddsFirst"));

        items.put(ODD_COMPANY_OPEN_VICTORY_ + companyID, item.getString("homeOdds"));
        items.put(ODD_COMPANY_OPEN_DREW_ + companyID, item.getString("goal"));
        items.put(ODD_COMPANY_OPEN_DEFEAT_ + companyID, item.getString("guestOdds"));
      }
    }



    // System.out.println(text);
    //
    // System.out.println(items);
  }
}

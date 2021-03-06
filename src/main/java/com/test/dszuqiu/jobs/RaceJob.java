package com.test.dszuqiu.jobs;

import static com.test.tools.Utils.setSkip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.Config;
import com.test.dszuqiu.parser.RaceParser;
import com.test.http.HttpJob;
import com.test.tools.Utils;

import okhttp3.Request;

public class RaceJob extends HttpJob {

  // private static final String REQUEST_URL =
  // "http://api.dszuqiu.com/v9/race/view?token=&race_id=%d";
  private static final String REQUEST_URL =
      "http://api.dszuqiu.com/v9/race/view?token=&race_id=%s";

  public RaceJob(int matchID) {
    super(matchID);
  }

  @Override
  public Request.Builder newRequestBuilder() {
    String requestUrl = String.format(REQUEST_URL, mMatchID);
    return new Request.Builder()
        .header("User-Agent", "Android 6.0.1/CLT-AL00/9")
        .url(requestUrl);
  }

  @Override
  public void onResponse(String text, Map<String, String> items) throws Exception {
    JSONObject json = JSON.parseObject(text);
    String error = RaceParser.isLegalRace(json);
    if (!TextUtils.isEmpty(error)) {
      setSkip(items);
      Config.LOGGER.log(
          String.format("[%s], matchID=%d, Error=%s", getClass().getSimpleName(), mMatchID, error));
      return;
    }

    // 处理Json
    new RaceParser(text, items).doParse();

    JSONObject race = json.getJSONObject("race");
    JSONObject host = race.getJSONObject("host");
    JSONObject guest = race.getJSONObject("guest");
    JSONObject league = race.getJSONObject("league");
    long matchTime = Utils.valueOfLong(race.getString("race_time")) * 1000;
    String leagueName = league.getString("short_name");
    String hostName = host.getString("sb_name");
    String customName = guest.getString("sb_name");

    items.put(MATCH_ID, "" + mMatchID);
    items.put(MATCH_TIME, matchTime + "");
    items.put(HOST_NAME, hostName);
    items.put(CUSTOM_NAME, customName);
    items.put(LEAGUE, leagueName);

    SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    sft.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

    Config.LOGGER.log(String.format("Found Match ID=%d, %s, [%s], %s VS %s",
        mMatchID,
        sft.format(new Date(matchTime)),
        leagueName,
        hostName,
        customName));
  }
}

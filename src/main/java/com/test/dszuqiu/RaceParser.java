package com.test.dszuqiu;

import static com.test.tools.Utils.setSkip;

import java.util.Map;

import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.promeg.pinyinhelper.Pinyin;
import com.test.Keys;
import com.test.tools.Utils;

public class RaceParser implements Keys {

  private final String mRawText;
  private final Map<String, String> mItems;

  public RaceParser(String rawText, Map<String, String> items) {
    mRawText = rawText;
    mItems = items;
  }


  public void doParse() {
    final JSONObject json = JSON.parseObject(mRawText);
    if (isLegalRace(json) != null) {
      setSkip(mItems);
      return;
    }

    basicParser(json);
  }


  private void basicParser(JSONObject json) {
    JSONObject race = json.getJSONObject("race");
    JSONObject host = race.getJSONObject("host");
    JSONObject guest = race.getJSONObject("guest");
    JSONObject league = race.getJSONObject("league");
    String matchID = race.getString("id");
    long matchTime = Utils.valueOfLong(race.getString("race_time")) * 1000;
    String leagueName = league.getString("short_name");
    String hostName = host.getString("sb_name");
    String customName = guest.getString("sb_name");
    int timeMin = timeMin(race.getString("status"));


    mItems.put(MATCH_ID, matchID);
    mItems.put(HOST_NAME, hostName);
    mItems.put(HOST_NAME_PINYIN, Pinyin.toPinyin(hostName, ""));
    mItems.put(CUSTOM_NAME, customName);
    mItems.put(CUSTOM_NAME_PINYIN, Pinyin.toPinyin(customName, ""));
    // mItems.put(MATCH_STATUS, attrs[4]);
    // items.put(MATCH_TIME, "" + Utils.valueOfDate(attrs[5]));
    // items.put(LEAGUE, attrs[15]);
    // items.put(HOST_LEAGUE_RANK, attrs[6]);
    // items.put(CUSTOM_LEAGUE_RANK, attrs[7]);
    // items.put(HOST_SCORE, attrs[10]);
    // items.put(CUSTOM_SCORE, attrs[11]);
    // items.put(TEMPERATURE, attrs[19]);
    // items.put(WEATHER, attrs[20]);
    // items.put(MIDDLE_HOST_SCORE, attrs[26]);
    // items.put(MIDDLE_CUSTOM_SCORE, attrs[27]);
  }


  public static String isLegalRace(JSONObject json) {
    if (json == null) {
      return "Json Null";
    } else {
      String error = json.getString("error");
      if (!TextUtils.isEmpty(error)) {
        return error;
      }
    }
    JSONObject race = json.getJSONObject("race");
    if (race == null) {
      return "Race Null";
    }

    return null;
  }

  public static String isLegalOdd(JSONObject json) {
    if (json == null) {
      return "Json Null";
    } else {
      String error = json.getString("error");
      if (!TextUtils.isEmpty(error)) {
        return error;
      }
    }
    JSONObject baijia = json.getJSONObject("baijia");
    if (baijia == null) {
      return "Pankou Null";
    }
    Object ji_pankou = baijia.get("ji_pankou");
    if (ji_pankou == null) {
      return "Pankou Null";
    }
    Object start_pankou = baijia.get("start_pankou");
    if (start_pankou == null) {
      return "Pankou Null";
    }

    return null;
  }

  private static int timeMin(String matchStatus) {
    try {
      Integer.parseInt(matchStatus);
    } catch (Exception e) {
      System.out.println(matchStatus);
    }
    return 0;
  }
}

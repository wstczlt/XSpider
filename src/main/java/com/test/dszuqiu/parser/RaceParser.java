package com.test.dszuqiu.parser;

import static com.test.tools.Utils.setSkip;

import java.util.Map;

import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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


    mItems.put(MATCH_ID, matchID);
    mItems.put(MATCH_TIME, matchTime + "");
    mItems.put(MATCH_STATUS, race.getString("time_status"));
    mItems.put(TIME_MIN, timeMin(race.getString("status")) + "");
    mItems.put(ZHANYI, race.getString("zhanyi"));


    mItems.put(LEAGUE, leagueName);
    mItems.put(LEAGUE_ID, league.getString("id"));
    mItems.put(LEAGUE_JUESHA_RATE, league.getString("juesha_rate"));
    mItems.put(LEAGUE_DA_RATE, league.getString("da_rate"));
    mItems.put(LEAGUE_XIAO_RATE, league.getString("xiao_rate"));


    mItems.put(HOST_NAME, hostName);
    mItems.put(HOST_ID, host.getString("id"));
    mItems.put(HOST_NAME_PINYIN, Pinyin.toPinyin(hostName, ""));
    mItems.put(HOST_LEAGUE_RANK, race.getString("host_pm"));
    mItems.put(HOST_JUESHA_RATE, host.getString("juesha_rate"));
    mItems.put(HOST_DA_RATE, host.getString("da_rate"));
    mItems.put(HOST_XIAO_RATE, host.getString("xiao_rate"));


    mItems.put(CUSTOM_ID, guest.getString("id"));
    mItems.put(CUSTOM_NAME, customName);
    mItems.put(CUSTOM_NAME_PINYIN, Pinyin.toPinyin(customName, ""));
    mItems.put(CUSTOM_LEAGUE_RANK, race.getString("guest_pm"));
    mItems.put(CUSTOM_JUESHA_RATE, guest.getString("juesha_rate"));
    mItems.put(CUSTOM_DA_RATE, guest.getString("da_rate"));
    mItems.put(CUSTOM_XIAO_RATE, guest.getString("xiao_rate"));



    // 场上实时数据
    Object obj = json.get("race_data");
    if (obj instanceof JSONObject && !((JSONObject) obj).keySet().isEmpty()) {
      JSONObject race_data = (JSONObject) obj;
      mItems.put(HOST_SCORE, race_data.getString("host_goal"));
      mItems.put(CUSTOM_SCORE, race_data.getString("guest_goal"));
      mItems.put(HOST_CORNER_SCORE, race_data.getString("host_corner"));
      mItems.put(CUSTOM_CORNER_SCORE, race_data.getString("guest_corner"));
      mItems.put(HOST_YELLOW_CARD, race_data.getString("host_yellowcard"));
      mItems.put(CUSTOM_YELLOW_CARD, race_data.getString("guest_yellowcard"));
      mItems.put(HOST_RED_CARD, race_data.getString("host_redcard"));
      mItems.put(CUSTOM_RED_CARD, race_data.getString("guest_redcard"));
    }

    obj = race.get("race_plus");
    if (!(obj instanceof JSONObject)) {
      // 外层找一下
      obj = json.get("race_plus");
    }
    if (obj instanceof JSONObject && !((JSONObject) obj).keySet().isEmpty()) {
      JSONObject race_plus = (JSONObject) obj;
      mItems.put(HOST_DANGER, race_plus.getString("host_danger"));
      mItems.put(CUSTOM_DANGER, race_plus.getString("guest_danger"));
      mItems.put(HOST_BEST_SHOOT, race_plus.getString("host_shotongoal"));
      mItems.put(CUSTOM_BEST_SHOOT, race_plus.getString("guest_shotongoal"));
      mItems.put(HOST_CONTROL_RATE, race_plus.getString("host_qiuquan"));
      mItems.put(CUSTOM_CONTROL_RATE, race_plus.getString("guest_qiuquan"));
    }

    obj = race.get("race_half");
    if (obj instanceof JSONObject && !((JSONObject) obj).keySet().isEmpty()) {
      JSONObject race_half = (JSONObject) obj;
      mItems.put(MIDDLE_HOST_SCORE, race_half.getString("host_goal"));
      mItems.put(MIDDLE_CUSTOM_SCORE, race_half.getString("guest_goal"));
      mItems.put(MIDDLE_HOST_CORNER_SCORE, race_half.getString("host_corner"));
      mItems.put(MIDDLE_CUSTOM_CORNER_SCORE, race_half.getString("guest_corner"));
      mItems.put(MIDDLE_HOST_YELLOW_CARD, race_half.getString("host_yellowcard"));
      mItems.put(MIDDLE_CUSTOM_YELLOW_CARD, race_half.getString("guest_yellowcard"));
      mItems.put(MIDDLE_HOST_RED_CARD, race_half.getString("host_redcard"));
      mItems.put(MIDDLE_CUSTOM_RED_CARD, race_half.getString("guest_redcard"));
    }

    obj = race.get("race_start_first");
    if (obj instanceof JSONObject && !((JSONObject) obj).keySet().isEmpty()) {
      JSONObject race_start_first = (JSONObject) obj;
      mItems.put(ORIGINAL_VICTORY_ODD, race_start_first.getString("host_sp"));
      mItems.put(ORIGINAL_DREW_ODD, race_start_first.getString("tie_sp"));
      mItems.put(ORIGINAL_DEFEAT_ODD, race_start_first.getString("guest_sp"));

      mItems.put(ORIGINAL_SCORE_ODD,
          Utils.convertDsOdd(race_start_first.getString("rangfen_handicap")) + "");
      mItems.put(ORIGINAL_SCORE_ODD_OF_VICTORY, race_start_first.getString("rangfen_host_sp"));
      mItems.put(ORIGINAL_SCORE_ODD_OF_DEFEAT, race_start_first.getString("rangfen_guest_sp"));

      mItems.put(ORIGINAL_BIG_ODD,
          Utils.convertDsOdd(race_start_first.getString("daxiao_handicap")) + "");
      mItems.put(ORIGINAL_BIG_ODD_OF_VICTORY, race_start_first.getString("daxiao_up_sp"));
      mItems.put(ORIGINAL_BIG_ODD_OF_DEFEAT, race_start_first.getString("daxiao_low_sp"));

    } else { // 初始赔率必须要求有
      setSkip(mItems);
    }


    obj = race.get("race_start");
    if (obj instanceof JSONObject && !((JSONObject) obj).keySet().isEmpty()) {
      JSONObject race_start = (JSONObject) obj;
      mItems.put(OPENING_VICTORY_ODD, race_start.getString("host_sp"));
      mItems.put(OPENING_DRAW_ODD, race_start.getString("tie_sp"));
      mItems.put(OPENING_DEFEAT_ODD, race_start.getString("guest_sp"));

      mItems.put(OPENING_SCORE_ODD,
          Utils.convertDsOdd(race_start.getString("rangfen_handicap")) + "");
      mItems.put(OPENING_SCORE_ODD_OF_VICTORY, race_start.getString("rangfen_host_sp"));
      mItems.put(OPENING_SCORE_ODD_OF_DEFEAT, race_start.getString("rangfen_guest_sp"));

      mItems.put(OPENING_BIG_ODD,
          Utils.convertDsOdd(race_start.getString("daxiao_handicap")) + "");
      mItems.put(OPENING_BIG_ODD_OF_VICTORY, race_start.getString("daxiao_up_sp"));
      mItems.put(OPENING_BIG_ODD_OF_DEFEAT, race_start.getString("daxiao_low_sp"));
    }

    JSONObject sp = json.getJSONObject("sp");
    if (sp != null) {
      JSONArray rangfen = sp.getJSONArray("rangfen");
      if (rangfen != null && rangfen.size() > 0) {
        lineRangfen(rangfen);
      }

      JSONArray daxiao = sp.getJSONArray("daxiao");
      if (daxiao != null && daxiao.size() > 0) {
        lineDaxiao(daxiao);
      }

      JSONArray bet = sp.getJSONArray("bet");
      if (bet != null && bet.size() > 0) {
        lineBet(bet);
      }

      // JSONArray corner = sp.getJSONArray("corner");
      // if (corner != null && corner.size() > 0) {
      // lineCorner(corner);
      // }
    }

    JSONArray trend = json.getJSONArray("trend");
    if (trend != null && trend.size() > 0) {
      lineTrend(trend);
    }
  }

  private void lineRangfen(JSONArray rangfen) {
    JSONObject last = new JSONObject();
    last.put("handicap", mItems.get(OPENING_SCORE_ODD));
    last.put("host_sp", mItems.get(OPENING_SCORE_ODD_OF_VICTORY));
    last.put("guest_sp", mItems.get(OPENING_SCORE_ODD_OF_DEFEAT));
    last.put("host_goal", "0");
    last.put("guest_goal", "0");
    for (int i = 0; i <= 90; i++) {
      JSONObject item = searchItem(rangfen, i, "handicap");
      if (item == null) item = last;
      last = item;
      mItems.put("min" + i + "_scoreOdd", Utils.convertDsOdd(item.getString("handicap")) + "");
      mItems.put("min" + i + "_scoreOddOfVictory", item.getString("host_sp"));
      mItems.put("min" + i + "_scoreOddOfDefeat", item.getString("guest_sp"));
      mItems.put("min" + i + "_hostScore", item.getString("host_goal"));
      mItems.put("min" + i + "_customScore", item.getString("guest_goal"));
    }
  }

  private void lineDaxiao(JSONArray daxiao) {
    JSONObject last = new JSONObject();
    last.put("handicap", mItems.get(OPENING_BIG_ODD));
    last.put("up_sp", mItems.get(OPENING_BIG_ODD_OF_VICTORY));
    last.put("low_sp", mItems.get(OPENING_BIG_ODD_OF_DEFEAT));
    last.put("host_goal", "0");
    last.put("guest_goal", "0");
    for (int i = 0; i <= 90; i++) {
      JSONObject item = searchItem(daxiao, i, "handicap");
      if (item == null) item = last;
      last = item;
      mItems.put("min" + i + "_bigOdd", Utils.convertDsOdd(item.getString("handicap")) + "");
      mItems.put("min" + i + "_bigOddOfVictory", item.getString("up_sp"));
      mItems.put("min" + i + "_bigOddOfDefeat", item.getString("low_sp"));
      mItems.put("min" + i + "_hostScore", item.getString("host_goal"));
      mItems.put("min" + i + "_customScore", item.getString("guest_goal"));
    }
  }

  private void lineBet(JSONArray rangfen) {
    JSONObject last = new JSONObject();
    last.put("tie_sp", mItems.get(OPENING_DRAW_ODD));
    last.put("host_sp", mItems.get(OPENING_VICTORY_ODD));
    last.put("guest_sp", mItems.get(OPENING_DEFEAT_ODD));
    last.put("host_goal", "0");
    last.put("guest_goal", "0");
    for (int i = 0; i <= 90; i++) {
      JSONObject item = searchItem(rangfen, i, "tie_sp");
      if (item == null) item = last;
      last = item;
      mItems.put("min" + i + "_drewOdd", item.getString("tie_sp") + "");
      mItems.put("min" + i + "_victoryOdd", item.getString("host_sp"));
      mItems.put("min" + i + "_defeatOdd", item.getString("guest_sp"));
      mItems.put("min" + i + "_hostScore", item.getString("host_goal"));
      mItems.put("min" + i + "_customScore", item.getString("guest_goal"));
    }
  }


  private void lineTrend(JSONArray trend) {
    JSONObject last = new JSONObject();
    last.put("host_shotongoal", "0");
    last.put("guest_shotongoal", "0");
    last.put("host_corner", "0");
    last.put("guest_corner", "0");
    last.put("host_goal", "0");
    last.put("guest_goal", "0");
    for (int i = 0; i <= 90; i++) {
      JSONObject item = searchItem(trend, i, "host_shotongoal");
      if (item == null) item = last;
      last = item;
      mItems.put("min" + i + "_hostDanger", item.getString("host_danger"));
      mItems.put("min" + i + "_customDanger", item.getString("guest_danger"));
      mItems.put("min" + i + "_hostBestShoot", item.getString("host_shotongoal"));
      mItems.put("min" + i + "_customBestShoot", item.getString("guest_shotongoal"));
      mItems.put("min" + i + "_hostCorner", item.getString("host_corner"));
      mItems.put("min" + i + "_customCorner", item.getString("guest_corner"));
      mItems.put("min" + i + "_hostScore", item.getString("host_goal"));
      mItems.put("min" + i + "_customScore", item.getString("guest_goal"));
    }
  }

  private JSONObject searchItem(JSONArray items, int timeMin, String verifyKey) {
    for (int i = 0; i < items.size(); i++) {
      JSONObject item = items.getJSONObject(i);
      String verifyValue = item.getString(verifyKey);
      if (verifyValue == null) { // 非法
        continue;
      }
      int passed_sec = item.getIntValue("passed_sec");
      if (passed_sec >= 0 && passed_sec >= timeMin * 60 && passed_sec < (timeMin + 1) * 60) {
        return item;
      }
    }

    return null;
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
      return Integer.parseInt(matchStatus);
    } catch (Exception e) {
      switch (matchStatus) {
        case "全":
          return 90;
        case "未":
          return -1;
        case "中":
          return 45;
        default:
          return -1;
      }
    }
  }
}

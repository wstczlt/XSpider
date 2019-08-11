package com.test.dszuqiu.parser;

import static com.test.tools.Utils.convertDsOdd;
import static com.test.tools.Utils.valueOfFloat;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.Keys;

public class OddParser implements Keys {

  private final String mRawText;
  private final Map<String, String> mItems;

  public OddParser(String rawText, Map<String, String> items) {
    mRawText = rawText;
    mItems = items;
  }

  public void doParse() {
    final JSONObject json = JSON.parseObject(mRawText);
    basicParser(json);
  }

  private void basicParser(JSONObject json) {
    JSONObject baijia = json.getJSONObject("baijia");
    String matchID = baijia.getString("ds_race_id");
    mItems.put(MATCH_ID, matchID);

    JSONArray ji_pankou = baijia.getJSONArray("ji_pankou");
    JSONArray start_pankou = baijia.getJSONArray("start_pankou");

    ji_pankou("ji_", ji_pankou);
    start_pankou("start_", start_pankou);
  }

  private void start_pankou(String prefix, JSONArray array) {
    for (int i = 0; i < array.size(); i++) {
      JSONObject item = array.getJSONObject(i);
      String cID = item.getString("id");
      Object obj = item.get("pankou");
      if (obj instanceof JSONObject && !((JSONObject) obj).keySet().isEmpty()) {
        JSONObject pankou = (JSONObject) obj;
        float scoreOdd = convertDsOdd(pankou.getString("rangfen_handicap"));
        float scoreOddOfVictory = valueOfFloat(pankou.getString("rangfen_host_sp"));
        float scoreOddOfDefeat = valueOfFloat(pankou.getString("rangfen_guest_sp"));
        mItems.put(prefix + cID + "_" + "scoreOdd", scoreOdd + "");
        mItems.put(prefix + cID + "_" + "scoreOddOfVictory", scoreOddOfVictory + "");
        mItems.put(prefix + cID + "_" + "scoreOddOfDefeat", scoreOddOfDefeat + "");


        float bigOdd = convertDsOdd(pankou.getString("daxiao_handicap"));
        float bigOddOfVictory = valueOfFloat(pankou.getString("daxiao_up_sp"));
        float bigOddOfDefeat = valueOfFloat(pankou.getString("daxiao_low_sp"));
        mItems.put(prefix + cID + "_" + "bigOdd", bigOdd + "");
        mItems.put(prefix + cID + "_" + "bigOddOfVictory", bigOddOfVictory + "");
        mItems.put(prefix + cID + "_" + "bigOddOfDefeat", bigOddOfDefeat + "");


        float drewOdd = convertDsOdd(pankou.getString("tie_sp"));
        float victoryOdd = valueOfFloat(pankou.getString("host_sp"));
        float defeatOdd = valueOfFloat(pankou.getString("guest_sp"));
        mItems.put(prefix + cID + "_" + "drewOdd", drewOdd + "");
        mItems.put(prefix + cID + "_" + "victoryOdd", victoryOdd + "");
        mItems.put(prefix + cID + "_" + "defeatOdd", defeatOdd + "");
      }
    }
  }

  private void ji_pankou(String prefix, JSONArray array) {
    for (int i = 0; i < array.size(); i++) {
      JSONObject item = array.getJSONObject(i);
      String cID = item.getString("id");
      Object obj = item.get("rangfen");
      if (obj instanceof JSONObject && !((JSONObject) obj).keySet().isEmpty()) {
        JSONObject rangfen = (JSONObject) obj;
        float handicap = convertDsOdd(rangfen.getString("handicap"));
        float host_sp = valueOfFloat(rangfen.getString("host_sp"));
        float guest_sp = valueOfFloat(rangfen.getString("guest_sp"));
        mItems.put(prefix + cID + "_" + "scoreOdd", handicap + "");
        mItems.put(prefix + cID + "_" + "scoreOddOfVictory", host_sp + "");
        mItems.put(prefix + cID + "_" + "scoreOddOfDefeat", guest_sp + "");
      }

      obj = item.get("daxiao");
      if (obj instanceof JSONObject && !((JSONObject) obj).keySet().isEmpty()) {
        JSONObject daxiao = (JSONObject) obj;
        float handicap = convertDsOdd(daxiao.getString("handicap"));
        float host_sp = valueOfFloat(daxiao.getString("up_sp"));
        float guest_sp = valueOfFloat(daxiao.getString("low_sp"));
        mItems.put(prefix + cID + "_" + "bigOdd", handicap + "");
        mItems.put(prefix + cID + "_" + "bigOddOfVictory", host_sp + "");
        mItems.put(prefix + cID + "_" + "bigOddOfDefeat", guest_sp + "");
      }

      obj = item.get("bet");
      if (obj instanceof JSONObject && !((JSONObject) obj).keySet().isEmpty()) {
        JSONObject bet = (JSONObject) obj;
        float handicap = convertDsOdd(bet.getString("tie_sp"));
        float host_sp = valueOfFloat(bet.getString("host_sp"));
        float guest_sp = valueOfFloat(bet.getString("guest_sp"));
        mItems.put(prefix + cID + "_" + "drewOdd", handicap + "");
        mItems.put(prefix + cID + "_" + "victoryOdd", host_sp + "");
        mItems.put(prefix + cID + "_" + "defeatOdd", guest_sp + "");
      }
    }
  }
}

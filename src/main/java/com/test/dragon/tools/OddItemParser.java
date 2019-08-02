package com.test.dragon.tools;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.spider.tools.SpiderUtils;
import com.test.tools.Utils;

public class OddItemParser implements Keys {

  private static final String PREFIX_MIDDLE = "middle_"; // 中场盘
  private static final String PREFIX_MIN_25 = "min25_"; // 25分钟盘
  private static final String PREFIX_MIN_30 = "min30_"; // 30分钟盘
  private static final String PREFIX_MIN_65 = "min65_"; // 65分钟盘
  private static final String PREFIX_MIN_70 = "min70_"; // 70分钟盘
  private static final String PREFIX_MIN_75 = "min75_"; // 75分钟盘

  private final OddType mType;
  private final JSONArray mJSONArray;

  private int mMaxMin; // 当前分钟数
  private ItemModel mMiddle; // 中场盘
  private ItemModel mMinOf25; // 25分钟盘
  private ItemModel mMinOf30; // 30分钟盘
  private ItemModel mMinOf65; // 65分钟盘
  private ItemModel mMinOf70; // 70分钟盘
  private ItemModel mMin0f75; // 75分钟盘

  public OddItemParser(OddType type, JSONArray array) {
    mType = type;
    mJSONArray = array;
    try {
      init();
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }
  }

  public void fill(Map<String, String> items) {
    List<ItemModel> lines = Arrays.asList(mMiddle, mMinOf25, mMinOf30,
        mMinOf65, mMinOf70, mMin0f75);
    int timeMin = Utils.valueOfInt(items.get(TIME_MIN));
    // 避免覆盖
    mMaxMin = Math.max(timeMin, mMaxMin);
    items.put("timeMin", mMaxMin + "");
    for (ItemModel line : lines) {
      if (line == null) {
        continue;
      }

      items.put(line.prefix() + mType.mHostScoreKey, line.mHostScore + "");
      items.put(line.prefix() + mType.mCustomScoreKey, line.mCustomScore + "");
      items.put(line.prefix() + mType.mOddKey, line.mOdd + "");
      items.put(line.prefix() + mType.mOddVictoryKey, line.mOddVictory + "");
      items.put(line.prefix() + mType.mOddDefeatKey, line.mOddDefeat + "");
    }
  }

  private void init() {
    boolean first25 = false; // 首个25分钟，用于破蛋
    boolean first30 = false;
    boolean first65 = false;
    boolean first70 = false;
    boolean first75 = false; // 首个70分钟，用于大球
    for (int i = mJSONArray.size() - 1; i > 0; i--) { // 倒着遍历, index=0是标题，不要
      final ItemModel line = new ItemModel(mJSONArray.getJSONObject(i), mType);
      mMaxMin = Math.max(line.mMinute, mMaxMin); // 时间
      if (line.isMiddle) {
        mMiddle = line;
      } else if (!first25 && line.mMinute >= 25) {
        mMinOf25 = line;
        first25 = true;
        line.isMin0f25 = true;
      } else if (!first30 && line.mMinute >= 30) {
        mMinOf30 = line;
        first30 = true;
        line.isMin0f30 = true;
      } else if (!first65 && line.mMinute >= 65) {
        mMinOf65 = line;
        first65 = true;
        line.isMin0f65 = true;
      } else if (!first70 && line.mMinute >= 70) {
        mMinOf70 = line;
        first70 = true;
        line.isMin0f70 = true;
      } else if (!first75 && line.mMinute >= 75) {
        mMin0f75 = line;
        first75 = true;
        line.isMin0f75 = true;
      }
    }
  }



  static class ItemModel {

    private final OddType mType;

    boolean isMiddle; // 是否中场
    boolean isMin0f25; // 是否中25分钟
    boolean isMin0f30; // 是否中30分钟
    boolean isMin0f65; // 是否中65分钟
    boolean isMin0f70; // 是否中70分钟
    boolean isMin0f75; // 是否中75分钟
    boolean isForbidden; // 是否封盘

    int mMinute; // 当前分钟
    int mHostScore;
    int mCustomScore;
    float mOdd; // 盘口
    float mOddVictory; // 主胜赔率
    float mOddDefeat; // 客胜赔率

    private final JSONObject mJson;

    ItemModel(JSONObject jsonObject, OddType type) {
      mType = type;
      mJson = jsonObject;
      try {
        init();
      } catch (Throwable e) {
        SpiderUtils.log(e);
      }
    }

    private void init() {
      final String stringOfMin = mJson.getString("HappenTime"); // 时间
      if (stringOfMin.trim().equals("中场")
          || stringOfMin.trim().equals("半")
          || stringOfMin.trim().equals("45")) {
        isMiddle = true;
        mMinute = 45;
      } else {
        mMinute = Utils.valueOfInt(stringOfMin);
      }
      String scoreString = mJson.getString("Score"); // 比分
      String[] scoreStringPair = scoreString.split("-");
      if (scoreStringPair.length == 2) {
        mHostScore = Utils.valueOfInt(scoreStringPair[0]);
        mCustomScore = Utils.valueOfInt(scoreStringPair[1]);
      }

      mOddVictory = mJson.getFloatValue("HomeOdds");
      mOddDefeat = mJson.getFloatValue("AwayOdds");
      String isClosedString = mJson.getString("IsClosed");
      if (!TextUtils.isEmpty(isClosedString)) { // 封盘，不要了
        isForbidden = true;
        return;
      }

      switch (mType) {
        case SCORE:
          mOdd = -mJson.getFloatValue("PanKou");
          break;
        case BALL:
          mOdd = mJson.getFloatValue("PanKou");
          break;
        case EUROPE:
          mOdd = mJson.getFloatValue("PanKou");
          break;
        case CORNER:
          mOdd = mJson.getFloatValue("PanKou");
          break;
      }
    }

    String prefix() {
      if (isMiddle) {
        return PREFIX_MIDDLE;
      }
      if (isMin0f25) {
        return PREFIX_MIN_25;
      }
      if (isMin0f30) {
        return PREFIX_MIN_30;
      }
      if (isMin0f65) {
        return PREFIX_MIN_65;
      }
      if (isMin0f70) {
        return PREFIX_MIN_70;
      }
      if (isMin0f75) {
        return PREFIX_MIN_75;
      }

      return "_";
    }

  }


  public enum OddType {

    SCORE("min", "hostScore", "customScore", "scoreOdd", "scoreOddOfVictory", "scoreOddOfDefeat"), // 让球
    BALL("min", "hostScore", "customScore", "bigOdd", "bigOddOfVictory", "bigOddOfDefeat"), // 大小球
    EUROPE("min", "hostScore", "customScore", "drawOdd", "victoryOdd", "defeatOdd"), // 欧指胜平负
    CORNER("min", "hostCornerScore", "customCornerScore", "cornerOdd", "cornerOddOfVictory",
        "cornerOddOfDefeat"); // 角球

    public final String mMinuteKey; // 当前分钟
    public final String mHostScoreKey;
    public final String mCustomScoreKey;
    public final String mOddKey; // 盘口
    public final String mOddVictoryKey; // 主胜赔率
    public final String mOddDefeatKey; // 客胜赔率

    OddType(String minuteKey, String hostScoreKey, String customScoreKey, String oddKey,
        String oddVictoryKey, String oddDefeatKey) {
      mMinuteKey = minuteKey;
      mHostScoreKey = hostScoreKey;
      mCustomScoreKey = customScoreKey;
      mOddKey = oddKey;
      mOddVictoryKey = oddVictoryKey;
      mOddDefeatKey = oddDefeatKey;
    }
  }
}

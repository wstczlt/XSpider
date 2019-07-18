package com.test.data;

import java.util.Map;

import com.test.xspider.utils.XSpiderUtils;

public class Match {

  public int mMatchID; // matchID
  public long mMatchTime; // 比赛时间
  public String mHostNamePinyin; // 主队名称拼音
  public String mCustomNamePinyin; // 客队名称拼音
  public int mHostScore; // 主队比分
  public int mCustomScore; // 客队比分

  public int mHostLeagueRank; // 主队联赛排名, 可能为0
  public int mHostLeagueOnHostRank; // 主队主场排名, 可能为0
  public int mCustomLeagueRank; // 客队联赛排名, 可能为0
  public int mCustomLeagueOnCustomRank; // 客队客场排名, 可能为0

  public float mOriginalScoreOdd; // 亚盘初盘让球盘口
  public float mOriginalScoreOddOfVictory; // 亚盘初盘让球盘口
  public float mOriginalScoreOddOfDefeat; // 亚盘初盘让球盘口

  public float mOriginalVictoryOdd; // 欧指初盘胜赔
  public float mOriginalDrawOdd; // 欧指初盘平赔
  public float mOriginalDefeatOdd; // 欧指初盘负赔

  public float mOpeningVictoryOdd; // 欧指即时盘胜赔
  public float mOpeningDrawOdd; // 欧指即时盘平赔
  public float mOpeningDefeatOdd; // 欧指即时盘负赔

  public float mOriginalBigOdd; // 初盘大小球盘口
  public float mOriginalBigOddOfVictory; // 初盘大小球赔率
  public float mOriginalBigOddOfDefeat; // 初盘大小球赔率

  public float mOpeningBigOdd; // 即时盘大小球盘口
  public float mOpeningBigOddOfVictory; // 即时盘大小球赔率
  public float mOpeningBigOddOfDefeat; // 即时盘大小球赔率

  public static Match fromMap(Map<String, Object> map) {
    Match match = new Match();
    match.mMatchID = XSpiderUtils.valueOfInt(map.get("matchID"));
    match.mMatchTime = Long.parseLong(String.valueOf(map.get("matchTime")));
    match.mHostNamePinyin = String.valueOf(map.get("hostNamePinyin"));
    match.mCustomNamePinyin = String.valueOf(map.get("customNamePinyin"));
    match.mHostScore = XSpiderUtils.valueOfInt(map.get("hostScore"));
    match.mCustomScore = XSpiderUtils.valueOfInt(map.get("customScore"));

    match.mHostLeagueRank = XSpiderUtils.valueOfInt(map.get("hostLeagueRank"));
    match.mHostLeagueOnHostRank =
        XSpiderUtils.valueOfInt(map.get("hostLeagueOnHostRank"));
    match.mCustomLeagueRank = XSpiderUtils.valueOfInt(map.get("customLeagueRank"));
    match.mCustomLeagueOnCustomRank =
        XSpiderUtils.valueOfInt(map.get("customLeagueOnCustomRank"));

    match.mOriginalScoreOdd = XSpiderUtils.valueOfFloat(map.get("original_scoreOdd"));
    match.mOriginalScoreOddOfVictory =
        XSpiderUtils.valueOfFloat(map.get("original_scoreOddOfVictory"));
    match.mOriginalScoreOddOfDefeat =
        XSpiderUtils.valueOfFloat(map.get("original_scoreOddOfDefeat"));

    match.mOriginalVictoryOdd =
        XSpiderUtils.valueOfFloat(map.get("original_victoryOdd"));
    match.mOriginalDrawOdd = XSpiderUtils.valueOfFloat(map.get("original_drawOdd"));
    match.mOriginalDefeatOdd = XSpiderUtils.valueOfFloat(map.get("original_defeatOdd"));
    match.mOpeningVictoryOdd = XSpiderUtils.valueOfFloat(map.get("opening_victoryOdd"));
    match.mOpeningDrawOdd = XSpiderUtils.valueOfFloat(map.get("opening_drawOdd"));
    match.mOpeningDefeatOdd = XSpiderUtils.valueOfFloat(map.get("opening_defeatOdd"));

    match.mOriginalBigOdd = XSpiderUtils.valueOfFloat(map.get("original_bigOdd"));
    match.mOriginalBigOddOfVictory =
        XSpiderUtils.valueOfFloat(map.get("original_bigOddOfVictory"));
    match.mOriginalBigOddOfDefeat =
        XSpiderUtils.valueOfFloat(map.get("original_bigOddOfDefeat"));
    match.mOpeningBigOdd = XSpiderUtils.valueOfFloat(map.get("opening_bigOdd"));
    match.mOpeningBigOddOfVictory =
        XSpiderUtils.valueOfFloat(map.get("opening_bigOddOfVictory"));
    match.mOpeningBigOddOfDefeat =
        XSpiderUtils.valueOfFloat(map.get("opening_bigOddOfDefeat"));

    return match;
  }
}

package com.test.train.match;

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
}

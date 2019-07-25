package com.test.train.match;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于AI模型训练的所有Key.
 */
public enum TrainKey {

  // 胜值, 0或者1
  VICTORY_VALUE("VICTORY_VALUE", match -> (match.mHostScore - match.mCustomScore) > 0 ? 1f : 0),
  // 平值, 0或者1
  DRAW_VALUE("DRAW_VALUE", match -> (match.mHostScore - match.mCustomScore == 0) ? 1f : 0),
  // 负值，0或者1
  DEFEAT_VALUE("DEFEAT_VALUE", match -> (match.mHostScore - match.mCustomScore < 0) ? 1f : 0),
  // 大球值，0或者1
  BALL_VICTORY_VALUE("BALL_VICTORY_VALUE",
      match -> (match.mHostScore + match.mCustomScore - match.mOpeningBigOdd) > 0 ? 1f : 0),
  // 走水值，0或者1
  BALL_DREW_VALUE("BALL_DREW_VALUE",
      match -> (match.mHostScore + match.mCustomScore - match.mOpeningBigOdd) == 0 ? 1f : 0),
  // 小球值，0或者1
  BALL_DEFEAT_VALUE("BALL_DEFEAT_VALUE",
      match -> (match.mHostScore + match.mCustomScore - match.mOpeningBigOdd) < 0 ? 1f : 0),

  // 让胜值, 0或者1
  ODD_VICTORY_VALUE("ODD_VICTORY_VALUE",
      match -> (match.mHostScore - match.mCustomScore + match.mOpeningScoreOdd) > 0 ? 1f : 0),
  // 让走值, 0或者1
  ODD_DRAW_VALUE("ODD_DRAW_VALUE",
      match -> (match.mHostScore - match.mCustomScore + match.mOpeningScoreOdd) == 0 ? 1f : 0),
  // 让负值, 0或者1
  ODD_DEFEAT_VALUE("ODD_DEFEAT_VALUE",
      match -> (match.mHostScore - match.mCustomScore + match.mOpeningScoreOdd) < 0 ? 1f : 0),

  // 近3场交战历史，主胜率
  HISTORY_HOST_VICTORY_RATE("HISTORY_HOST_VICTORY_RATE", new HistoryHostVictoryRateCal()),
  // 近3场交战历史，主让胜率
  HISTORY_HOST_ODD_VICTORY_RATE("HISTORY_HOST_ODD_VICTORY_RATE",
      new HistoryHostOddVictoryRateCal()),
  // 主队近3场胜率
  RECENT_HOST_VICTORY_RATE("RECENT_HOST_VICTORY_RATE", new RecentHostVictoryRateCal()),
  // 客队近3场胜率
  RECENT_CUSTOM_VICTORY_RATE("RECENT_CUSTOM_VICTORY_RATE", new RecentHostVictoryRateCal()),
  // 主队近3场胜率 - 客队近3场胜率
  RECENT_DISTANCE_VICTORY_RATE("RECENT_DISTANCE_VICTORY_RATE",
      match -> new RecentHostVictoryRateCal().compute(match)
          - new RecentCustomVictoryRateCal().compute(match)),

  // 主队近3场让胜率
  RECENT_HOST_ODD_VICTORY_RATE("RECENT_HOST_ODD_VICTORY_RATE", new RecentHostOddVictoryRateCal()),
  // 客队近3场让胜率
  RECENT_CUSTOM_ODD_VICTORY_RATE("RECENT_CUSTOM_ODD_VICTORY_RATE",
      new RecentCustomOddVictoryRateCal()),
  // 主队近3场让胜率 - 客队近3场让胜率
  RECENT_DISTANCE_ODD_VICTORY_RATE("RECENT_DISTANCE_ODD_VICTORY_RATE",
      match -> new RecentHostOddVictoryRateCal().compute(match)
          - new RecentCustomOddVictoryRateCal().compute(match)),

  // 主队主场联赛排名
  HOST_LEAGUE_RANK("HOST_LEAGUE_RANK", match -> match.mHostLeagueRank),
  // 客队客场联赛排名
  CUSTOM_LEAGUE_RANK("CUSTOM_LEAGUE_RANK", match -> match.mCustomLeagueRank),
  // 主队主场联赛排名 - 客队客场联赛排名
  DISTANCE_LEAGUE_RANK("DISTANCE_LEAGUE_RANK",
      match -> match.mHostLeagueRank - match.mCustomLeagueRank),

  // 主队近3场进球数
  RECENT_HOST_BALL_COUNT("RECENT_HOST_BALL_COUNT", match -> match.mHostScoreOf3),
  // 客队近3场进球数
  RECENT_CUSTOM_BALL_COUNT("RECENT_CUSTOM_BALL_COUNT", match -> match.mCustomScoreOf3),
  // 主队近3场进球数 - 客队近3场进球数
  DISTANCE_RECENT_BALL_COUNT("DISTANCE_RECENT_BALL_COUNT",
      match -> match.mHostScoreOf3 - match.mCustomScoreOf3),

  // 主队近3场丢球数
  RECENT_HOST_LOST_COUNT("RECENT_HOST_LOST_COUNT", match -> match.mHostLossOf3),
  // 客队近3场丢球数
  RECENT_CUSTOM_LOST_COUNT("RECENT_CUSTOM_LOST_COUNT", match -> match.mCustomLossOf3),
  // 主队近3场丢球数 - 客队近3场丢球数
  DISTANCE_RECENT_LOST_COUNT("DISTANCE_RECENT_LOST_COUNT",
      match -> match.mHostLossOf3 - match.mCustomLossOf3),

  // 主队近3场控球率
  RECENT_HOST_CONTROL_RATE("RECENT_HOST_CONTROL_RATE", match -> match.mHostControlRateOf3),
  // 客队近3场控球率
  RECENT_CUSTOM_CONTROL_RATE("RECENT_CUSTOM_CONTROL_RATE", match -> match.mCustomControlRateOf3),
  // 主队近3场控球率 - 客队近3场控球率
  DISTANCE_RECENT_CONTROL_RATE("DISTANCE_RECENT_CONTROL_RATE",
      match -> match.mHostControlRateOf3 - match.mCustomControlRateOf3),

  // 主队近3场角球数
  RECENT_HOST_CORNER_COUNT("RECENT_HOST_CORNER_COUNT", match -> match.mHostCornerOf3),
  // 客队近3场角球数
  RECENT_CUSTOM_CORNER_COUNT("RECENT_CUSTOM_CORNER_COUNT", match -> match.mCustomCornerOf3),
  // 主队近3场角球数 - 客队近3场角球数
  DISTANCE_RECENT_CORNER_COUNT("DISTANCE_RECENT_CORNER_COUNT",
      match -> match.mHostCornerOf3 - match.mCustomCornerOf3),

  // 亚盘转换为欧盘权值
  ORIGINAL_SCORE_ODD_FIXED("ORIGINAL_SCORE_ODD_FIXED",
      match -> (1 + match.mOriginalScoreOddOfVictory * 10.00f / (11 - match.mOriginalScoreOdd))),
  // 初盘让球盘口
  ORIGINAL_SCORE_ODD("ORIGINAL_SCORE_ODD", match -> match.mOriginalScoreOdd),
  // 初盘让球上盘水位
  ORIGINAL_SCORE_ODD_OF_VICTORY("ORIGINAL_SCORE_ODD_OF_VICTORY",
      match -> match.mOriginalScoreOddOfVictory),
  // // 初盘让球下盘水位
  ORIGINAL_SCORE_ODD_OF_DEFEAT("ORIGINAL_SCORE_ODD_OF_DEFEAT",
      match -> match.mOriginalScoreOddOfDefeat),
  // 开盘让球盘口
  OPENING_SCORE_ODD("OPENING_SCORE_ODD", match -> match.mOpeningScoreOdd),
  // 开盘让球上盘水位
  OPENING_SCORE_ODD_OF_VICTORY("OPENING_SCORE_ODD_OF_VICTORY",
      match -> match.mOpeningScoreOddOfDefeat),
  // 让球盘口变化
  DELTA_SCORE_ODD("DELTA_SCORE_ODD", match -> match.mOpeningScoreOdd - match.mOriginalScoreOdd),

  // 欧指初盘胜赔
  ORIGINAL_VICTORY_ODD("ORIGINAL_VICTORY_ODD", match -> match.mOriginalVictoryOdd),
  // 欧指初盘平赔
  ORIGINAL_DRAW_ODD("ORIGINAL_DRAW_ODD", match -> match.mOriginalDrawOdd),
  // 欧指初盘负赔
  ORIGINAL_DEFEAT_ODD("ORIGINAL_DEFEAT_ODD", match -> match.mOriginalDefeatOdd),
  // 欧指初盘胜赔变化
  DELTA_VICTORY_ODD("DELTA_VICTORY_ODD",
      match -> match.mOpeningVictoryOdd - match.mOriginalVictoryOdd),
  // 欧指初盘平赔变化
  DELTA_DRAW_ODD("DELTA_DRAW_ODD", match -> match.mOpeningDrawOdd - match.mOriginalDrawOdd),
  // 欧指初盘负赔变化
  DELTA_DEFEAT_ODD("DELTA_DEFEAT_ODD", match -> match.mOriginalDefeatOdd - match.mOpeningDefeatOdd),

  // 初盘大小球盘口
  ORIGINAL_BIG_ODD("ORIGINAL_BIG_ODD", match -> match.mOriginalBigOdd),
  // 初盘大球赔率
  ORIGINAL_BIG_ODD_OF_VICTORY("ORIGINAL_BIG_ODD_OF_VICTORY",
      match -> match.mOriginalBigOddOfVictory),
  // 初盘小球赔率
  ORIGINAL_BIG_ODD_OF_DEFEAT("ORIGINAL_BIG_ODD_OF_DEFEAT", match -> match.mOriginalBigOddOfDefeat),
  // 开盘大小球盘口
  OPENING_BIG_ODD("OPENING_BIG_ODD", match -> match.mOpeningBigOdd),
  // 开盘大球赔率
  OPENING_BIG_ODD_OF_VICTORY("OPENING_BIG_ODD_OF_VICTORY", match -> match.mOpeningBigOddOfVictory),
  // 开盘小球赔率
  OPENING_BIG_ODD_OF_DEFEAT("OPENING_BIG_ODD_OF_DEFEAT", match -> match.mOpeningBigOddOfDefeat),
  // 开盘大小球盘口变化
  DELTA_BIG_ODD("DELTA_BIG_ODD", match -> match.mOpeningBigOdd - match.mOriginalBigOdd),
  // 75分钟之后大球的值
  BIG_BALL_OF_MIN75_VALUE("BIG_BALL_OF_MIN75_VALUE", match -> match.mHostScore + match.mCustomScore
      - match.mHostScoreMinOf75 - match.mCustomScoreMinOf75 > 0 ? 1 : 0),
  // 75分钟大小球盘口差距
  BIG_BALL_ODD_DISTANCE_OF_MIN_75("BIG_BALL_ODD_DISTANCE_OF_MIN_75",
      match -> match.mOriginalBigOdd - match.mBigOddOfMinOfMin75),
  // 75分钟盘口让球差距
  SCORE_ODD_DISTANCE_OF_MIN_75("SCORE_ODD_DISTANCE_OF_MIN_75",
      match -> (match.mHostScoreMinOf75 - match.mCustomScoreMinOf75) + match.mOriginalScoreOdd),
  // 75分钟大小球赔率
  BIG_BALL_ODD_VICTORY_OF_MIN75("BIG_BALL_ODD_VICTORY_OF_MIN75",
      match -> match.mBigOddOfVictoryOfMin75),
  // 75分钟大小球赔率
  BIG_BALL_ODD_DEFEAT_OF_MIN75("BIG_BALL_ODD_DEFEAT_OF_MIN75",
      match -> match.mBigOddOfDefeatOfMin75),
  // 75分钟总射正次数
  TOTAL_BEST_SHOOT_OF_MIN75("TOTAL_BEST_SHOOT_OF_MIN75",
      match -> (match.mHostBestShoot + match.mCustomBestShoot) * 1f),
  // 75分钟总角球次数
  TOTAL_CORNER_OF_MIN75("TOTAL_CORNER_OF_MIN75",
      match -> (match.mHostCornerScore + match.mCustomCornerScore) * 1f),


  // 70分钟之后大球的值
  BIG_BALL_OF_MIN70_VALUE("BIG_BALL_OF_MIN70_VALUE", match -> match.mHostScore + match.mCustomScore
      - match.mHostScoreMinOf70 - match.mCustomScoreMinOf70 > 0 ? 1 : 0),
  // 70分钟大小球盘口差距
  BIG_BALL_ODD_DISTANCE_OF_MIN_70("BIG_BALL_ODD_DISTANCE_OF_MIN_70",
      match -> match.mOriginalBigOdd - match.mBigOddOfMinOfMin75),
  // 70分钟盘口让球差距
  SCORE_ODD_DISTANCE_OF_MIN_70("SCORE_ODD_DISTANCE_OF_MIN_70",
      match -> (match.mHostScoreMinOf70 - match.mCustomScoreMinOf70) + match.mOriginalScoreOdd),
  // 70分钟大小球赔率
  BIG_BALL_ODD_VICTORY_OF_MIN70("BIG_BALL_ODD_VICTORY_OF_MIN70",
      match -> match.mBigOddOfVictoryOfMin70),
  // 75分钟大小球赔率
  BIG_BALL_ODD_DEFEAT_OF_MIN70("BIG_BALL_ODD_DEFEAT_OF_MIN70",
      match -> match.mBigOddOfDefeatOfMin70),
  // 75分钟总射正次数
  TOTAL_BEST_SHOOT_OF_MIN70("TOTAL_BEST_SHOOT_OF_MIN70",
      match -> (match.mHostBestShoot + match.mCustomBestShoot) * 1),
  // 75分钟总角球次数
  TOTAL_CORNER_OF_MIN70("TOTAL_CORNER_OF_MIN70",
      match -> (match.mHostCornerScore + match.mCustomCornerScore) * 1),


  // 25分钟之后大球的值
  BIG_BALL_OF_MIN25_VALUE("BIG_BALL_OF_MIN25_VALUE",
      match -> match.mHostScoreOfMiddle + match.mCustomScoreOfMiddle
          - match.mHostScoreMinOf25 - match.mCustomScoreMinOf25 > 0 ? 1 : 0),
  // 25分钟总进球数
  TOTAL_BALL_COUNT_OF_MIN_25("TOTAL_BALL_COUNT_OF_MIN_25",
      match -> match.mHostScoreMinOf25 + match.mCustomScoreMinOf25),
  // 25分钟盘口让球差距
  SCORE_ODD_DISTANCE_OF_MIN_25("SCORE_ODD_DISTANCE_OF_MIN_25",
      match -> (match.mHostScoreMinOf25 - match.mCustomScoreMinOf25) + match.mOriginalScoreOdd),
  // 25分钟大小球赔率
  BIG_BALL_ODD_VICTORY_OF_MIN25("BIG_BALL_ODD_VICTORY_OF_MIN25",
      match -> match.mBigOddOfVictoryOfMin25);

  public final String mKey;
  public final Calculator mCalculator;

  TrainKey(java.lang.String key, Calculator calculator) {
    mKey = key;
    mCalculator = calculator;
  }

  public static List<TrainKey> helpfulKeys() {
    List<TrainKey> trainKeys = new ArrayList<>();
    // trainKeys.add(DISTANCE_RECENT_BALL_COUNT);
    // trainKeys.add(DISTANCE_RECENT_LOST_COUNT);
    // trainKeys.add(DISTANCE_RECENT_CONTROL_RATE);
    trainKeys.add(ORIGINAL_SCORE_ODD_FIXED);
    // trainKeys.add(DELTA_SCORE_ODD);
    trainKeys.add(ORIGINAL_VICTORY_ODD);
    trainKeys.add(ORIGINAL_DRAW_ODD);
    // trainKeys.add(ORIGINAL_DEFEAT_ODD);
    trainKeys.add(DELTA_VICTORY_ODD);
    trainKeys.add(DELTA_DRAW_ODD);
    // trainKeys.add(DELTA_DEFEAT_ODD);
    // trainKeys.add(ORIGINAL_BIG_ODD);
    // trainKeys.add(DELTA_BIG_ODD);

    return trainKeys;
  }

  static class HistoryHostVictoryRateCal implements Calculator {
    @Override
    public float compute(Match match) {
      return 0;
    }
  }

  static class HistoryHostOddVictoryRateCal implements Calculator {
    @Override
    public float compute(Match match) {
      return 0;
    }
  }

  static class RecentHostVictoryRateCal implements Calculator {
    @Override
    public float compute(Match match) {
      return 0;
    }
  }

  static class RecentCustomVictoryRateCal implements Calculator {
    @Override
    public float compute(Match match) {
      return 0;
    }
  }

  static class RecentHostOddVictoryRateCal implements Calculator {
    @Override
    public float compute(Match match) {
      return 0;
    }
  }

  static class RecentCustomOddVictoryRateCal implements Calculator {
    @Override
    public float compute(Match match) {
      return 0;
    }
  }
}

package com.test.train.tools;

import org.apache.http.util.TextUtils;

/**
 * 用于AI模型训练的所有Key.
 */
public enum MappedValue {

  // 胜值, 0或者1
  VICTORY_VALUE(match -> (match.mHostScore - match.mCustomScore) > 0 ? 1f : 0),
  // 平值, 0或者1
  DRAW_VALUE(match -> (match.mHostScore - match.mCustomScore == 0) ? 1f : 0),
  // 负值，0或者1
  DEFEAT_VALUE(match -> (match.mHostScore - match.mCustomScore < 0) ? 1f : 0),
  // 大球值，0或者1
  BALL_VICTORY_VALUE(
      match -> (match.mHostScore + match.mCustomScore - match.mOpeningBigOdd) > 0 ? 1f : 0),
  // 走水值，0或者1
  BALL_DREW_VALUE(
      match -> (match.mHostScore + match.mCustomScore - match.mOpeningBigOdd) == 0 ? 1f : 0),
  // 小球值，0或者1
  BALL_DEFEAT_VALUE(
      match -> (match.mHostScore + match.mCustomScore - match.mOpeningBigOdd) < 0 ? 1f : 0),

  // 让胜值, 0或者1
  ODD_VICTORY_VALUE(
      match -> (match.mHostScore - match.mCustomScore + match.mOpeningScoreOdd) > 0 ? 1f : 0),
  // 让走值, 0或者1
  ODD_DRAW_VALUE(
      match -> (match.mHostScore - match.mCustomScore + match.mOpeningScoreOdd) == 0 ? 1f : 0),
  // 让负值, 0或者1
  ODD_DEFEAT_VALUE(
      match -> (match.mHostScore - match.mCustomScore + match.mOpeningScoreOdd) < 0 ? 1f : 0),

  // 占位
  EMPTY(match -> 0f),

  // 主队主场联赛排名
  HOST_LEAGUE_RANK(match -> match.mHostLeagueRank),
  // 客队客场联赛排名
  CUSTOM_LEAGUE_RANK(match -> match.mCustomLeagueRank),
  // 主队主场联赛排名 - 客队客场联赛排名
  DISTANCE_LEAGUE_RANK(match -> match.mHostLeagueRank - match.mCustomLeagueRank),

  // 主队近3场进球数
  RECENT_HOST_BALL_COUNT(match -> match.mHostScoreOf3),
  // 客队近3场进球数
  RECENT_CUSTOM_BALL_COUNT(match -> match.mCustomScoreOf3),
  // 主队近3场进球数 - 客队近3场进球数
  DISTANCE_RECENT_BALL_COUNT(match -> match.mHostScoreOf3 - match.mCustomScoreOf3),

  // 主队近3场丢球数
  RECENT_HOST_LOST_COUNT(match -> match.mHostLossOf3),
  // 客队近3场丢球数
  RECENT_CUSTOM_LOST_COUNT(match -> match.mCustomLossOf3),
  // 主队近3场丢球数 - 客队近3场丢球数
  DISTANCE_RECENT_LOST_COUNT(match -> match.mHostLossOf3 - match.mCustomLossOf3),

  // 主队近3场控球率
  RECENT_HOST_CONTROL_RATE(match -> match.mHostControlRateOf3),
  // 客队近3场控球率
  RECENT_CUSTOM_CONTROL_RATE(match -> match.mCustomControlRateOf3),
  // 主队近3场控球率 - 客队近3场控球率
  DISTANCE_RECENT_CONTROL_RATE(match -> match.mHostControlRateOf3 - match.mCustomControlRateOf3),

  // 主队近3场角球数
  RECENT_HOST_CORNER_COUNT(match -> match.mHostCornerOf3),
  // 客队近3场角球数
  RECENT_CUSTOM_CORNER_COUNT(match -> match.mCustomCornerOf3),
  // 主队近3场角球数 - 客队近3场角球数
  DISTANCE_RECENT_CORNER_COUNT(match -> match.mHostCornerOf3 - match.mCustomCornerOf3),

  // 初盘让球盘口
  ORIGINAL_SCORE_ODD(match -> match.mOriginalScoreOdd),
  // 让球盘口绝对值
  ORIGINAL_SCORE_ODD_ABS(match -> Math.abs(match.mOriginalScoreOdd)),
  // 初盘让球上盘水位
  ORIGINAL_SCORE_ODD_OF_VICTORY(match -> match.mOriginalScoreOddOfVictory),
  // // 初盘让球下盘水位
  ORIGINAL_SCORE_ODD_OF_DEFEAT(match -> match.mOriginalScoreOddOfDefeat),
  // 开盘让球盘口
  OPENING_SCORE_ODD(match -> match.mOpeningScoreOdd),
  // 开盘让球上盘水位
  OPENING_SCORE_ODD_OF_VICTORY(match -> match.mOpeningScoreOddOfDefeat),
  // 让球盘口变化
  DELTA_SCORE_ODD(match -> match.mOpeningScoreOdd - match.mOriginalScoreOdd),

  // 欧指初盘胜赔
  ORIGINAL_VICTORY_ODD(match -> match.mOriginalVictoryOdd),
  // 欧指初盘平赔
  ORIGINAL_DRAW_ODD(match -> match.mOriginalDrawOdd),
  // 欧指初盘负赔
  ORIGINAL_DEFEAT_ODD(match -> match.mOriginalDefeatOdd),
  // 欧指初盘胜赔变化
  DELTA_VICTORY_ODD(match -> match.mOpeningVictoryOdd - match.mOriginalVictoryOdd),
  // 欧指初盘平赔变化
  DELTA_DRAW_ODD(match -> match.mOpeningDrawOdd - match.mOriginalDrawOdd),
  // 欧指初盘负赔变化
  DELTA_DEFEAT_ODD(match -> match.mOriginalDefeatOdd - match.mOpeningDefeatOdd),

  // 初盘大小球盘口
  ORIGINAL_BIG_ODD(match -> match.mOriginalBigOdd),
  // 初盘大球赔率
  ORIGINAL_BIG_ODD_OF_VICTORY(match -> match.mOriginalBigOddOfVictory),
  // 初盘小球赔率
  ORIGINAL_BIG_ODD_OF_DEFEAT(match -> match.mOriginalBigOddOfDefeat),
  // 开盘大小球盘口
  OPENING_BIG_ODD(match -> match.mOpeningBigOdd),
  // 开盘大球赔率
  OPENING_BIG_ODD_OF_VICTORY(match -> match.mOpeningBigOddOfVictory),
  // 开盘小球赔率
  OPENING_BIG_ODD_OF_DEFEAT(match -> match.mOpeningBigOddOfDefeat),
  // 开盘大小球盘口变化
  DELTA_BIG_ODD(match -> match.mOpeningBigOdd - match.mOriginalBigOdd),

  // 是否杯赛
  IS_CUP_MATCH(match -> match.mLeague != null && match.mLeague.contains("杯") ? 1f : 0),

  // 是否野鸡
  IS_YEJI_MATCH(match -> TextUtils.isEmpty(match.mLeague) || match.mLeague.equals("null") ? 1f : 0),

  // 70分钟之后大球的值
  BIG_BALL_OF_MIN70_VALUE(match -> match.mHostScore + match.mCustomScore
      - match.mHostScoreMinOf70 - match.mCustomScoreMinOf70 > 0 ? 1f : 0),
  // 70分钟主队得分
  HOST_SCORE_OF_MIN_70(match -> match.mHostScoreMinOf70),
  // 70分钟客队得分
  CUSTOM_SCORE_OF_MIN_70(match -> match.mCustomScoreMinOf70),
  // 70分钟大小球盘口
  BIG_BALL_ODD_OF_MIN_70(match -> match.mBigOddOfMin70),
  // 70分钟大小球赔率
  BIG_BALL_ODD_VICTORY_OF_MIN70(match -> match.mBigOddOfVictoryOfMin70),
  BIG_BALL_ODD_VICTORY_OF_MIN70_FIX(match -> {
    float delta = match.mBigOddOfMin70 - (int) match.mBigOddOfMin70; // 取整
    if (delta == 0) { // 当前比分+1球的盘口
      return match.mBigOddOfVictoryOfMin70 * 0.5f;
    } else if (delta == 0.75) {
      return match.mBigOddOfVictoryOfMin70 * 0.7f;
    } else {
      return match.mBigOddOfVictoryOfMin70;
    }
  }),

  // 大小球加权变化
  BIG_BALL_ODD_DELTA_OF_MIN70(
      match -> match.mOriginalBigOdd * 1.00f / match.mOriginalBigOddOfVictory -
          match.mBigOddOfMin70 * 1.00f / match.mBigOddOfVictoryOfMin70),

  // 70分钟让球盘口
  SCORE_ODD_OF_MIN_70(match -> match.mScoreOddOfMin70),
  // 70分钟让球盘口
  SCORE_ODD_VICTORY_OF_MIN_70(match -> match.mScoreOddOfVictoryOfMin70),
  // 70分钟大小球盘口和初盘差距
  BIG_BALL_ODD_DISTANCE_OF_MIN_70(
      match -> (match.mHostScoreMinOf70 + match.mCustomScoreMinOf70) - match.mOriginalBigOdd),
  // 70分钟盘口让球和初盘差距
  SCORE_ODD_DISTANCE_OF_MIN_70(
      match -> (match.mHostScoreMinOf70 - match.mCustomScoreMinOf70) + match.mOriginalScoreOdd),
  // 70分钟大小球盘口和中场的差距
  BIG_BALL_ODD_DISTANCE_TO_MIDDLE_OF_MIN_70(
      match -> (match.mHostScoreMinOf70 + match.mCustomScoreMinOf70) - match.mBigOddOfMiddle),
  // 70分钟盘口让球和中场的差距
  SCORE_ODD_DISTANCE_TO_MIDDLE_OF_MIN_70(
      match -> ((match.mHostScoreMinOf70 - match.mHostScoreOfMiddle)
          - (match.mCustomScoreMinOf70 - match.mCustomScoreOfMiddle)) + match.mMiddleScoreOdd),

  TOTAL_SCORE_OF_MIN_70(match -> match.mHostScoreMinOf70 + match.mCustomScoreMinOf70),

  // 70分钟大小球赔率
  BIG_BALL_ODD_DEFEAT_OF_MIN70(match -> match.mBigOddOfDefeatOfMin70),

  // 70分钟总射正次数
  TOTAL_BEST_SHOOT_OF_MIN70(match -> (match.mHostBestShoot + match.mCustomBestShoot) * 0.7f),
  // 70分钟总角球次数
  TOTAL_CORNER_OF_MIN70(match -> (match.mHostCornerScore + match.mCustomCornerScore) * 0.7f),

  // 25分钟之后大球的值
  BIG_BALL_OF_MIN25_VALUE(match -> match.mHostScoreOfMiddle + match.mCustomScoreOfMiddle
      - match.mHostScoreMinOf25 - match.mCustomScoreMinOf25 > 0 ? 1f : 0),
  // 25分钟总进球数
  TOTAL_BALL_COUNT_OF_MIN_25(match -> match.mHostScoreMinOf25 + match.mCustomScoreMinOf25),
  // 25分钟盘口让球差距
  SCORE_ODD_DISTANCE_OF_MIN_25(
      match -> (match.mHostScoreMinOf25 - match.mCustomScoreMinOf25) + match.mOriginalScoreOdd),
  // 25分钟大小球赔率
  BIG_BALL_ODD_VICTORY_OF_MIN25(match -> match.mBigOddOfVictoryOfMin25),

  // 大小球加权变化
  BIG_BALL_ODD_DELTA_OF_MIN25(
      match -> match.mOriginalBigOdd * 1.00f / match.mOriginalBigOddOfVictory -
          match.mBigOddOfMin25 * 1.00f / match.mBigOddOfVictoryOfMin25),
  // 25分钟小球赔率
  BIG_BALL_ODD_DEFEAT_OF_MIN25(match -> match.mBigOddOfDefeatOfMin25),

  // 25分钟总射正次数
  TOTAL_BEST_SHOOT_OF_MIN25(match -> (match.mHostBestShoot + match.mCustomBestShoot) * 0.3f);

  public final Mapper mMapper;

  MappedValue(Mapper mapper) {
    mMapper = mapper;
  }
}

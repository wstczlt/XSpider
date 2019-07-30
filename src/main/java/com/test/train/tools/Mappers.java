package com.test.train.tools;

/**
 * 用于AI模型训练的所有数值映射计算.
 */
public interface Mappers {

  // 占位
  Mapper ZERO = match -> 0f;

  // 大球值，0或者1
  Mapper BALL_VICTORY_VALUE =
      match -> (match.mHostScore + match.mCustomScore - match.mOpeningBigOdd) > 0 ? 1f : 0;
  // 走水值，0或者1
  Mapper BALL_DREW_VALUE =
      match -> (match.mHostScore + match.mCustomScore - match.mOpeningBigOdd) == 0 ? 1f : 0;
  // 让胜值, 0或者1
  Mapper ODD_VICTORY_VALUE =
      match -> (match.mHostScore - match.mCustomScore + match.mOpeningScoreOdd) > 0 ? 1f : 0;


  // 初盘让球盘口
  Mapper ORIGINAL_SCORE_ODD = match -> match.mOriginalScoreOdd;
  // 让球盘口绝对值
  Mapper ORIGINAL_SCORE_ODD_ABS = match -> Math.abs(match.mOriginalScoreOdd);
  // 初盘让球上盘水位
  Mapper ORIGINAL_SCORE_ODD_OF_VICTORY = match -> match.mOriginalScoreOddOfVictory;
  // // 初盘让球下盘水位
  Mapper ORIGINAL_SCORE_ODD_OF_DEFEAT = match -> match.mOriginalScoreOddOfDefeat;

  // 欧指初盘胜赔
  Mapper ORIGINAL_VICTORY_ODD = match -> match.mOriginalVictoryOdd;
  // 欧指初盘平赔
  Mapper ORIGINAL_DRAW_ODD = match -> match.mOriginalDrawOdd;
  // 欧指初盘负赔
  Mapper ORIGINAL_DEFEAT_ODD = match -> match.mOriginalDefeatOdd;
  // 欧指初盘胜赔变化
  Mapper DELTA_VICTORY_ODD = match -> match.mOpeningVictoryOdd - match.mOriginalVictoryOdd;
  // 欧指初盘平赔变化
  Mapper DELTA_DRAW_ODD = match -> match.mOpeningDrawOdd - match.mOriginalDrawOdd;
  // 欧指初盘负赔变化
  Mapper DELTA_DEFEAT_ODD = match -> match.mOriginalDefeatOdd - match.mOpeningDefeatOdd;

  // 初盘大小球盘口
  Mapper ORIGINAL_BIG_ODD = match -> match.mOriginalBigOdd;
  // 初盘大球赔率
  Mapper ORIGINAL_BIG_ODD_OF_VICTORY = match -> match.mOriginalBigOddOfVictory;
  // 初盘小球赔率
  Mapper ORIGINAL_BIG_ODD_OF_DEFEAT = match -> match.mOriginalBigOddOfDefeat;
  // 开盘大小球盘口变化
  Mapper DELTA_BIG_ODD = match -> match.mOpeningBigOdd - match.mOriginalBigOdd;

  // 70分钟之后大球的值
  Mapper BIG_BALL_OF_MIN70_VALUE = match -> match.mHostScore + match.mCustomScore
      - match.mHostScoreMinOf70 - match.mCustomScoreMinOf70 > 0 ? 1f : 0;

  interface Mapper {

    float val(Match match);
  }
}

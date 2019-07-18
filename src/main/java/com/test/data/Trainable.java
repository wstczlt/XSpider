package com.test.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 描述训练模型.
 */
public abstract class Trainable {

  public static final String VICTORY_VALUE = "VICTORY_VALUE"; // 胜值, 0或者1
  public static final String DRAW_VALUE = "DRAW_VALUE"; // 平值, 0或者1
  public static final String DEFEAT_VALUE = "DEFEAT_VALUE"; // 负值，0或者1
  public static final String BALL_COUNT_VALUE = "BALL_COUNT_VALUE"; // 总进球数

  public static final String ODD_VICTORY_VALUE = "ODD_VICTORY_VALUE"; // 让胜值, 0或者1
  public static final String ODD_DRAW_VALUE = "ODD_DRAW_VALUE"; // 让走值, 0或者1
  public static final String ODD_DEFEAT_VALUE = "ODD_DEFEAT_VALUE"; // 让负值, 0或者1

  public static final String ORIGINAL_SCORE_ODD = "ORIGINAL_SCORE_ODD"; // 亚盘初盘让球盘口
  public static final String ORIGINAL_SCORE_ODD_OF_VICTORY = "ORIGINAL_SCORE_ODD_OF_VICTORY"; // 亚盘初盘让球主队赔率
  public static final String ORIGINAL_SCORE_ODD_OF_DEFEAT = "ORIGINAL_SCORE_ODD_OF_DEFEAT"; // 亚盘初盘让球客队赔率

  public static final String ORIGINAL_BIG_ODD = "ORIGINAL_BIG_ODD"; // 初盘大小球盘口
  public static final String ORIGINAL_BIG_ODD_OF_VICTORY = "ORIGINAL_BIG_ODD_OF_VICTORY"; // 初盘大球赔率
  public static final String ORIGINAL_BIG_ODD_OF_DEFEAT = "ORIGINAL_BIG_ODD_OF_DEFEAT"; // 初盘小球赔率

  public static final String ORIGINAL_VICTORY_ODD = "ORIGINAL_VICTORY_ODD"; // 欧指初盘胜赔
  public static final String ORIGINAL_DRAW_ODD = "ORIGINAL_DRAW_ODD"; // 欧指初盘平赔
  public static final String ORIGINAL_DEFEAT_ODD = "ORIGINAL_DEFEAT_ODD"; // 欧指初盘负赔

  public static final String DELTA_VICTORY_ODD = "DELTA_VICTORY_ODD"; // 胜赔变化
  public static final String DELTA_DRAW_ODD = "DELTA_DRAW_ODD"; // 平赔变化
  public static final String DELTA_DEFEAT_ODD = "DELTA_DEFEAT_ODD"; // 负赔变化

  protected final Map<String, Float> mValues = new HashMap<>();

  public final void setValues(Map<String, Float> values) {
    mValues.clear();
    mValues.putAll(values);
  }

  /**
   * 需要训练的数据集key
   */
  public abstract List<String> trainKeys();

  /**
   * 训练集的结果集的key
   */
  public abstract String trainValue();

  /**
   * 计算预期值.
   */
  public abstract float computeValue();

  /**
   * 计算该场次比赛收益.
   */
  public abstract float computeProfit();

  /**
   * 判断是否是正向样本.
   */
  public abstract boolean isPositive();

  /**
   * 用于产生数据集.
   */
  public final String toX() {
    return toValue(trainKeys(), mValues);
  }

  /**
   * 用于产生数据集.
   */
  public final String toY() {
    return toValue(Collections.singletonList(trainValue()), mValues);
  }

  private static String toValue(List<String> keys, Map<String, Float> values) {
    List<String> list = new ArrayList<>();
    for (String key : keys) {
      list.add(String.format("%.2f", values.get(key)));
    }

    return StringUtils.join(list, "   ");
  }

  public static Map<String, Float> toMap(Match match) {
    Map<String, Float> values = new HashMap<>();
    if (isLegal(match)) {
      values.put(VICTORY_VALUE, (match.mHostScore - match.mCustomScore) > 0 ? 1f : 0);
      values.put(DRAW_VALUE, (match.mHostScore - match.mCustomScore) == 0 ? 1f : 0);
      values.put(DEFEAT_VALUE, (match.mHostScore - match.mCustomScore) < 0 ? 1f : 0);
      values.put(BALL_COUNT_VALUE, (float) (match.mHostScore + match.mCustomScore));
      values.put(ODD_VICTORY_VALUE,
          (match.mHostScore - match.mCustomScore + match.mOriginalScoreOdd) > 0 ? 1f : 0);
      values.put(ODD_DRAW_VALUE,
          (match.mHostScore - match.mCustomScore + match.mOriginalScoreOdd) == 0 ? 1f : 0);
      values.put(ODD_DEFEAT_VALUE,
          (match.mHostScore - match.mCustomScore + match.mOriginalScoreOdd) < 0 ? 1f : 0);
      values.put(ORIGINAL_SCORE_ODD, match.mOriginalScoreOdd);
      values.put(ORIGINAL_SCORE_ODD_OF_VICTORY, match.mOriginalScoreOddOfVictory);
      values.put(ORIGINAL_SCORE_ODD_OF_DEFEAT, match.mOriginalScoreOddOfDefeat);
      values.put(ORIGINAL_BIG_ODD, match.mOriginalBigOdd);
      values.put(ORIGINAL_BIG_ODD_OF_VICTORY, match.mOriginalBigOddOfVictory);
      values.put(ORIGINAL_BIG_ODD_OF_DEFEAT, match.mOriginalBigOddOfDefeat);
      values.put(ORIGINAL_VICTORY_ODD, match.mOriginalVictoryOdd);
      values.put(ORIGINAL_DRAW_ODD, match.mOriginalDrawOdd);
      values.put(ORIGINAL_DEFEAT_ODD, match.mOriginalDefeatOdd);
      values.put(DELTA_VICTORY_ODD, match.mOriginalVictoryOdd - match.mOpeningVictoryOdd);
      values.put(DELTA_DRAW_ODD, match.mOriginalDrawOdd - match.mOpeningDrawOdd);
      values.put(DELTA_DEFEAT_ODD, match.mOriginalDefeatOdd - match.mOpeningDefeatOdd);
    }

    return values;
  }

  private static boolean isLegal(Match match) {
    return match.mOriginalVictoryOdd > 0
        && match.mOriginalDrawOdd > 0
        && match.mOriginalDefeatOdd > 0
        && match.mOriginalScoreOddOfVictory > 0
        && match.mOriginalScoreOddOfDefeat > 0
        && match.mOpeningVictoryOdd > 0
        && match.mOpeningDrawOdd > 0
        && match.mOpeningDefeatOdd > 0
        && match.mOriginalBigOdd > 0
        && match.mOriginalBigOddOfVictory > 0
        && match.mOriginalBigOddOfDefeat > 0;
  }

}

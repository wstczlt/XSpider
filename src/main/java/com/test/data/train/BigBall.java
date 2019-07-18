package com.test.data.train;

import java.util.ArrayList;
import java.util.List;

import com.test.data.Trainable;

/**
 * 大球训练模型.
 */
public class BigBall extends Trainable {

  // 训练集
  private static final List<String> TRAIN_KEYS = new ArrayList<>();
  static {
    TRAIN_KEYS.add(ORIGINAL_SCORE_ODD); // 初盘让球盘口
    TRAIN_KEYS.add(ORIGINAL_SCORE_ODD_OF_VICTORY); // 初盘让球主队赔率
    TRAIN_KEYS.add(ORIGINAL_SCORE_ODD_OF_DEFEAT); // 初盘让球客队赔率
    TRAIN_KEYS.add(ORIGINAL_BIG_ODD); // 初盘大小球
    TRAIN_KEYS.add(ORIGINAL_BIG_ODD_OF_VICTORY); // 初盘大球赔率
    TRAIN_KEYS.add(ORIGINAL_BIG_ODD_OF_DEFEAT); // 初盘小球赔率
    TRAIN_KEYS.add(ORIGINAL_VICTORY_ODD); // 初盘欧盘胜赔
    TRAIN_KEYS.add(ORIGINAL_DRAW_ODD); // 初盘欧盘平赔
    TRAIN_KEYS.add(ORIGINAL_DEFEAT_ODD); // 初盘欧盘负赔
    TRAIN_KEYS.add(DELTA_VICTORY_ODD); // 临场欧盘胜赔变化
    TRAIN_KEYS.add(DELTA_DRAW_ODD); // 临场欧盘平赔变化
    TRAIN_KEYS.add(DELTA_DEFEAT_ODD); // 临场欧盘负赔变化
  }

  // 训练出来的权重系数
  private static final double[] TRAIN_WEIGHT =
      new double[] {-0.2327718, -0.315221, -0.09945758, 0.87020046, 1.1548071, 1.7634715,
          0.09812142, 0.03829395, -0.01923525, 0.02837913, -0.46690947, 0.11287733};
  // 训练出来的常量
  private static final double TRAIN_CONST = -2.158255;

  @Override
  public List<String> trainKeys() {
    return TRAIN_KEYS;
  }

  @Override
  public String trainValue() {
    return BALL_COUNT_VALUE;
  }

  @Override
  public float computeValue() {
    double y = TRAIN_CONST;
    for (int i = 0; i < TRAIN_KEYS.size(); i++) {
      Float value = mValues.get(TRAIN_KEYS.get(i));
      if (value == null) {
        return -1;
      }
      y += value * TRAIN_WEIGHT[i];
    }

    return (float) y;
  }

  @Override
  public float computeProfit() {
    float ballCount = mValues.get(BALL_COUNT_VALUE);
    float originOddCount = mValues.get(ORIGINAL_BIG_ODD);
    float odd = mValues.get(ORIGINAL_BIG_ODD_OF_VICTORY);
    boolean isVictory = ballCount > originOddCount;
    boolean isDrew = ballCount == originOddCount; // 走水
    return isVictory ? odd : (isDrew ? 0 : -1); // 用初盘水位计算
  }

  @Override
  public boolean isPositive() {
    float ballCount = mValues.get(ORIGINAL_BIG_ODD);
    return computeValue() >= (ballCount / 0.9f); // 预期进球数大于初盘平衡
  }
}

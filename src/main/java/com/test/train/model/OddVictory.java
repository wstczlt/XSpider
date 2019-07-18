package com.test.train.model;

import java.util.ArrayList;
import java.util.List;

import com.test.train.TrainModel;

/**
 * 让球胜训练模型.
 */
public class OddVictory extends TrainModel {

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
      new double[] {0.08320605, 0.04103623, 0.27885258, 0.0009057, -0.19773558, -0.29755065,
          -0.01712915, 0.00851083, 0.01462657, 0.05188101, -0.00913558, -0.02969478};
  // 训练出来的常量
  private static final double TRAIN_CONST = 0.594880;

  @Override
  public List<String> trainKeys() {
    return TRAIN_KEYS;
  }

  @Override
  public String trainValue() {
    return ODD_VICTORY_VALUE;
  }

  @Override
  public float computeValue() {
    double prop = TRAIN_CONST;
    for (int i = 0; i < TRAIN_KEYS.size(); i++) {
      Float value = mValues.get(TRAIN_KEYS.get(i));
      if (value == null) {
        return -1;
      }
      prop += value * TRAIN_WEIGHT[i];
    }

    return (float) prop;
  }

  @Override
  public float computeProfit() {
    boolean isVictory = mValues.get(ODD_VICTORY_VALUE) == 1;
    boolean isDrew = mValues.get(ODD_DRAW_VALUE) == 1; // 走水
    return isVictory ? mValues.get(ORIGINAL_SCORE_ODD_OF_VICTORY) : (isDrew ? 0 : -1); // 用初盘水位计算
  }

  @Override
  public boolean isPositive() {
    return computeValue() >= 0.5f;
  }
}

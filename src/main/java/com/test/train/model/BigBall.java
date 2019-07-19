package com.test.train.model;

import java.util.ArrayList;
import java.util.List;

import com.test.train.TrainModel;

/**
 * 大球训练模型.
 */
public class BigBall extends TrainModel {

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

  @Override
  public String name() {
    return "bigBall";
  }

  @Override
  public List<String> keyOfX() {
    return TRAIN_KEYS;
  }

  @Override
  public String keyOfY() {
    return BALL_VICTORY_VALUE;
  }
}

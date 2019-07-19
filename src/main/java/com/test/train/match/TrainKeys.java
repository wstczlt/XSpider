package com.test.train.match;

/**
 * 用于AI模型训练的所有Key.
 */
public interface TrainKeys {

  String VICTORY_VALUE = "VICTORY_VALUE"; // 胜值, 0或者1
  String DRAW_VALUE = "DRAW_VALUE"; // 平值, 0或者1
  String DEFEAT_VALUE = "DEFEAT_VALUE"; // 负值，0或者1

  String BALL_VICTORY_VALUE = "BALL_VICTORY_VALUE"; // 大球值，0或者1
  String BALL_DREW_VALUE = "BALL_DREW_VALUE"; // 走水值，0或者1
  String BALL_DEFEAT_VALUE = "BALL_DEFEAT_VALUE"; // 小球值，0或者1

  String ODD_VICTORY_VALUE = "ODD_VICTORY_VALUE"; // 让胜值, 0或者1
  String ODD_DRAW_VALUE = "ODD_DRAW_VALUE"; // 让走值, 0或者1
  String ODD_DEFEAT_VALUE = "ODD_DEFEAT_VALUE"; // 让负值, 0或者1

  String ORIGINAL_SCORE_ODD = "ORIGINAL_SCORE_ODD"; // 亚盘初盘让球盘口
  String ORIGINAL_SCORE_ODD_OF_VICTORY = "ORIGINAL_SCORE_ODD_OF_VICTORY"; // 亚盘初盘让球主队赔率
  String ORIGINAL_SCORE_ODD_OF_DEFEAT = "ORIGINAL_SCORE_ODD_OF_DEFEAT"; // 亚盘初盘让球客队赔率

  String ORIGINAL_BIG_ODD = "ORIGINAL_BIG_ODD"; // 初盘大小球盘口
  String ORIGINAL_BIG_ODD_OF_VICTORY = "ORIGINAL_BIG_ODD_OF_VICTORY"; // 初盘大球赔率
  String ORIGINAL_BIG_ODD_OF_DEFEAT = "ORIGINAL_BIG_ODD_OF_DEFEAT"; // 初盘小球赔率

  String ORIGINAL_VICTORY_ODD = "ORIGINAL_VICTORY_ODD"; // 欧指初盘胜赔
  String ORIGINAL_DRAW_ODD = "ORIGINAL_DRAW_ODD"; // 欧指初盘平赔
  String ORIGINAL_DEFEAT_ODD = "ORIGINAL_DEFEAT_ODD"; // 欧指初盘负赔

  String DELTA_VICTORY_ODD = "DELTA_VICTORY_ODD"; // 胜赔变化
  String DELTA_DRAW_ODD = "DELTA_DRAW_ODD"; // 平赔变化
  String DELTA_DEFEAT_ODD = "DELTA_DEFEAT_ODD"; // 负赔变化

}

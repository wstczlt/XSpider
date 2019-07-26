package com.test.train.model;

import static com.test.train.match.QueryHelper.SQL_BASE;
import static com.test.train.match.QueryHelper.SQL_MIN_70;
import static com.test.train.match.QueryHelper.SQL_ORDER;
import static com.test.train.match.TrainKey.BIG_BALL_ODD_DEFEAT_OF_MIN70;
import static com.test.train.match.TrainKey.BIG_BALL_ODD_DISTANCE_OF_MIN_70;
import static com.test.train.match.TrainKey.BIG_BALL_ODD_VICTORY_OF_MIN70;
import static com.test.train.match.TrainKey.BIG_BALL_OF_MIN70_VALUE;
import static com.test.train.match.TrainKey.ORIGINAL_BIG_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_SCORE_ODD;
import static com.test.train.match.TrainKey.SCORE_ODD_DISTANCE_OF_MIN_70;
import static com.test.train.match.TrainKey.TOTAL_BEST_SHOOT_OF_MIN70;
import static com.test.train.match.TrainKey.TOTAL_CORNER_OF_MIN70;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.test.train.TrainModel;
import com.test.train.match.TrainKey;

public class BallOfMin70 extends TrainModel {

  public static Map<Integer, String> BIG_BALL_OF_MIN_70_RATE_MAP = new HashMap<>();

  static { // 训练测试结果
    BIG_BALL_OF_MIN_70_RATE_MAP.put(50, "60%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(51, "62%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(52, "64%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(53, "65%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(54, "66%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(55, "66%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(56, "68%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(57, "69%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(58, "70%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(59, "71%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(60, "72%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(61, "73%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(62, "74%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(63, "76%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(64, "78%");
    BIG_BALL_OF_MIN_70_RATE_MAP.put(65, "80%");
  }

  @Override
  public String name() {
    return "bigBallOfMin70";
  }

  @Override
  public String buildQuerySql() {
    return SQL_BASE + SQL_MIN_70 + SQL_ORDER;
  }

  @Override
  public List<TrainKey> keyOfX() {
    List<TrainKey> trainKeys = new ArrayList<>();
    trainKeys.add(ORIGINAL_BIG_ODD); // 大小球初盘
    trainKeys.add(ORIGINAL_SCORE_ODD); // 让球初盘

    trainKeys.add(BIG_BALL_ODD_DISTANCE_OF_MIN_70); // 70分钟大小球和初盘差距
    trainKeys.add(SCORE_ODD_DISTANCE_OF_MIN_70); // 70分钟让球和初盘差距
    // trainKeys.add(BIG_BALL_ODD_DISTANCE_TO_MIDDLE_OF_MIN_70); // 70分钟大小球和中场差距
    // trainKeys.add(SCORE_ODD_DISTANCE_TO_MIDDLE_OF_MIN_70); // 70分钟让球和中场差距

    trainKeys.add(BIG_BALL_ODD_VICTORY_OF_MIN70); // 70分钟大球赔率
    trainKeys.add(BIG_BALL_ODD_DEFEAT_OF_MIN70); // 70分钟大球赔率
    trainKeys.add(TOTAL_BEST_SHOOT_OF_MIN70); // 70分钟场上射正次数
    trainKeys.add(TOTAL_CORNER_OF_MIN70); // 70分钟场上角球次数

    return trainKeys;
  }

  @Override
  public TrainKey keyOfY() {
    return BIG_BALL_OF_MIN70_VALUE;
  }

  @Override
  public float bestThreshold() {
    return 0.50f; // 命中率=69% 左右
  }

  @Override
  public float profit(Map<String, Float> values, float predictValue) {
    float realValue = values.get(keyOfY().mKey);
    if (realValue != predictValue) {
      return 0;
    }
    if (realValue == 1) { // 上盘赔率
      return values.get(BIG_BALL_ODD_VICTORY_OF_MIN70.mKey);
    } else {
      return values.get(BIG_BALL_ODD_DEFEAT_OF_MIN70.mKey);
    }
  }
}

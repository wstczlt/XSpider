package com.test.train.model;

import static com.test.train.match.QueryHelper.SQL_BASE;
import static com.test.train.match.QueryHelper.SQL_MIN_70;
import static com.test.train.match.QueryHelper.SQL_ORDER;
import static com.test.train.match.TrainKey.BIG_BALL_ODD_DEFEAT_OF_MIN70;
import static com.test.train.match.TrainKey.BIG_BALL_ODD_VICTORY_OF_MIN70_FIX;
import static com.test.train.match.TrainKey.BIG_BALL_OF_MIN70_VALUE;
import static com.test.train.match.TrainKey.ORIGINAL_BIG_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_SCORE_ODD_ABS;
import static com.test.train.match.TrainKey.TOTAL_BEST_SHOOT_OF_MIN70;
import static com.test.train.match.TrainKey.TOTAL_SCORE_OF_MIN_70;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.test.train.match.TrainKey;
import com.test.train.utils.TrainModel;

public class BallAt70 extends TrainModel {

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
    // trainKeys.add(IS_CUP_MATCH);
    // trainKeys.add(IS_YEJI_MATCH);
    trainKeys.add(ORIGINAL_BIG_ODD); // 大小球初盘
    trainKeys.add(ORIGINAL_SCORE_ODD_ABS); // 让球初盘绝对值
    trainKeys.add(BIG_BALL_ODD_VICTORY_OF_MIN70_FIX);// 70'大小球赔率
    trainKeys.add(TOTAL_SCORE_OF_MIN_70); // 70'总进球
    trainKeys.add(TOTAL_BEST_SHOOT_OF_MIN70); // 70' 总射门

    // trainKeys.add(BIG_BALL_ODD_OF_MIN_70); // 大小球70'
    // trainKeys.add(SCORE_ODD_OF_MIN_70); // 让球70'

    // trainKeys.add(ORIGINAL_BIG_ODD_OF_VICTORY); // 初盘大小球赔率
    // trainKeys.add(BIG_BALL_ODD_VICTORY_OF_MIN70);// 70'大小球赔率

    // trainKeys.add(TrainKey.HOST_SCORE_OF_MIN_70);
    // trainKeys.add(TrainKey.CUSTOM_SCORE_OF_MIN_70);

    // trainKeys.add(BIG_BALL_ODD_DISTANCE_OF_MIN_70); // 70分钟大小球和初盘差距
    // trainKeys.add(SCORE_ODD_DISTANCE_OF_MIN_70); // 70分钟让球和初盘差距
    // trainKeys.add(BIG_BALL_ODD_DISTANCE_TO_MIDDLE_OF_MIN_70); // 70分钟大小球和中场差距
    // trainKeys.add(SCORE_ODD_DISTANCE_TO_MIDDLE_OF_MIN_70); // 70分钟让球和中场差距

    // trainKeys.add(RECENT_HOST_BALL_COUNT); // 主队近3场进球数
    // trainKeys.add(RECENT_CUSTOM_BALL_COUNT); // 客队近3场进球数

    // trainKeys.add(BIG_BALL_ODD_VICTORY_OF_MIN70); // 70分钟大球赔率
    // trainKeys.add(BIG_BALL_ODD_DEFEAT_OF_MIN70); // 70分钟大球赔率
    // trainKeys.add(TOTAL_BEST_SHOOT_OF_MIN70); // 70分钟场上射正次数
    // trainKeys.add(TOTAL_CORNER_OF_MIN70); // 70分钟场上角球次数

    return trainKeys;
  }

  @Override
  public TrainKey keyOfY() {
    return BIG_BALL_OF_MIN70_VALUE;
  }

  @Override
  public float bestThreshold() {
    return 0.55f; // 命中率=69% 左右
  }

  @Override
  public float profit(Map<String, Float> values, float predictValue) {
    float realValue = values.get(keyOfY().mKey);
    if (realValue != predictValue) {
      return -1;
    }
    if (realValue == 1) { // 出大球
      return 0.8f; // 平均赔率代替
    } else { // 出小球，全赢
      return 0.9f;
    }
  }
}

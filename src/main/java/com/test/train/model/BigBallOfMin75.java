package com.test.train.model;

import static com.test.train.match.TrainKey.BIG_BALL_ODD_DISTANCE_OF_MIN_75;
import static com.test.train.match.TrainKey.BIG_BALL_ODD_VICTORY_OF_MIN75;
import static com.test.train.match.TrainKey.BIG_BALL_OF_MIN75_VALUE;
import static com.test.train.match.TrainKey.ORIGINAL_BIG_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_BIG_ODD_OF_VICTORY;
import static com.test.train.match.TrainKey.ORIGINAL_SCORE_ODD;
import static com.test.train.match.TrainKey.ORIGINAL_SCORE_ODD_OF_VICTORY;
import static com.test.train.match.TrainKey.SCORE_ODD_DISTANCE_OF_MIN_75;
import static com.test.train.match.TrainKey.TOTAL_BEST_SHOOT_OF_MIN75;
import static com.test.train.match.TrainKey.TOTAL_CORNER_OF_MIN75;

import java.util.ArrayList;
import java.util.List;

import com.test.train.TrainModel;
import com.test.train.match.TrainKey;

public class BigBallOfMin75 extends TrainModel {

  @Override
  public String name() {
    return "bigBallOfMin75";
  }

  @Override
  public List<TrainKey> keyOfX() {
    List<TrainKey> trainKeys = new ArrayList<>();
    trainKeys.add(ORIGINAL_BIG_ODD); // 大小球初盘
    trainKeys.add(ORIGINAL_SCORE_ODD); // 让球初盘
    trainKeys.add(BIG_BALL_ODD_DISTANCE_OF_MIN_75); // 75分钟大小球和初盘差距
    trainKeys.add(SCORE_ODD_DISTANCE_OF_MIN_75); // 75分钟让球和初盘差距
    trainKeys.add(BIG_BALL_ODD_VICTORY_OF_MIN75); // 75分钟大球赔率
    trainKeys.add(TOTAL_BEST_SHOOT_OF_MIN75); // 75分钟场上射正次数
    trainKeys.add(TOTAL_CORNER_OF_MIN75); // 75分钟场上角球次数

    return trainKeys;
  }

  @Override
  public TrainKey keyOfY() {
    return BIG_BALL_OF_MIN75_VALUE;
  }
}

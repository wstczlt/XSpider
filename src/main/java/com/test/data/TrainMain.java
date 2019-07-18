package com.test.data;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.io.IOUtils;

import com.test.data.train.BigBall;
import com.test.xspider.utils.XSpiderDbSupplier;

public class TrainMain {

  // select count(*) from football where hostScore is not null AND customScore is not null AND
  // original_scoreOdd is not null AND original_bigOdd is not null AND original_drawOdd is not null;

  // (matchID, customAttach, customBestAttack, customBestShoot,
  // customControlRate, customControlRateOf10, customControlRateOf3, customCornerOf10,
  // customCornerOf3, customCornerScore, customLeagueOnCustomRank,
  // customLeagueOnCustomRateOfVictory, customLeagueRank, customLeagueRateOfVictory, customLossOf10,
  // customLossOf3, customName, customNamePinyin, customRedCard, customScore, customScoreOf10,
  // customScoreOf3, customShoot, customYellowCard, customYellowCardOf10, customYellowCardOf3,
  // hostAttack, hostBestAttack, hostBestShoot, hostControlRate, hostControlRateOf10,
  // hostControlRateOf3, hostCornerOf10, hostCornerOf3, hostCornerScore, hostLeagueOnHostRank,
  // hostLeagueOnHostRateOfVictory, hostLeagueRank, hostLeagueRateOfVictory, hostLossOf10,
  // hostLossOf3, hostName, hostNamePinyin, hostRedCard, hostScore, hostScoreOf10, hostScoreOf3,
  // hostShoot, hostYellowCard, hostYellowCardOf10, hostYellowCardOf3, league, matchTime,
  // middle_bigOdd, middle_bigOddOfDefeat, middle_bigOddOfVictory, middle_cornerOdd,
  // middle_cornerOddOfDefeat, middle_cornerOddOfVictory, middle_customCornerScore,
  // middle_customScore, middle_defeatOdd, middle_drawOdd, middle_hostCornerScore, middle_hostScore,
  // middle_scoreOdd, middle_scoreOddOfDefeat, middle_scoreOddOfVictory, middle_victoryOdd,
  // opening_bigOdd, opening_bigOddOfDefeat, opening_bigOddOfVictory, opening_cornerOdd,
  // opening_cornerOddOfDefeat, opening_cornerOddOfVictory, opening_customCornerScore,
  // opening_customScore, opening_defeatOdd, opening_drawOdd, opening_hostCornerScore,
  // opening_hostScore, opening_scoreOdd, opening_scoreOddOfDefeat, opening_scoreOddOfVictory,
  // opening_victoryOdd, original_bigOdd, original_bigOddOfDefeat, original_bigOddOfVictory,
  // original_cornerOdd, original_cornerOddOfDefeat, original_cornerOddOfVictory,
  // original_customCornerScore, original_customScore, original_defeatOdd, original_drawOdd,
  // original_hostCornerScore, original_hostScore, original_scoreOdd, original_scoreOddOfDefeat,
  // original_scoreOddOfVictory, original_victoryOdd, weather)


  private static final String QUERY_SQL =
      "select * from football where hostScore is not null AND customScore is not null AND original_scoreOdd is not null AND original_bigOdd is not null AND original_drawOdd is not null order by matchTime desc";
  private static final int MAX_RECENTLY = 5; // 最近几场比赛

  private static final int TRAINING_SET_COUNT = 6000; // 训练集大小
  private static final String TRAINING_X_PATH = "training/x.dat";
  private static final String TRAINING_Y_PATH = "training/y.dat";

  public static void main(String[] args) {
    TrainMain train = new TrainMain();
    final List<Match> matches = train.loadAllMatch();
    // train.writeTrainingValues(matches, new BigBall());
    train.testTrainOddVictory(matches, new BigBall());
  }

  private List<Match> loadAllMatch() {
    final List<Match> matches = new ArrayList<>();
    QueryRunner runner = new QueryRunner(XSpiderDbSupplier.getDataSource());
    try {
      List<Map<String, Object>> mapList = runner.query(QUERY_SQL, new MapListHandler());
      for (Map<String, Object> map : mapList) {
        final Match match = Match.fromMap(map);
        matches.add(match);
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return matches;
  }

  private void testTrainOddVictory(List<Match> matches, Trainable trainable) { // 参数: 最小购买概率
    int buyCount = 0; // 购买总数(等价于比赛总数)
    int trainBuyCount = 0; // AI购买的比赛总数
    float totalGain = 0; // 总盈利
    float trainBuyGain = 0; // AI总回款
    for (int i = 0; i < matches.size(); i++) {
      if (i < TRAINING_SET_COUNT) { // 训练集数据不使用
        continue;
      }
      final Match match = matches.get(i);
      final Map<String, Float> values = Trainable.toMap(match);
      if (values.isEmpty()) {
        continue;
      }
      trainable.setValues(values);
      System.out
          .println(
              "matchID=" + match.mMatchID + "ballCount=" + (match.mHostScore + match.mCustomScore)
                  + ", originOddCount=" + match.mOriginalBigOdd
                  + ", odd=" + match.mOriginalBigOddOfVictory);
      boolean trainBuy = trainable.isPositive();
      buyCount++;
      totalGain = totalGain + trainable.computeProfit();
      if (trainBuy) {
        trainBuyCount++;
        trainBuyGain = trainBuyGain + trainable.computeProfit();
      }
    }

    System.out.println(String.format("无脑购买了: %d 场，盈利: %.2f, 盈利率: %.2f", buyCount, totalGain,
        totalGain * 1f / buyCount));
    System.out.println(String.format("AI购买了: %d 场上盘，盈利: %.2f, 盈利率: %.2f", trainBuyCount,
        trainBuyGain, trainBuyGain * 1f / trainBuyCount));
  }


  // 写入训练集数据
  private void writeTrainingValues(List<Match> matches, Trainable trainable) {
    List<String> xValue = new ArrayList<>();
    List<String> yValue = new ArrayList<>();
    for (int i = 0; i < matches.size(); i++) {
      final Match match = matches.get(i);
      Map<String, Float> values = Trainable.toMap(match);
      if (values.isEmpty() || i >= TRAINING_SET_COUNT) { // 训练集大小
        continue;
      }
      trainable.setValues(values);
      xValue.add(trainable.toX());
      yValue.add(trainable.toY());
    }
    FileWriter xWriter = null;
    FileWriter yWriter = null;
    try {
      xWriter = new FileWriter(TRAINING_X_PATH);
      yWriter = new FileWriter(TRAINING_Y_PATH);
      IOUtils.writeLines(xValue, null, xWriter);
      IOUtils.writeLines(yValue, null, yWriter);
    } catch (Throwable e) {
      e.printStackTrace();
    } finally {
      IOUtils.closeQuietly(xWriter);
      IOUtils.closeQuietly(yWriter);
    }
  }

}

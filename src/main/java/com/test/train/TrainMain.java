package com.test.train;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.test.spider.tools.Pair;
import com.test.train.match.Match;
import com.test.train.match.MatchDao;
import com.test.train.match.PredictResult;
import com.test.train.model.BigBallOfMin75;
import com.test.train.utils.TrainUtils;

public class TrainMain {

  public static void main(String[] args) throws Exception {
    trainTest();
  }

  private static void trainTest() throws Exception {
    final int totalRound = 3; // 测试轮数
    final int testSetCount = 2000; // 测试集长度
    final float threshold = 0.75f; // 高概率要求的阈值
    final List<Match> matches = MatchDao.loadAllMatch();
    final TrainModel model = new BigBallOfMin75(); // 训练模型
    final List<Map<String, Float>> dataSet = createDataSet(matches);
    final List<Pair<PredictResult, PredictResult>> results = new ArrayList<>();
    for (int i = 0; i < totalRound; i++) {
      Collections.shuffle(dataSet);
      List<Map<String, Float>> trainSet = dataSet.subList(0, dataSet.size() - testSetCount);
      List<Map<String, Float>> testSet =
          dataSet.subList(dataSet.size() - testSetCount, dataSet.size());
      // 训练
      model.train(trainSet);
      results.add(doTest(model, testSet, threshold));
    }

    display(model, results);
  }

  private static List<Map<String, Float>> createDataSet(List<Match> matches) {
    // 生成训练集以及测试集
    Collections.shuffle(matches); // 拷贝一份并打散, 有利于验证结果
    final List<Map<String, Float>> dataSet = new ArrayList<>();
    for (int i = 0; i < matches.size(); i++) {
      final Match match = matches.get(i);
      final Map<String, Float> item = TrainUtils.buildTrainMap(match);
      if (item.isEmpty()) {
        continue;
      }
      dataSet.add(item);
    }

    return dataSet;
  }

  private static Pair<PredictResult, PredictResult> doTest(TrainModel model,
      List<Map<String, Float>> testSet, double positiveThreshold) throws Exception {
    List<Pair<Double, Double>> results = model.predict(testSet);
    int normalTotalCount = 0, highProbTotalCount = 0;
    int normalHitCount = 0, highProbHitCount = 0;
    int normalPositiveHitCount = 0, highPositiveProbHitCount = 0;
    if (results.size() != testSet.size()) {
      throw new RuntimeException();
    }
    for (int i = 0; i < results.size(); i++) {
      normalTotalCount++;
      float realValue = testSet.get(i).get(model.keyOfY().mKey);
      float predictValue = results.get(i).first.floatValue();
      boolean positive = realValue == predictValue;
      if (positive) { // 实际阳性
        normalHitCount++;
        if (realValue == 1) { // 正向
          normalPositiveHitCount++;
        }
      }
      // 高概率
      if (results.get(i).second >= positiveThreshold) {
        highProbTotalCount++;
        if (positive) { // 实际阳性
          highProbHitCount++;
          if (realValue == 1) { // 正向
            highPositiveProbHitCount++;
          }
        }
      }
    }

    PredictResult normalResult =
        new PredictResult(normalTotalCount, normalHitCount, normalPositiveHitCount, 0);
    PredictResult highProbResult =
        new PredictResult(highProbTotalCount, highProbHitCount, highPositiveProbHitCount, 0);

    return new Pair<>(normalResult, highProbResult);
  }

  private static void display(TrainModel model, List<Pair<PredictResult, PredictResult>> results) {
    int totalRound = results.size();
    float totalCount = 0, hitCount = 0, positiveHitCount = 0, profit = 0;
    float totalCountOfHigh = 0, hitCountOfHigh = 0, positiveHitCountOfHigh = 0, profitOfHigh = 0;

    for (Pair<PredictResult, PredictResult> pair : results) {
      totalCount += pair.first.mTotalCount;
      hitCount += pair.first.mHitCount;
      positiveHitCount += pair.first.mPositiveHitCount;
      profit += pair.first.mProfit;

      totalCountOfHigh += pair.second.mTotalCount;
      hitCountOfHigh += pair.second.mHitCount;
      positiveHitCountOfHigh += pair.second.mPositiveHitCount;
      profitOfHigh += pair.second.mProfit;
    }
    PredictResult normalResult = new PredictResult(totalCount / totalRound, hitCount / totalRound,
        positiveHitCount / totalRound, profit / totalRound);
    PredictResult highProbResult =
        new PredictResult(totalCountOfHigh / totalRound, hitCountOfHigh / totalRound,
            positiveHitCountOfHigh / totalRound, profitOfHigh / totalRound);

    System.out.println(String.format("Model=%s, 总场次=%.2f, 命中次数=%.2f, 命中上盘次数=%.2f，命中率=%d%%，盈利=%.2f",
        model.name(), normalResult.mTotalCount,
        normalResult.mHitCount,
        normalResult.mPositiveHitCount,
        (int) (normalResult.mHitCount * 100 / normalResult.mTotalCount),
        normalResult.mHitCount * 1.88f - normalResult.mTotalCount));

    System.out
        .println(String.format("Model=%s, 高概率场次=%.2f, 命中次数=%.2f, 命中上盘次数=%.2f，命中率=%d%%，盈利=%.2f",
            model.name(), highProbResult.mTotalCount,
            highProbResult.mHitCount,
            highProbResult.mPositiveHitCount,
            (int) (highProbResult.mHitCount * 100 / highProbResult.mTotalCount),
            highProbResult.mHitCount * 1.88f - highProbResult.mTotalCount));

    System.out.println();
  }

}

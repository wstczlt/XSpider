package com.test.train;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.test.spider.tools.Pair;
import com.test.train.match.Match;
import com.test.train.match.MatchQueryHelper;
import com.test.train.match.PredictResult;
import com.test.train.model.BigBallOfMin70;
import com.test.train.utils.TrainUtils;

public class TrainMain {

  public static void main(String[] args) throws Exception {
    final int totalRound = 10; // 测试轮数
    final int testSetCount = 2000; // 测试集长度
    final float[] thresholds = new float[] {
        0.56f, 0.57f, 0.58f, 0.59f, 0.60f,
        0.61f, 0.62f, 0.63f, 0.64f, 0.65f,}; // 高概率要求的阈值
    final List<Match> matches = MatchQueryHelper.loadAll();
    final TrainModel model = new BigBallOfMin70(); // 训练模型
    final List<Map<String, Float>> dataSet = TrainUtils.trainMaps(matches);

    for (float threshold : thresholds) {
      trainTest(model, totalRound, testSetCount, threshold, dataSet);
    }
  }

  private static void trainTest(TrainModel model, int totalRound, int testSetCount, float threshold,
      List<Map<String, Float>> dataSet) throws Exception {
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

    display(model, threshold, results);
  }

  private static Pair<PredictResult, PredictResult> doTest(TrainModel model,
      List<Map<String, Float>> testSet, double positiveThreshold) throws Exception {
    List<Pair<Double, Double>> results = model.predict(testSet);
    int normalTotalCount = 0, highProbTotalCount = 0, normalPositiveHitCount = 0, normalProfit = 0,
        maxContinueHitCount = 0, maxContinueMissCount = 0;
    int normalHitCount = 0, highProbHitCount = 0, highPositiveProbHitCount = 0, highPorbProfit = 0,
        highMaxContinueHitCount = 0, highMaxContinueMissCount = 0;
    if (results.size() != testSet.size()) {
      throw new RuntimeException();
    }
    int continueHit = 0, continueMiss = 0, highContinueHit = 0, highContinueMiss = 0;
    boolean lastHit = false, highLastHit = false;
    for (int i = 0; i < results.size(); i++) {
      normalTotalCount++;
      normalProfit += model.profit(testSet.get(i), results.get(i).first.floatValue());
      float realValue = testSet.get(i).get(model.keyOfY().mKey);
      float predictValue = results.get(i).first.floatValue();
      boolean thisHit = realValue == predictValue;
      if (thisHit) { // 实际阳性
        normalHitCount++;
        if (realValue == 1) { // 正向
          normalPositiveHitCount++;
        }
      }
      // 处理连黑连红计算
      if (thisHit) {
        continueMiss = 0;
        if (lastHit) {
          continueHit++;
        }
      } else {
        continueHit = 0;
        if (!lastHit) {
          continueMiss++;
        }
      }
      lastHit = thisHit;
      maxContinueHitCount = Math.max(continueHit, maxContinueHitCount);
      maxContinueMissCount = Math.max(continueMiss, maxContinueMissCount);

      // 高概率
      if (results.get(i).second >= positiveThreshold) {
        highProbTotalCount++;
        highPorbProfit += model.profit(testSet.get(i), results.get(i).first.floatValue());
        if (thisHit) { // 实际阳性
          highProbHitCount++;
          if (realValue == 1) { // 正向
            highPositiveProbHitCount++;
          }
        }

        // 处理连黑连红计算
        if (thisHit) {
          highContinueMiss = 0;
          if (highLastHit) {
            highContinueHit++;
          }
        } else {
          highContinueHit = 0;
          if (!highLastHit) {
            highContinueMiss++;
          }
        }
        highLastHit = thisHit;
        highMaxContinueHitCount = Math.max(highContinueHit, highMaxContinueHitCount);
        highMaxContinueMissCount = Math.max(highContinueMiss, highMaxContinueMissCount);
      }
    }

    PredictResult normalResult =
        new PredictResult(normalTotalCount, normalHitCount, normalPositiveHitCount,
            normalProfit, maxContinueHitCount, maxContinueMissCount);
    PredictResult highProbResult =
        new PredictResult(highProbTotalCount, highProbHitCount, highPositiveProbHitCount,
            highPorbProfit, highMaxContinueHitCount, highMaxContinueMissCount);

    return new Pair<>(normalResult, highProbResult);
  }

  private static void display(TrainModel model, float threshold,
      List<Pair<PredictResult, PredictResult>> results) {
    int totalRound = results.size();
    float totalCount = 0, hitCount = 0, positiveHitCount = 0, profit = 0, continueHitCount = 0,
        continueMissCount = 0;
    float totalCountOfHigh = 0, hitCountOfHigh = 0, positiveHitCountOfHigh = 0, profitOfHigh = 0,
        highContinueHitCount = 0, highContinueMissCount = 0;

    for (Pair<PredictResult, PredictResult> pair : results) {
      totalCount += pair.first.mTotalCount;
      hitCount += pair.first.mHitCount;
      positiveHitCount += pair.first.mPositiveHitCount;
      profit += pair.first.mProfit;
      continueHitCount += pair.first.mMaxContinueHitCount;
      continueMissCount += pair.first.mMaxContinueMissCount;

      totalCountOfHigh += pair.second.mTotalCount;
      hitCountOfHigh += pair.second.mHitCount;
      positiveHitCountOfHigh += pair.second.mPositiveHitCount;
      profitOfHigh += pair.second.mProfit;
      highContinueHitCount += pair.second.mMaxContinueHitCount;
      highContinueMissCount += pair.second.mMaxContinueMissCount;
    }
    PredictResult normalResult = new PredictResult(totalCount / totalRound, hitCount / totalRound,
        positiveHitCount / totalRound, profit / totalRound,
        continueHitCount / totalRound, continueMissCount / totalRound);
    PredictResult highProbResult =
        new PredictResult(totalCountOfHigh / totalRound, hitCountOfHigh / totalRound,
            positiveHitCountOfHigh / totalRound, profitOfHigh / totalRound,
            highContinueHitCount / totalRound, highContinueMissCount / totalRound);

    System.out.println("Test For threshold = " + threshold);
    System.out.println(String.format(
        "Model=%s, 总场次=%.2f, 命中次数=%.2f, 命中上盘次数=%.2f，命中率=%d%%，最多连红=%.2f, 最多连黑=%.2f, 盈利=%.2f",
        model.name(), normalResult.mTotalCount,
        normalResult.mHitCount,
        normalResult.mPositiveHitCount,
        (int) (normalResult.mHitCount * 100 / normalResult.mTotalCount),
        normalResult.mMaxContinueHitCount,
        normalResult.mMaxContinueMissCount,
        normalResult.mProfit));

    System.out
        .println(String.format(
            "Model=%s, 高概率场次=%.2f, 命中次数=%.2f, 命中上盘次数=%.2f，命中率=%d%%，最多连红=%.2f, 最多连黑=%.2f, 盈利=%.2f",
            model.name(), highProbResult.mTotalCount,
            highProbResult.mHitCount,
            highProbResult.mPositiveHitCount,
            (int) (highProbResult.mHitCount * 100 / highProbResult.mTotalCount),
            highProbResult.mMaxContinueHitCount,
            highProbResult.mMaxContinueMissCount,
            highProbResult.mProfit));

    System.out.println();
    System.out.println();
  }

}

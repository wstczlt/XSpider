package com.test.train;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.test.tools.Pair;
import com.test.train.model.BallHalfModel;
import com.test.train.model.Model;
import com.test.train.model.OddHalfModel;
import com.test.train.tools.Estimation;
import com.test.train.tools.Match;
import com.test.train.tools.MatchQuery;
import com.test.train.tools.TrainInputs;
import com.test.train.tools.TrainSummary;

public class TrainMain {

  private static final int TOTAL_ROUND = 5;// 测试轮数
  private static final int TEST_SET_COUNT = 2000; // 测试集长度
  private static final float[] THRESHOLDS = new float[] {
      // 0.50f,
      // 0.51f, 0.52f, 0.53f, 0.54f, 0.55f,
      0.50f, 0.55f, 0.60f, 0.65f, 0.7f}; // 高概率要求的阈值

  public static void main(String[] args) throws Exception {
    final Model model = new BallHalfModel(); // 训练模型


    final List<Match> matches = MatchQuery.doQuery(model.buildQuerySql());
    for (float threshold : THRESHOLDS) {
      trainTest(model, threshold, matches);
      Thread.sleep(1000); // 等待资源释放
    }
  }

  private static void trainTest(Model model, float threshold, List<Match> matches)
      throws Exception {
    final List<Pair<TrainSummary, TrainSummary>> results = new ArrayList<>();
    for (int i = 0; i < TOTAL_ROUND; i++) {
      Collections.shuffle(matches);
      List<Match> trainMatches = matches.subList(0, matches.size() - TEST_SET_COUNT);
      List<Match> testMatches = matches.subList(matches.size() - TEST_SET_COUNT, matches.size());

      TrainInputs trainData = new TrainInputs(model, trainMatches, true);
      TrainInputs testData = new TrainInputs(model, testMatches, false);
      // 训练
      model.train(trainData);
      results.add(doTest(model, testData, threshold));
    }

    display(model, threshold, results);
  }

  private static Pair<TrainSummary, TrainSummary> doTest(Model model,
      TrainInputs data, double threshold) throws Exception {
    List<Estimation> estimations = model.estimate(data);
    int normalTotalCount = 0, highProbTotalCount = 0, normalPositiveHitCount = 0,
        maxContinueHitCount = 0, maxContinueMissCount = 0;
    float normalProfit = 0;
    int normalHitCount = 0, highProbHitCount = 0, highPositiveProbHitCount = 0,
        highMaxContinueHitCount = 0, highMaxContinueMissCount = 0;
    float highPorbProfit = 0;

    int continueHit = 0, continueMiss = 0, highContinueHit = 0, highContinueMiss = 0;
    boolean lastHit = false, highLastHit = false;
    for (int i = 0; i < estimations.size(); i++) {
      final Match match = data.mMatches.get(i);
      // 随机结果
      final Estimation randomEst = new Estimation(new Random().nextInt(2), 0.5f);
      final float randomGain = model.calGain(match, randomEst);
      final boolean isRandomHit = randomGain > 0;

      normalTotalCount++;
      normalProfit += randomGain;

      if (isRandomHit) { // 实际命中
        normalHitCount++;
        // 上盘
        if (randomEst.mValue == 1) normalPositiveHitCount++;
      }
      // 处理连黑连红计算
      if (isRandomHit) {
        continueMiss = 0;
        // 连红
        if (lastHit) continueHit++;
      } else {
        continueHit = 0;
        // 连黑
        if (!lastHit) continueMiss++;
      }

      lastHit = isRandomHit;
      maxContinueHitCount = Math.max(continueHit, maxContinueHitCount);
      maxContinueMissCount = Math.max(continueMiss, maxContinueMissCount);

      // 处理AI的结果
      final Estimation est = estimations.get(i);
      final float aiGain = model.calGain(match, est);
      final boolean isAiHit = aiGain > 0;

      // 高概率
      if (est.mProbability >= threshold) {
        highProbTotalCount++;
        highPorbProfit += aiGain;
        if (isAiHit) { // 实际阳性
          highProbHitCount++;
          // 上盘
          if (est.mValue == 1) highPositiveProbHitCount++;
        }

        // 处理连黑连红计算
        if (isAiHit) {
          highContinueMiss = 0;
          // 连红
          if (highLastHit) highContinueHit++;
        } else {
          highContinueHit = 0;
          // 连黑
          if (!highLastHit) highContinueMiss++;
        }
        highLastHit = isAiHit;
        highMaxContinueHitCount = Math.max(highContinueHit, highMaxContinueHitCount);
        highMaxContinueMissCount = Math.max(highContinueMiss, highMaxContinueMissCount);
      }
    }

    TrainSummary normalResult =
        new TrainSummary(normalTotalCount, normalHitCount, normalPositiveHitCount,
            normalProfit, maxContinueHitCount, maxContinueMissCount);
    TrainSummary highProbResult =
        new TrainSummary(highProbTotalCount, highProbHitCount, highPositiveProbHitCount,
            highPorbProfit, highMaxContinueHitCount, highMaxContinueMissCount);

    return new Pair<>(normalResult, highProbResult);
  }

  private static void display(Model model, float threshold,
      List<Pair<TrainSummary, TrainSummary>> results) {
    int totalRound = results.size();
    float totalCount = 0, hitCount = 0, positiveHitCount = 0, profit = 0, continueHitCount = 0,
        continueMissCount = 0;
    float totalCountOfHigh = 0, hitCountOfHigh = 0, positiveHitCountOfHigh = 0, profitOfHigh = 0,
        highContinueHitCount = 0, highContinueMissCount = 0;

    for (Pair<TrainSummary, TrainSummary> pair : results) {
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
    TrainSummary normalResult = new TrainSummary(totalCount / totalRound, hitCount / totalRound,
        positiveHitCount / totalRound, profit / totalRound,
        continueHitCount / totalRound, continueMissCount / totalRound);
    TrainSummary highProbResult =
        new TrainSummary(totalCountOfHigh / totalRound, hitCountOfHigh / totalRound,
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

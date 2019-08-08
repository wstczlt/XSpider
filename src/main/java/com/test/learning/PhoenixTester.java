package com.test.learning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.test.db.QueryHelper;
import com.test.entity.Estimation;
import com.test.entity.Model;
import com.test.tools.Pair;

public class PhoenixTester {

  private static final int TOTAL_ROUND = 3;// 测试轮数
  private static final int TEST_SET_COUNT = 2000; // 测试集长度
  private static final float[] THRESHOLDS = new float[] {
      0.50f};
  // 0.4f, 0.45f, 0.5f, 0.53f, 0.55f, 0.58f};
  // 0.50f, 0.55f, 0.60f, 0.65f, 0.70f, 0.75f, 0.76f, 0.78f, 0.80f, 0.85f, 0.90f, 0.95f}; //
  // 高概率要求的阈值

  public static void runTest(Model model) throws Exception {
    final List<Map<String, Object>> matches = QueryHelper.doQuery(model.querySql(), 50000);
    for (float threshold : THRESHOLDS) {
      trainAndTest(model, threshold, matches);
      Thread.sleep(1000); // 等待资源释放
    }
  }

  private static void trainAndTest(Model model, float threshold, List<Map<String, Object>> matches)
      throws Exception {
    final List<Pair<EstScore, EstScore>> results = new ArrayList<>();
    for (int i = 0; i < TOTAL_ROUND; i++) {
      Collections.shuffle(matches);
      List<Map<String, Object>> trainMatches = matches.subList(0, matches.size() - TEST_SET_COUNT);
      List<Map<String, Object>> testMatches =
          matches.subList(matches.size() - TEST_SET_COUNT, matches.size());

      PhoenixInputs trainData = new PhoenixInputs(model, trainMatches, true);
      PhoenixInputs testData = new PhoenixInputs(model, testMatches, false);
      // 训练
      Phoenix.runTrainMetric(model, trainData);
      // 测试
      List<Estimation> ests = Phoenix.runEstMetric(model, testData);
      // 展示结果
      results.add(score(model, testMatches, ests, threshold));
    }

    display(model, threshold, results);
  }

  private static Pair<EstScore, EstScore> score(Model model, List<Map<String, Object>> matches,
      List<Estimation> estimations, double threshold) {
    int normalTotalCount = 0, highProbTotalCount = 0, normalPositiveHitCount = 0,
        maxContinueHitCount = 0, maxContinueMissCount = 0;
    float normalProfit = 0;
    int normalHitCount = 0, highProbHitCount = 0, highPositiveProbHitCount = 0,
        highMaxContinueHitCount = 0, highMaxContinueMissCount = 0;
    int normalDrewCount = 0, highProbDrewCount = 0;
    float highProbProfit = 0;

    int continueHit = 0, continueMiss = 0, highContinueHit = 0, highContinueMiss = 0;
    boolean lastHit = false, highLastHit = false;
    for (int i = 0; i < estimations.size(); i++) {
      final Map<String, Object> match = matches.get(i);
      // 随机结果
      final Estimation randomEst = new Estimation(new Random().nextInt(3), 0.5f);
      final float randomGain = model.calGain(match, randomEst);
      final boolean isRandomHit = randomGain > 0;
      final boolean isRandomDrew = randomGain == 0;

      normalTotalCount++;
      normalProfit += randomGain;

      if (isRandomHit) { // 实际命中
        normalHitCount++;
        // 上盘
        if (randomEst.mValue == 0) normalPositiveHitCount++;
      }
      // 处理连黑连红计算
      if (isRandomHit) {
        continueMiss = 0;
        // 连红
        if (lastHit) continueHit++;
      } else if (isRandomDrew) {
        normalDrewCount++;
        // 不重置连红连黑
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
      final boolean isAiDrew = aiGain == 0;

      // System.out.println(est.mValue + ", " + model.yValue(match) + ", "
      // + ((OddModel) model).deltaScore(match) + ", " + aiGain);
      // 高概率
      if (est.mProbability >= threshold) {
        highProbTotalCount++;
        highProbProfit += aiGain;
        if (isAiHit) { // 实际阳性
          highProbHitCount++;
          // 上盘
          if (est.mValue == 0) highPositiveProbHitCount++;
        }

        // 处理连黑连红计算
        if (isAiHit) {
          highContinueMiss = 0;
          // 连红
          if (highLastHit) highContinueHit++;
        } else if (isAiDrew) {
          highProbDrewCount++;
          // 不重置连红连黑
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

    EstScore normalResult =
        new EstScore(normalTotalCount, normalHitCount, normalDrewCount,
            normalPositiveHitCount,
            normalProfit, maxContinueHitCount, maxContinueMissCount);
    EstScore highProbResult =
        new EstScore(highProbTotalCount, highProbHitCount, highProbDrewCount,
            highPositiveProbHitCount,
            highProbProfit, highMaxContinueHitCount, highMaxContinueMissCount);

    return new Pair<>(normalResult, highProbResult);
  }

  private static void display(Model model, float threshold,
      List<Pair<EstScore, EstScore>> results) {
    int totalRound = results.size();
    float totalCount = 0, hitCount = 0, drewCount = 0, positiveHitCount = 0, profit = 0,
        continueHitCount = 0, continueMissCount = 0;
    float totalCountOfHigh = 0, hitCountOfHigh = 0, drewCountOfHigh = 0, positiveHitCountOfHigh = 0,
        profitOfHigh = 0, highContinueHitCount = 0, highContinueMissCount = 0;

    for (Pair<EstScore, EstScore> pair : results) {
      totalCount += pair.first.mTotalCount;
      hitCount += pair.first.mHitCount;
      drewCount += pair.first.mDrewCount;
      positiveHitCount += pair.first.mPositiveHitCount;
      profit += pair.first.mProfit;
      continueHitCount += pair.first.mMaxContinueHitCount;
      continueMissCount += pair.first.mMaxContinueMissCount;

      totalCountOfHigh += pair.second.mTotalCount;
      hitCountOfHigh += pair.second.mHitCount;
      drewCountOfHigh += pair.second.mDrewCount;
      positiveHitCountOfHigh += pair.second.mPositiveHitCount;
      profitOfHigh += pair.second.mProfit;
      highContinueHitCount += pair.second.mMaxContinueHitCount;
      highContinueMissCount += pair.second.mMaxContinueMissCount;
    }


    EstScore normalResult = new EstScore(totalCount / totalRound, hitCount / totalRound,
        drewCount / totalRound,
        positiveHitCount / totalRound, profit / totalRound,
        continueHitCount / totalRound, continueMissCount / totalRound);
    EstScore highProbResult =
        new EstScore(totalCountOfHigh / totalRound, hitCountOfHigh / totalRound,
            drewCountOfHigh / totalRound,
            positiveHitCountOfHigh / totalRound, profitOfHigh / totalRound,
            highContinueHitCount / totalRound, highContinueMissCount / totalRound);

    System.out.println("Test For threshold = " + threshold);
    System.out.println(String.format(
        "Model=%s, 随机场次=%.2f, 命中次数=%.2f, 命中主队次数=%.2f，胜率=%d%%，走率=%d%%，败率=%d%%，最多连红=%.2f, 最多连黑=%.2f, 盈利=%.2f, 盈利率=%d%%",
        model.name(), normalResult.mTotalCount,
        normalResult.mHitCount,
        normalResult.mPositiveHitCount,
        (int) (normalResult.mHitCount * 100 / normalResult.mTotalCount),
        (int) (normalResult.mDrewCount * 100 / normalResult.mTotalCount),
        (int) ((normalResult.mTotalCount - normalResult.mHitCount - normalResult.mDrewCount) * 100
            / normalResult.mTotalCount),
        normalResult.mMaxContinueHitCount,
        normalResult.mMaxContinueMissCount,
        normalResult.mProfit,
        (int) (normalResult.mProfit * 100 / (normalResult.mTotalCount - normalResult.mDrewCount))));

    System.out
        .println(String.format(
            "Model=%s, 筛选场次=%.2f, 命中次数=%.2f, 命中主队次数=%.2f，胜率=%d%%，走率=%d%%，败率=%d%%，最多连红=%.2f, 最多连黑=%.2f, 盈利=%.2f, 盈利率=%d%%",
            model.name(), highProbResult.mTotalCount,
            highProbResult.mHitCount,
            highProbResult.mPositiveHitCount,
            (int) (highProbResult.mHitCount * 100 / highProbResult.mTotalCount),
            (int) (highProbResult.mDrewCount * 100 / highProbResult.mTotalCount),
            (int) ((highProbResult.mTotalCount - highProbResult.mHitCount
                - highProbResult.mDrewCount) * 100 / highProbResult.mTotalCount),
            highProbResult.mMaxContinueHitCount,
            highProbResult.mMaxContinueMissCount,
            highProbResult.mProfit,
            (int) (highProbResult.mProfit * 100
                / (highProbResult.mTotalCount - highProbResult.mDrewCount))));

    System.out.println();
    System.out.println();
  }
}

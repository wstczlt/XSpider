package com.test.learning;

import static com.test.db.QueryHelper.SQL_ST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.test.db.QueryHelper;
import com.test.entity.Estimation;
import com.test.entity.Model;
import com.test.tools.Pair;
import com.test.tools.Utils;

public class PhoenixTester {

  private static final int TOTAL_ROUND = 1;// 测试轮数
  private static final float[] THRESHOLDS = new float[] {
      // 0.01f};
      // 0.03f, 0.04f, 0.05f, 0.06f, 0.07f, 0.08f, 0.1f};
      // 0.05f, 0.08f, 0.1f};
      0.08f, 0.1f, 0.12f, 0.14f, 0.16f, 0.18f};
  // 0.18f, 0.2f, 0.22f, 0.24f, 0.25f};
  // 0.25f};
  // 0.05f, 0.06f, 0.07f, 0.08f, 0.09f,
  // 0.1f, 0.11f, 0.12f, 0.13f, 0.14f,
  // 0.15f, 0.16f, 0.17f, 0.18f, 0.19f,
  // 0.2f, 0.21f, 0.22f, 0.23f, 0.24f, 0.25f,
  // 0.26f, 0.27f, 0.28f, 0.29f, 0.30f,
  // 0.31f, 0.32f, 0.33f, 0.34f, 0.35f};
  // 0.6f, 0.62f, 0.64f, 0.66f, 0.68f};
  // 0.60f, 0.65f, 0.70f, 0.71f, 0.72f, 0.73f, 0.74f, 0.75f, 0.76f, 0.77f, 0.78f, 0.79f, 0.8f}; //

  public static void runTest(Model model) throws Exception {
    final List<Map<String, Object>> matches = QueryHelper.doQuery(model.querySql(SQL_ST), 1000000);
    for (float threshold : THRESHOLDS) {
      trainAndTest(model, threshold, matches);
      Thread.sleep(1000); // 等待资源释放
    }
  }

  private static void trainAndTest(Model model, float threshold, List<Map<String, Object>> matches)
      throws Exception {

    final List<Pair<EstScore, EstScore>> lr = new ArrayList<>();
    for (int i = 0; i < TOTAL_ROUND; i++) {
      Collections.shuffle(matches);
      int testSetCount = (int) (matches.size() * 0.25);
      List<Map<String, Object>> trainMatches = matches.subList(0, matches.size() - testSetCount);
      List<Map<String, Object>> testMatches =
          matches.subList(matches.size() - testSetCount, matches.size());

      // 训练
      Phoenix.runTrain(model, trainMatches);
      // 测试
      lr.add(score(model, testMatches, Phoenix.runEst(model, testMatches), threshold));
    }

    System.out.println("拟合算法结果: 概率>=" + threshold);
    display(model, threshold, lr);
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
      final Estimation randomEst = new Estimation(model, match,
          new Random().nextInt(2) * 2,
          1f, 1f, 1f, 1f);
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
      Estimation est = estimations.get(i);
      // // 尝试反买AI
      // est = new Estimation(est.mModel, est.mMatch, est.mValue == 0 ? 2 : 0, est.mProb2,
      // est.mProb1,
      // est.mProb0);
      final float aiGain = model.calGain(match, est);
      final boolean isAiHit = aiGain > 0;
      final boolean isAiDrew = aiGain == 0;

      // 精选高概率
      if (Math.abs(est.mProb0 - est.mProb2) >= threshold) {
        // System.out.println(String.format(
        // "[%s], [ManbetX=%s, bet365=%s, Easybets=%s], %.2f, 概率[%.2f, %.2f, %.2f], 初盘=%s, 临场盘=%s,
        // 初赔率=%s, 临赔率=%s, 买入=%s, 结果=%s",
        // match.get(MATCH_ID),
        // match.get("start_7_victoryOdd"),
        // match.get("start_8_victoryOdd"),
        // match.get("start_12_victoryOdd"),
        // valueOfFloat(match.get(OPENING_VICTORY_ODD))
        // - valueOfFloat(match.get(ORIGINAL_VICTORY_ODD)),
        // est.mProb0, est.mProb1, est.mProb2,
        // match.get(ORIGINAL_SCORE_ODD),
        // match.get(OPENING_SCORE_ODD),
        // match.get(ORIGINAL_SCORE_ODD_OF_VICTORY),
        // match.get(OPENING_SCORE_ODD_OF_VICTORY),
        // est.mProb0 - est.mProb2 > 0 ? "主" : "客",
        // ("(" + match.get(HOST_SCORE) + "-" + match.get(CUSTOM_SCORE) + ")")
        // + (isAiHit ? "红" : (isAiDrew ? "走" : "黑"))));
        System.out.println(String.format("best=%s, value=%.2f, %.2f， 概率[%.2f, %.2f, %.2f]",
            Utils.valueOfInt(match.get("min45_hostBestShoot"))
                - Utils.valueOfInt(match.get("min45_customBestShoot")),
            est.mValue, aiGain, est.mProb0, est.mProb1, est.mProb2));
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

    System.out.println(String.format(
        "Model=%s, 随机场次=%.2f, 筛选比例=%d%%, 胜率=%d%%，走率=%d%%，败率=%d%% 盈利=%.2f, 盈利率=%d%%",
        model.name(), normalResult.mTotalCount,
        100,
        (int) (normalResult.mHitCount * 100 / normalResult.mTotalCount),
        (int) (normalResult.mDrewCount * 100 / normalResult.mTotalCount),
        (int) ((normalResult.mTotalCount - normalResult.mHitCount - normalResult.mDrewCount) * 100
            / normalResult.mTotalCount),
        normalResult.mProfit,
        (int) (normalResult.mProfit * 100 / (normalResult.mTotalCount))));

    System.out
        .println(String.format(
            "Model=%s, 筛选场次=%.2f, 筛选比例=%d%%，胜率=%d%%，走率=%d%%，败率=%d%%，盈利=%.2f, 盈利率=%d%%",
            model.name(), highProbResult.mTotalCount,
            (int) (highProbResult.mTotalCount * 100 / normalResult.mTotalCount),
            (int) (highProbResult.mHitCount * 100 / highProbResult.mTotalCount),
            (int) (highProbResult.mDrewCount * 100 / highProbResult.mTotalCount),
            (int) ((highProbResult.mTotalCount - highProbResult.mHitCount
                - highProbResult.mDrewCount) * 100 / highProbResult.mTotalCount),
            highProbResult.mProfit,
            (int) (highProbResult.mProfit * 100 / (highProbResult.mTotalCount))));

    System.out.println();
    System.out.println();
  }
}

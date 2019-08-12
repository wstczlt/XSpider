package com.test.manual;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.test.Keys;
import com.test.db.QueryHelper;
import com.test.entity.Estimation;
import com.test.learning.model.Odd45;

public class OddRules implements Keys {

  // 数据库查询条数
  private static final int DEFAULT_SQL_COUNT = 1000000;
  // 数据低于多少条则不要
  private static final int DEFAULT_MIN_RULE_COUNT = 200;
  // 最低胜率要求
  private static final float DEFAULT_MIN_VICTORY_RATE = 0.5f;
  // 最低盈利率要求
  private static final float DEFAULT_MIN_PROFIT_RATE = 1.0f;

  private final int mTimeMin;
  private final String mTimePrefix;

  private final Set<String> mRuleKeys = new HashSet<>();
  private final Map<String, Float> mHostSum = new HashMap<>();
  private final Map<String, Float> mCustomSum = new HashMap<>();
  private final Map<String, Integer> mHostCount = new HashMap<>();
  private final Map<String, Integer> mDrewCount = new HashMap<>();
  private final Map<String, Integer> mCustomCount = new HashMap<>();

  // 规则
  private final Map<String, Integer> mRules = new HashMap<>();
  // 命中率
  private final Map<String, Float> mRuleVictoryRate = new HashMap<>();
  // 盈利率
  private final Map<String, Float> mRuleProfitRate = new HashMap<>();

  public OddRules(int timeMin) {
    mTimeMin = timeMin;
    mTimePrefix = "min" + mTimeMin + "_";
  }

  public void make() throws Exception {
    mHostSum.clear();
    mCustomSum.clear();
    List<Map<String, Object>> matches = QueryHelper.doQuery(buildSql(), DEFAULT_SQL_COUNT);
    Collections.shuffle(matches);
    List<Map<String, Object>> trains = matches.subList(0, (int) (matches.size() * 0.75));
    List<Map<String, Object>> tests =
        matches.subList((int) (matches.size() * 0.75), matches.size());

    // 训练
    trains.forEach(this::calMatch);
    mRuleKeys.forEach(this::makeOne);
    // print();

    List<Estimation> estimations = tests.stream().map(this::estOneMatch).filter(Objects::nonNull)
        .collect(Collectors.toList());

    Float[] thresholds = new Float[] {1.02f, 1.03f, 1.04f, 1.05f, 1.08f, 1.1f};
    Arrays.asList(thresholds).forEach(threshold -> {
      // 测试
      System.out.println("threshold=" + threshold);
      List<Estimation> filtered = estimations.stream()
          .filter(estimation -> estimation.mProfitRate >= threshold)
          .collect(Collectors.toList());
      float profit = filtered.stream()
          .map(estimation -> new Odd45().calGain(estimation.mMatch, estimation))
          .mapToInt(value -> (int) (value * 10000)).sum() / 10000.00f;
      int victory = filtered.stream()
          .mapToInt(estimation -> new Odd45().calGain(estimation.mMatch, estimation) > 0 ? 1 : 0)
          .sum();
      System.out.println(String.format("总预测场次：%d, 胜率: %.2f, 盈利: %.2f",
          filtered.size(),
          victory * 1.00f / filtered.size(),
          profit));
    });

  }



  public Estimation estOneMatch(Map<String, Object> match) {
    float originalScoreOdd = valueOfFloat(match.get(ORIGINAL_SCORE_ODD));
    float openingScoreOdd = valueOfFloat(match.get(OPENING_SCORE_ODD));
    float minScoreOdd = mTimeMin > 0
        ? valueOfFloat(match.get(mTimePrefix + "scoreOdd"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD));
    int minHostScore = mTimeMin > 0 ? valueOfInt(match.get(mTimePrefix + "hostScore")) : 0;
    int minCustomScore = mTimeMin > 0 ? valueOfInt(match.get(mTimePrefix + "customScore")) : 0;
    int minScoreDistance = minHostScore - minCustomScore;
    final String ruleKey = StringUtils
        .join(new float[] {originalScoreOdd, openingScoreOdd, minScoreOdd, minScoreDistance}, '@');

    if (!mRuleKeys.contains(ruleKey)) {
      return null;
    }

    float estValue = mRules.get(ruleKey);
    float victoryRate = mRuleVictoryRate.get(ruleKey);
    float profitRate = mRuleProfitRate.get(ruleKey);
    int hostCount = mHostCount.get(ruleKey);
    int drewCount = mDrewCount.get(ruleKey);
    int customCount = mCustomCount.get(ruleKey);

    if (victoryRate < DEFAULT_MIN_VICTORY_RATE) {
      return null;
    }
    if (profitRate < DEFAULT_MIN_PROFIT_RATE) {
      return null;
    }
    if (hostCount + drewCount + customCount < DEFAULT_MIN_RULE_COUNT) {
      return null;
    }

    System.out.println(
        (hostCount + drewCount + customCount) + "@" + ruleKey + "@"
            + mRuleProfitRate.get(ruleKey) + "@" + mRuleVictoryRate.get(ruleKey) + "@"
            + mRules.get(ruleKey));
    float prob0 = estValue == 0 ? victoryRate : (1 - victoryRate);
    return new Estimation(match, estValue, prob0, 0, 1 - prob0, profitRate);
  }

  private void print() {
    mRules.keySet().stream()
        .filter(s -> mHostCount.get(s) + mDrewCount.get(s)
            + mCustomCount.get(s) >= DEFAULT_MIN_RULE_COUNT)
        .filter(s -> mRuleProfitRate.get(s) >= DEFAULT_MIN_PROFIT_RATE)
        .filter(s -> mRuleVictoryRate.get(s) >= DEFAULT_MIN_VICTORY_RATE)
        .sorted((o1, o2) -> mHostCount.get(o2) + mDrewCount.get(o2) + mCustomCount.get(o2)
            - (mHostCount.get(o1) + mDrewCount.get(o1) + mCustomCount.get(o1)))
        .forEach(ruleKey -> {
          int hostCount = mHostCount.get(ruleKey);
          int drewCount = mDrewCount.get(ruleKey);
          int customCount = mCustomCount.get(ruleKey);
          System.out.println(
              (hostCount + drewCount + customCount) + "@" + ruleKey + "@"
                  + mRuleProfitRate.get(ruleKey) + "@" + mRuleVictoryRate.get(ruleKey) + "@"
                  + mRules.get(ruleKey));
        });

    int total = mRules.keySet().stream().mapToInt(ruleKey -> {
      int hostCount = mHostCount.get(ruleKey);
      int drewCount = mDrewCount.get(ruleKey);
      int customCount = mCustomCount.get(ruleKey);
      return hostCount + drewCount + customCount;
    }).sum();

    System.out.println(total);
  }

  private void makeOne(String ruleKey) {
    int hostCount = mHostCount.get(ruleKey);
    int customCount = mCustomCount.get(ruleKey);
    float hostSum = mHostSum.get(ruleKey);
    float customSum = mCustomSum.get(ruleKey);

    int selectValue = hostSum > customSum ? 0 : 2;
    float profitRate = Math.max(hostSum, customSum) * 1.00f / (hostCount + customCount);
    float victoryRate = Math.max(hostCount, customCount) * 1.00f / (hostCount + customCount);
    mRules.put(ruleKey, selectValue);
    mRuleVictoryRate.put(ruleKey, victoryRate);
    mRuleProfitRate.put(ruleKey, profitRate);
  }


  private void calMatch(Map<String, Object> match) {
    float originalScoreOdd = valueOfFloat(match.get(ORIGINAL_SCORE_ODD));
    float openingScoreOdd = valueOfFloat(match.get(OPENING_SCORE_ODD));
    float minScoreOdd = mTimeMin > 0
        ? valueOfFloat(match.get(mTimePrefix + "scoreOdd"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD));
    float minScoreOddVictory = mTimeMin > 0
        ? valueOfFloat(match.get(mTimePrefix + "scoreOddOfVictory"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_VICTORY));
    float minScoreOddDefeat = mTimeMin > 0
        ? valueOfFloat(match.get(mTimePrefix + "scoreOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_DEFEAT));
    int hostScore = valueOfInt(match.get(HOST_SCORE));
    int customScore = valueOfInt(match.get(CUSTOM_SCORE));
    int minHostScore = mTimeMin > 0 ? valueOfInt(match.get(mTimePrefix + "hostScore")) : 0;
    int minCustomScore = mTimeMin > 0 ? valueOfInt(match.get(mTimePrefix + "customScore")) : 0;
    int minScoreDistance = minHostScore - minCustomScore;
    float deltaScore = (hostScore - minHostScore) - (customScore - minCustomScore) + minScoreOdd;

    final String ruleKey = StringUtils
        .join(new float[] {originalScoreOdd, openingScoreOdd, minScoreOdd, minScoreDistance}, '@');
    mRuleKeys.add(ruleKey);

    float hostSum = deltaScore >= 0.5f
        ? minScoreOddVictory
        : (deltaScore >= 0.25 ? (0.5f + 0.5f * minScoreOddVictory) : 0f);
    float customSum = deltaScore <= -0.5f
        ? minScoreOddDefeat
        : (deltaScore <= -0.25 ? (0.5f + 0.5f * minScoreOddDefeat) : 0f);
    float totalHostSum = (mHostSum.containsKey(ruleKey) ? mHostSum.get(ruleKey) : 0) + hostSum;
    float totalCustomSum =
        (mCustomSum.containsKey(ruleKey) ? mCustomSum.get(ruleKey) : 0) + customSum;
    mHostSum.put(ruleKey, totalHostSum);
    mCustomSum.put(ruleKey, totalCustomSum);

    int hostCount = deltaScore > 0 ? 1 : 0;
    int drewCount = deltaScore == 0 ? 1 : 0;
    int customCount = deltaScore < 0 ? 1 : 0;
    int totalHostCount =
        (mHostCount.containsKey(ruleKey) ? mHostCount.get(ruleKey) : 0) + hostCount;
    int totalDrewCount =
        (mDrewCount.containsKey(ruleKey) ? mDrewCount.get(ruleKey) : 0) + drewCount;
    int totalCustomCount =
        (mCustomCount.containsKey(ruleKey) ? mCustomCount.get(ruleKey) : 0) + customCount;
    mHostCount.put(ruleKey, totalHostCount);
    mDrewCount.put(ruleKey, totalDrewCount);
    mCustomCount.put(ruleKey, totalCustomCount);
  }

  public String buildSql() {
    String selectSql = "select hostScore, customScore, original_scoreOdd, opening_scoreOdd, "
        + (mTimeMin > 0 ? (mTimePrefix + "scoreOdd, ") : "")
        + (mTimeMin > 0
            ? (mTimePrefix + "scoreOddOfVictory, ")
            : (OPENING_SCORE_ODD_OF_VICTORY + ", "))
        + (mTimeMin > 0
            ? (mTimePrefix + "scoreOddOfDefeat, ")
            : (OPENING_SCORE_ODD_OF_DEFEAT + ", "))
        + (mTimeMin > 0 ? (mTimePrefix + "hostScore, ") : "")
        + (mTimeMin > 0 ? (mTimePrefix + "customScore, ") : "")
        + "1 "
        + "from football where 1=1 "
        + "and " + (mTimeMin > 0 ? (mTimePrefix + "scoreOddOfVictory>=1.7 ") : "1=1 ")
        + "and " + (mTimeMin > 0 ? (mTimePrefix + "scoreOddOfDefeat>=1.7 ") : "1=1 ");
    return selectSql + QueryHelper.SQL_AND + QueryHelper.SQL_ST;
  }
}

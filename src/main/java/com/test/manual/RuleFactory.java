package com.test.manual;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.test.Keys;
import com.test.db.QueryHelper;
import com.test.tools.Pair;

public class RuleFactory implements Keys {

  // 数据低于多少条则不要
  private static final int DEFAULT_MIN_RULE_COUNT = 300;
  // 最低胜率要求
  private static final float DEFAULT_MIN_VICTORY_RATE = 0.54f;
  // 最低盈利率要求
  private static final float DEFAULT_MIN_PROFIT_RATE = 1.02f;

  // 规则
  private final Map<String, Rule> mRules = new HashMap<>();
  private final RuleType mRuleType;

  public RuleFactory(RuleType ruleType) {
    mRuleType = ruleType;
  }

  public void build() throws Exception {
    mRules.clear();
    // 聚类训练, 摊到前后三分钟，增加训练数据量
    final int delay = 3;
    int start = -1, end = 80;
    // int start = 3, end = 6;
    for (int timeMin = start; timeMin <= end; timeMin++) {
      final long timeStart = System.currentTimeMillis();
      // 查询
      List<Map<String, Object>> train = QueryHelper.doQuery(buildSql(timeMin), 100_0000);
      final long queryEnd = System.currentTimeMillis();

      // 训练
      train(timeMin, timeMin, Math.min(end, timeMin + delay), train);
      final long trainEnd = System.currentTimeMillis();

      // 条件过滤
      filterByLimit(timeMin);
      // 随机抽样检测过滤
      int testRound = 5, testCount = train.size() / 5;
      for (int i = 0; i < testRound; i++) {
        Collections.shuffle(train);
        filterByTest(timeMin, train.subList(0, testCount));
      }
      final long testEnd = System.currentTimeMillis();

      System.out.println(String.format("模型构建中: %d', 查询耗时: %.1fs, 训练耗时: %.1fs, 校验耗时: %.1fs",
          timeMin,
          (queryEnd - timeStart) / 1000.0,
          (trainEnd - queryEnd) / 1000.0,
          (testEnd - trainEnd) / 1000.0));
    }

    // 持久化
    String rulesJson = new Gson().toJson(mRules);
    FileUtils.writeStringToFile(mRuleType.file(), rulesJson, "utf-8");

    // 输出结果
    mRules.values().stream()
        .sorted((o1, o2) -> (int) (o2.profitRate() * 1000 - o1.profitRate() * 1000))
        .forEach(rule -> System.out.println(rule.total() + "@" + rule.mRuleKey + "@"
            + rule.profitRate() + "@" + rule.victoryRate() + "@" + rule.value()));

    System.gc();
  }

  private void train(int valueMin, int keyMinStart, int keyMinEnd,
      List<Map<String, Object>> trains) {
    // 循环计算每场比赛的盈利
    trains.forEach(match -> {
      Pair<Float, Float> newGain = mRuleType.calGain(valueMin, match);
      for (int keyMin = keyMinStart; keyMin <= keyMinEnd; keyMin++) {
        final String ruleKey = mRuleType.calKey(keyMin, valueMin, match);
        final Rule rule = mRules.get(ruleKey);
        float totalHostSum = (rule != null ? rule.mHostProfit : 0) + newGain.first;
        float totalCustomSum = (rule != null ? rule.mCustomProfit : 0) + newGain.second;
        int hostTotal = (rule != null ? rule.mHostTotal : 0) + (newGain.first > 0 ? 1 : 0);
        int drewTotal = (rule != null ? rule.mDrewTotal : 0)
            + ((newGain.first == 0 && newGain.second == 0) ? 1 : 0);
        int customTotal = (rule != null ? rule.mCustomTotal : 0) + (newGain.second > 0 ? 1 : 0);

        Rule newRule = new Rule(mRuleType, ruleKey, keyMin, hostTotal, drewTotal, customTotal,
            totalHostSum, totalCustomSum);
        mRules.put(ruleKey, newRule);
      }
    });
  }

  private void filterByLimit(int timeMin) {
    Set<String> keySet = new HashSet<>(mRules.keySet());
    keySet.stream()
        .filter(ruleKey -> mRules.get(ruleKey).mTimeMin == timeMin)
        .filter(ruleKey -> {
          final Rule rule = mRules.get(ruleKey);
          return rule.total() < DEFAULT_MIN_RULE_COUNT
              || rule.profitRate() < DEFAULT_MIN_PROFIT_RATE
              || rule.victoryRate() < DEFAULT_MIN_VICTORY_RATE;
        })
        .forEach(mRules::remove);
  }

  private void filterByTest(int timeMin, List<Map<String, Object>> test) {
    Set<String> keySet = new HashSet<>(mRules.keySet());
    keySet.forEach(ruleKey -> {
      final Rule rule = mRules.get(ruleKey);
      if (rule.mTimeMin != timeMin) {
        return;
      }
      final AtomicInteger applied = new AtomicInteger();
      double profit = test.stream().mapToDouble(match -> {
        final String newRuleKey = mRuleType.calKey(timeMin, timeMin, match);
        if (!newRuleKey.equals(ruleKey)) {
          return 0;
        }
        Pair<Float, Float> newGain = rule.mType.calGain(timeMin, match);
        if (newGain.first == 0 && newGain.second == 0) {
          return 0; // 和
        }
        applied.getAndIncrement();
        return rule.value() == 0 ? newGain.first : newGain.second;
      }).sum();

      final int total = applied.get();
      double profitRate = profit / (total + 0.01);
      if (total < 10 || profitRate < DEFAULT_MIN_PROFIT_RATE) {
        mRules.remove(ruleKey);
      }
      // System.out.println(rule.total() + "@" + ruleKey + ", -> " + total + " => " + profitRate);
    });
  }


  private String buildSql(int timeMin) {
    String timePrefix = "min" + timeMin + "_";
    String selectSql =
        "select hostScore, customScore, original_scoreOdd, original_bigOdd, opening_scoreOdd, opening_bigOdd, "
            + "original_victoryOdd, opening_victoryOdd,"
            + (timeMin > 0 ? (timePrefix + "scoreOdd, ") : "")
            + (timeMin > 0
                ? (timePrefix + "scoreOddOfVictory, ")
                : (OPENING_SCORE_ODD_OF_VICTORY + ", "))
            + (timeMin > 0
                ? (timePrefix + "scoreOddOfDefeat, ")
                : (OPENING_SCORE_ODD_OF_DEFEAT + ", "))

            + (timeMin > 0 ? (timePrefix + "bigOdd, ") : "")
            + (timeMin > 0
                ? (timePrefix + "bigOddOfVictory, ")
                : (OPENING_BIG_ODD_OF_VICTORY + ", "))
            + (timeMin > 0
                ? (timePrefix + "bigOddOfDefeat, ")
                : (OPENING_BIG_ODD_OF_DEFEAT + ", "))

            + (timeMin > 0 ? (timePrefix + "hostScore, ") : "")
            + (timeMin > 0 ? (timePrefix + "customScore, ") : "")
            + (timeMin > 0 ? (timePrefix + "hostBestShoot, ") : "")
            + (timeMin > 0 ? (timePrefix + "customBestShoot, ") : "")
            + (timeMin > 0 ? (timePrefix + "hostDanger, ") : "")
            + (timeMin > 0 ? (timePrefix + "customDanger, ") : "")
            + "1 "
            + "from football where 1=1 "
            + "and " + (timeMin > 0 ? (timePrefix + "scoreOddOfVictory>=1.7 ") : "1=1 ")
            + "and " + (timeMin > 0 ? (timePrefix + "scoreOddOfDefeat>=1.7 ") : "1=1 ");

    return selectSql + QueryHelper.SQL_AND + QueryHelper.SQL_ST + " ";
  }
}

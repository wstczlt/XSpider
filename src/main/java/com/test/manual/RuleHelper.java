package com.test.manual;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.test.Keys;
import com.test.db.QueryHelper;
import com.test.tools.Pair;

public class RuleHelper implements Keys {

  // 数据库查询条数
  private static final int DEFAULT_SQL_COUNT = 1000000;
  // 数据低于多少条则不要
  private static final int DEFAULT_MIN_RULE_COUNT = 200;
  // 最低胜率要求
  private static final float DEFAULT_MIN_VICTORY_RATE = 0.55f;
  // 最低盈利率要求
  private static final float DEFAULT_MIN_PROFIT_RATE = 1.05f;

  // 规则
  private final Map<String, Rule> mRules = new HashMap<>();
  private final RuleType mRuleType;

  public RuleHelper(RuleType ruleType) {
    mRuleType = ruleType;
  }

  public void calRules() throws Exception {
    mRules.clear();
    List<Integer> timeMinArray = new ArrayList<>();
    for (int i = -1; i <= 80; i = i + 1) {
      timeMinArray.add(i);
    }
    for (int timeMin : timeMinArray) {
      List<Map<String, Object>> matches = QueryHelper.doQuery(buildSql(timeMin), DEFAULT_SQL_COUNT);
      Collections.shuffle(matches); // 打散
      int trainCount = (int) (matches.size() * 0.7);
      List<Map<String, Object>> trains = matches.subList(0, trainCount);
      List<Map<String, Object>> test = matches.subList(trainCount, matches.size());
      final Map<String, Rule> rules = new HashMap<>();

      // 聚类训练
      train(timeMin, trains, rules);
      // 删除无用结果
      filter(timeMin, test, rules);
      // 保存有用的结果
      mRules.putAll(rules);
    }

    // 持久化
    String rulesJson = new Gson().toJson(mRules);
    FileUtils.writeStringToFile(new File("rules_" + mRuleType.name().toLowerCase() + ".txt"),
        rulesJson, "utf-8");

    // 输出结果
    mRules.values().stream()
        .sorted((o1, o2) -> (int) (o2.profitRate() * 1000 - o1.profitRate() * 1000))
        .forEach(rule -> System.out.println(rule.total() + "@" + rule.mRuleKey + "@"
            + rule.profitRate() + "@" + rule.victoryRate() + "@" + rule.value()));
  }

  private void train(int timeMin, List<Map<String, Object>> trains, Map<String, Rule> rules) {
    // 循环计算每场比赛的盈利
    trains.forEach(match -> {
      final String ruleKey = ruleKey(match, timeMin);
      Rule rule = rules.get(ruleKey);
      Pair<Float, Float> newGain = mRuleType.mGainFunc.apply(new Pair<>(timeMin, match));

      float totalHostSum = (rule != null ? rule.mHostProfit : 0) + newGain.first;
      float totalCustomSum = (rule != null ? rule.mCustomProfit : 0) + newGain.second;
      int hostTotal = (rule != null ? rule.mHostTotal : 0) + (newGain.first > 0 ? 1 : 0);
      int drewTotal = (rule != null ? rule.mDrewTotal : 0)
          + ((newGain.first == 0 && newGain.second == 0) ? 1 : 0);
      int customTotal = (rule != null ? rule.mCustomTotal : 0) + (newGain.second > 0 ? 1 : 0);

      Rule newRule = new Rule(mRuleType, ruleKey, timeMin, hostTotal, drewTotal, customTotal,
          totalHostSum, totalCustomSum);
      rules.put(ruleKey, newRule);
    });
  }

  private void filter(int timeMin, List<Map<String, Object>> test, Map<String, Rule> rules) {
    Set<String> keySet = new HashSet<>(rules.keySet());
    keySet.stream()
        .filter(ruleKey -> {
          final Rule rule = rules.get(ruleKey);
          boolean select = rule.total() >= DEFAULT_MIN_RULE_COUNT
              && rule.profitRate() >= DEFAULT_MIN_PROFIT_RATE
              && rule.victoryRate() >= DEFAULT_MIN_VICTORY_RATE;

          return select || rules.remove(ruleKey) == null;
        })
        .filter(ruleKey -> {
          AtomicInteger cnt = new AtomicInteger();
          double profit = test.stream().mapToDouble(match -> {
            final String newRuleKey = ruleKey(match, timeMin);
            if (!newRuleKey.equals(ruleKey)) {
              return 0;
            }
            cnt.getAndIncrement();
            Rule rule = rules.get(ruleKey);
            Pair<Float, Float> newGain = rule.mType.mGainFunc.apply(new Pair<>(timeMin, match));
            if (newGain.first == 0 && newGain.second == 0) {
              return 0; // 和
            }
            return rule.value() == 0 ? newGain.first - 1 : newGain.second - 1;
          }).sum();

          System.out.println(ruleKey + ",  -> " + cnt.get() + " => " + profit);
          boolean select = profit > 0;
          return select || rules.remove(ruleKey) == null;
        }).forEach(s -> {});
  }


  private String ruleKey(Map<String, Object> match, int timeMin) {
    String timePrefix = "min" + timeMin + "_";
    int timeZone = timeMin <= 0 ? -1 : timeMin;
    int minHostScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostScore"));
    int minCustomScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customScore"));
    float openingScoreOdd = valueOfFloat(match.get(OPENING_SCORE_ODD));
    float openingBallOdd = valueOfFloat(match.get(OPENING_BIG_ODD));
    float minScoreOdd = timeMin <= 0
        ? valueOfFloat(match.get(ORIGINAL_SCORE_ODD))
        : valueOfFloat(match.get(timePrefix + "scoreOdd"));
    float minBallOdd = timeMin <= 0
        ? valueOfFloat(match.get(OPENING_BIG_ODD))
        : valueOfFloat(match.get(timePrefix + "bigOdd"));

    return StringUtils.join(new float[] {timeZone, minHostScore, minCustomScore,
        openingScoreOdd, openingBallOdd, minScoreOdd, minBallOdd},
        '@');
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
            + "1 "
            + "from football where 1=1 "
            + "and " + (timeMin > 0 ? (timePrefix + "scoreOddOfVictory>=1.7 ") : "1=1 ")
            + "and " + (timeMin > 0 ? (timePrefix + "scoreOddOfDefeat>=1.7 ") : "1=1 ");

    return selectSql + QueryHelper.SQL_AND + QueryHelper.SQL_ST + " order by random() ";
  }
}

package com.test.manual;

import static com.test.tools.Utils.valueOfInt;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.Keys;
import com.test.entity.Estimation;

public class RuleEval implements Keys {

  private final Set<Rule> mRules;

  public RuleEval() {
    mRules = new HashSet<>();
    final Type typeOfT = new TypeToken<Map<String, Rule>>() {}.getType();
    try {
      String scoreJson = FileUtils.readFileToString(RuleType.SCORE.file(), "utf-8");
      String ballJson = FileUtils.readFileToString(RuleType.BALL.file(), "utf-8");
      Map<String, Rule> scoreRules = new Gson().fromJson(scoreJson, typeOfT);
      Map<String, Rule> ballRules = new Gson().fromJson(ballJson, typeOfT);
      mRules.addAll(scoreRules.values());
      mRules.addAll(ballRules.values());
    } catch (Exception ignore) {}
  }

  public List<Estimation> evalEst(int nowMin, Map<String, Object> match) {
    final String nowTimePrefix = "min" + nowMin + "_";
    final int nowHostScore = valueOfInt(match.get(nowTimePrefix + "hostScore"));
    final int nowCustomScore = valueOfInt(match.get(nowTimePrefix + "customScore"));

    return evalRules(nowMin, match).stream().filter(rule -> {
      final int timeMin = rule.mTimeMin;
      final String timePrefix = "min" + timeMin + "_";
      int minHostScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostScore"));
      int minCustomScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customScore"));
      // 比分发生了则抛弃
      return minHostScore == nowHostScore && minCustomScore == nowCustomScore;
    }).map(rule -> new Estimation(rule, match, rule.value(), rule.prob0(), rule.prob1(),
        rule.prob2(), rule.profitRate())).collect(Collectors.toList());
  }


  public List<Rule> evalRules(int nowMin, Map<String, Object> match) {
    Set<String> keySet = new HashSet<>();
    List<Rule> rules = new ArrayList<>();
    IntStream.range(-1, nowMin).forEach(timeMin -> {
      final String timePrefix = "min" + timeMin + "_";
      int minHostScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "hostScore"));
      int minCustomScore = timeMin <= 0 ? 0 : valueOfInt(match.get(timePrefix + "customScore"));
      // 用于去重
      final String duplicateKey = minHostScore + "@" + minCustomScore;
      // 遍历规则
      final String scoreKey = RuleType.SCORE.calKey(timeMin, timeMin, match);
      // 按盈利率从大到小
      mRules.stream()
          .filter(rule -> scoreKey.equals(rule.mRuleKey))
          // 去重(相同比分同类型盘口不重复推)
          .filter(rule -> keySet.add(duplicateKey + "@" + rule.mType))
          // 最大盈利率
          .max((o1, o2) -> (int) (o1.profitRate() * 100 - o2.profitRate() * 100))
          .ifPresent(rules::add);

      final String ballKey = RuleType.BALL.calKey(timeMin, timeMin, match);
      mRules.stream()
          .filter(rule -> ballKey.equals(rule.mRuleKey))
          // 去重(相同分数同类型盘口不重复推)
          .filter(rule -> keySet.add(duplicateKey + "@" + rule.mType))
          // 最大盈利率
          .max((o1, o2) -> (int) (o1.profitRate() * 100 - o2.profitRate() * 100))
          .ifPresent(rules::add);
    });

    return rules;
  }

}

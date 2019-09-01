package com.test.manual;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 通过组合初盘，临场盘，比分等来发现最优购买方案.
 */
public class RuleEval0 extends RuleEval {

  private final Set<Rule> mRules;

  public RuleEval0() {
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

  @Override
  public List<Rule> eval(int timeMin, Map<String, Object> match) {
    final List<Rule> rules = new ArrayList<>();
    // 遍历规则
    final String scoreKey = RuleType.SCORE.calKey(timeMin, timeMin, match);
    // 按盈利率从大到小
    mRules.stream()
        .filter(rule -> scoreKey.equals(rule.mRuleKey))
        // 最大盈利率
        .max((o1, o2) -> (int) (o1.profitRate() * 100 - o2.profitRate() * 100))
        .ifPresent(rules::add);

    final String ballKey = RuleType.BALL.calKey(timeMin, timeMin, match);
    mRules.stream()
        .filter(rule -> ballKey.equals(rule.mRuleKey))
        // 最大盈利率
        .max((o1, o2) -> (int) (o1.profitRate() * 100 - o2.profitRate() * 100))
        .ifPresent(rules::add);

    return rules;
  }
}

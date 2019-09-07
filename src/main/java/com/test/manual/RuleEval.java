package com.test.manual;

import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.test.Keys;
import com.test.entity.Estimation;

public abstract class RuleEval implements Keys {

  public final List<Estimation> evalEst(int nowMin, Map<String, Object> match) {
    final String nowTimePrefix = "min" + nowMin + "_";
    final int nowHostScore = valueOfInt(match.get(nowTimePrefix + "hostScore"));
    final int nowCustomScore = valueOfInt(match.get(nowTimePrefix + "customScore"));

    return evalRules(nowMin, match).stream().filter(rule -> {
      final int timeMin = rule.mTimeMin;
      final String timePrefix = "min" + timeMin + "_";
      int minHostScore = valueOfInt(match.get(timePrefix + "hostScore"));
      int minCustomScore = valueOfInt(match.get(timePrefix + "customScore"));
      // 比分发生了则抛弃
      return minHostScore == nowHostScore && minCustomScore == nowCustomScore;
    }).map(rule -> new Estimation(rule, match, rule.value(), rule.prob0(), rule.prob1(),
        rule.prob2(), rule.profitRate())).collect(Collectors.toList());
  }

  public final List<Rule> evalRules(int nowMin, Map<String, Object> match) {
    Set<String> keySet = new HashSet<>();
    List<Rule> rules = new ArrayList<>();
    for (int timeMin = 0; timeMin <= nowMin; timeMin++) {
      // 用于去重, 一场比赛每个盘口类型(让球/大小球)只预测一次
      final List<Rule> newRules = eval(timeMin, match);

      for (Rule rule : newRules) {
        final String duplicateKey = rule.mType.name();
        if (keySet.contains(duplicateKey)) {
          continue;
        }
        keySet.add(duplicateKey);
        rules.add(rule);
      }
    }

    return rules;
  }


  public abstract List<Rule> eval(int timeMin, Map<String, Object> match);

}

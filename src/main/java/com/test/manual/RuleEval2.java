package com.test.manual;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 大比分领先时，购买小球来获得盈利.
 */
public class RuleEval2 extends RuleEval {

  @Override
  public List<Rule> eval(int timeMin, Map<String, Object> match) {
    final List<Rule> list = new ArrayList<>();
    if (timeMin <= 30 || timeMin >= 35) {
      return list;
    }
    final String timePrefix = "min" + timeMin + "_";
    int hostScore = valueOfInt(match.get(timePrefix + "hostScore"));
    int customScore = valueOfInt(match.get(timePrefix + "customScore"));
    float openingBigOdd = valueOfFloat(match.get("min0_bigOdd"));
    float openingBigOddOfVictory = valueOfFloat(match.get("min0_bigOddOfVictory"));
    float openingBigOddOfDefeat = valueOfFloat(match.get("min0_bigOddOfDefeat"));
    float openingScoreOdd = valueOfFloat(match.get("min0_scoreOdd"));
    float openingScoreOddOfVictory = valueOfFloat(match.get("min0_scoreOddOfVictory"));
    float openingScoreOddOfDefeat = valueOfFloat(match.get("min0_scoreOddOfDefeat"));
    float bigOdd = valueOfFloat(match.get(timePrefix + "bigOdd"));
    float bigOddOfDefeat = valueOfFloat(timePrefix + "bigOddOfDefeat");
    int scoreAll = hostScore + customScore;
    int scoreDis = hostScore - customScore;

    if (openingBigOdd >= 4 || openingBigOddOfVictory <= 1.7 || openingBigOddOfDefeat <= 1.7) {
      return list;
    }

    if (Math.abs(openingScoreOdd) >= 1.5 || openingScoreOddOfVictory <= 1.7
        || openingScoreOddOfDefeat <= 1.7) {
      return list;
    }

    // 要求起码再进3球以上
    if (bigOdd <= scoreAll + 2.25) {
      return list;
    }
    // 3球差距以上
    if (scoreDis <= 2 && scoreDis >= -2) {
      return list;
    }
    // 小球赔率不能太低
    if (bigOddOfDefeat <= 1.7) {
      return list;
    }


    final Rule newRule = new Rule(RuleType.BALL, "", timeMin,
        0, 0, 1,
        0, 2);

    return Collections.singletonList(newRule);
  }
}

package com.test.manual;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;
import static com.test.tools.Utils.valueOfLong;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.function.Consumer;

import com.test.Keys;
import com.test.entity.Estimation;
import com.test.tools.Utils;

public class HistoryConsumer implements Consumer<Estimation>, Keys {

  @Override
  public void accept(Estimation est) {
    if (!(est.mModel instanceof Rule)) {
      return;
    }

    final Rule rule = (Rule) est.mModel;
    final Map<String, Object> match = est.mMatch;
    String hostName = (String) match.get(HOST_NAME);
    String customName = (String) match.get(CUSTOM_NAME);
    String league = (String) match.get(LEAGUE);
    String matchID = match.get(MATCH_ID) + "";

    int timeMin = rule.mTimeMin;
    int nowMin = Utils.valueOfInt(match.get(TIME_MIN));
    long matchTime = valueOfLong(match.get(MATCH_TIME));
    int matchStatus = valueOfInt(match.get(MATCH_STATUS));
    final int hostScore = valueOfInt(match.get(HOST_SCORE));
    final int customScore = valueOfInt(match.get(CUSTOM_SCORE));

    final String timePrefix = "min" + timeMin + "_";
    final String nowTimePrefix = "min" + nowMin + "_";
    int minHostScore = valueOfInt(match.get(timePrefix + "hostScore"));
    int minCustomScore = valueOfInt(match.get(timePrefix + "customScore"));
    float minScoreOdd = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOdd"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD));
    float minScoreOddVictory = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOddOfVictory"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_VICTORY));
    float minScoreOddDefeat = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_DEFEAT));
    float minBallOdd = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOdd"))
        : valueOfFloat(match.get(OPENING_BIG_ODD));
    float minBallOddVictory = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOddOfVictory"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_VICTORY));
    float minBallOddDefeat = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_DEFEAT));


    float nowScoreOdd = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "scoreOdd"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD));
    float nowScoreOddVictory = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "scoreOddOfVictory"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_VICTORY));
    float nowScoreOddDefeat = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "scoreOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_DEFEAT));
    float nowBallOdd = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "bigOdd"))
        : valueOfFloat(match.get(OPENING_BIG_ODD));
    float nowBallOddVictory = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "bigOddOfVictory"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_VICTORY));
    float nowBallOddDefeat = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "bigOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_DEFEAT));

    String matchTimeStr =
        new SimpleDateFormat("yyyy-MM-dd HH:mm").format(matchTime - 8 * 3600 * 1000);
    String matchStatusStr =
        matchStatus == 0 ? "未开始" : (matchStatus == 1 ? "进行中" : (matchStatus == 3 ? "已结束" : "未知"));

    System.out.println(String.format("%s, 状态: %s", matchTimeStr, matchStatusStr));
    System.out.println(
        String.format("%d' [%s], %s VS %s, [%s]", nowMin, league, hostName, customName, matchID));


    System.out.println(String.format("     当前时刻: %d' [比分: %d : %d],  盘口: %s， 赔率: %.2f",
        nowMin,  hostScore, customScore,
        rule.mType == RuleType.SCORE
            ? (nowScoreOdd + "[" + (est.mValue == 0 ? "主" : "客") + "]")
            : (nowBallOdd + "[" + (est.mValue == 0 ? "大" : "小") + "]"),
        rule.mType == RuleType.SCORE
            ? (est.mValue == 0 ? nowScoreOddVictory : nowScoreOddDefeat)
            : (est.mValue == 0 ? nowBallOddVictory : nowBallOddDefeat)));


    System.out.println(String.format("     预测时刻: %d' [比分: %d : %d], 盘口: %s， 赔率: %.2f",
        timeMin, minHostScore, minCustomScore,
        rule.mType == RuleType.SCORE
            ? (minScoreOdd + "[" + (est.mValue == 0 ? "主" : "客") + "]")
            : (minBallOdd + "[" + (est.mValue == 0 ? "大" : "小") + "]"),
        rule.mType == RuleType.SCORE
            ? (est.mValue == 0 ? minScoreOddVictory : minScoreOddDefeat)
            : (est.mValue == 0 ? minBallOddVictory : minBallOddDefeat)));

    System.out.println(
        String.format("     历史胜率:  %.2f [%.2f, %.2f, %.2f]，历史盈利率: %.2f",
            est.mProbability, est.mProb0, est.mProb1, est.mProb2, est.mProfitRate));
    System.out.println("\n\n");
  }
}

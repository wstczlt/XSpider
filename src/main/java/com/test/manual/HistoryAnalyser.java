package com.test.manual;

import static com.test.db.QueryHelper.SQL_AND;
import static com.test.db.QueryHelper.SQL_BASE;
import static com.test.db.QueryHelper.SQL_ORDER;
import static com.test.db.QueryHelper.SQL_ST;
import static com.test.manual.HistoryHelper.ballKeys;
import static com.test.manual.HistoryHelper.fullKeys;
import static com.test.manual.HistoryHelper.oddKeys;
import static com.test.manual.HistoryHelper.similar;
import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.test.Keys;
import com.test.db.QueryHelper;

public class HistoryAnalyser implements Keys {

  private static final int MIN_SIMILAR_COUNT = 100;

  public static void main(String[] args) throws Exception {
    String querySql = SQL_BASE + SQL_AND + SQL_ST + SQL_ORDER;
    List<Map<String, Object>> matches = QueryHelper.doQuery(querySql, 100);
    List<HistorySuggest> suggests = new ArrayList<>();
    for (Map<String, Object> match : matches) {
      for (int timeMin = 0; timeMin <= 80; timeMin = timeMin + 10) {
        HistorySuggest historySuggest = suggest(timeMin, match);
        System.out.println("matchID=" + match.get(MATCH_ID) + ", timeMin=" + timeMin);
        if (historySuggest.mTotalCount <= 0) {
          System.out.println("没有相似数据.");
        }
        if (historySuggest.mScoreProfit < 1f) {
          System.out.println("没有推荐让分");
        } else {
          System.out.println(String.format("让分(%d), 时间: %d, 盘口: %.2f[%s], 胜率: %.2f, 盈利率: %.2f",
              historySuggest.mTotalCount, timeMin, historySuggest.mScoreOdd,
              historySuggest.mScoreValue == 0 ? "主" : "客",
              (historySuggest.mScoreValue == 0
                  ? historySuggest.mScoreProb0
                  : historySuggest.mScoreProb2) + historySuggest.mScoreProb1,
              historySuggest.mScoreProfit));
        }

        if (historySuggest.mBallProfit < 1f) {
          System.out.println("没有推荐大小球");
        } else {
          System.out.println(String.format("大小球(%d), 时间: %d, 盘口: %.2f[%s], 胜率: %.2f, 盈利率: %.2f",
              historySuggest.mTotalCount, timeMin, historySuggest.mBallOdd,
              historySuggest.mBallValue == 0 ? "大" : "小",
              (historySuggest.mBallValue == 0
                  ? historySuggest.mBallProb0
                  : historySuggest.mBallProb2) + historySuggest.mBallProb1,
              historySuggest.mBallProfit));
        }

      }
    }

  }

  public static HistorySuggest suggest(int timeMin, Map<String, Object> match) throws Exception {
    HistorySuggest suggestOfFull = suggest(timeMin, similar(fullKeys(timeMin), match));
    if (suggestOfFull.mTotalCount >= MIN_SIMILAR_COUNT) {
      return suggestOfFull;
    }

    HistorySuggest suggestOfScore = suggest(timeMin, similar(oddKeys(timeMin), match));
    HistorySuggest suggestOfBall = suggest(timeMin, similar(ballKeys(timeMin), match));
    return new HistorySuggest(suggestOfScore.mTotalCount + suggestOfBall.mTotalCount,
        suggestOfScore.mScoreOdd, suggestOfScore.mScoreValue, suggestOfScore.mScoreProfit,
        suggestOfScore.mScoreProb0,
        suggestOfScore.mScoreProb1, suggestOfScore.mScoreProb2,
        suggestOfBall.mBallOdd, suggestOfBall.mBallValue, suggestOfBall.mBallProfit,
        suggestOfBall.mBallProb0,
        suggestOfBall.mBallProb1, suggestOfBall.mBallProb2);
  }

  public static HistorySuggest suggest(int timeMin, List<Map<String, Object>> matches) {
    float totalScoreHostSum = 0, totalScoreCustomSum = 0;
    float totalScoreHostCount = 0, totalScoreCustomCount = 0, totalScoreDrewCount = 0;

    float totalBallHostSum = 0, totalBallCustomSum = 0;
    float totalBallHostCount = 0, totalBallCustomCount = 0, totalBallDrewCount = 0;
    float minScoreOdd = 999, minBallOdd = 999;
    for (Map<String, Object> match : matches) {
      final String timePrefix = "min" + timeMin + "_";
      minScoreOdd = timeMin > 0
          ? valueOfFloat(match.get(timePrefix + "scoreOdd"))
          : valueOfFloat(match.get(OPENING_SCORE_ODD));
      float minScoreOddVictory = timeMin > 0
          ? valueOfFloat(match.get(timePrefix + "scoreOddOfVictory"))
          : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_VICTORY));
      float minScoreOddDefeat = timeMin > 0
          ? valueOfFloat(match.get(timePrefix + "scoreOddOfDefeat"))
          : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_DEFEAT));
      minBallOdd = timeMin > 0
          ? valueOfFloat(match.get(timePrefix + "bigOdd"))
          : valueOfFloat(match.get(OPENING_BIG_ODD));
      float minBallOddOfVictory = timeMin > 0
          ? valueOfFloat(match.get(timePrefix + "bigOddOfVictory"))
          : valueOfFloat(match.get(OPENING_BIG_ODD_OF_VICTORY));
      float minBallOddOfDefeat = timeMin > 0
          ? valueOfFloat(match.get(timePrefix + "bigOddOfDefeat"))
          : valueOfFloat(match.get(OPENING_BIG_ODD_OF_DEFEAT));

      int hostScore = valueOfInt(match.get(HOST_SCORE));
      int customScore = valueOfInt(match.get(CUSTOM_SCORE));
      int minHostScore = timeMin > 0 ? valueOfInt(match.get(timePrefix + "hostScore")) : 0;
      int minCustomScore = timeMin > 0 ? valueOfInt(match.get(timePrefix + "customScore")) : 0;
      float deltaOddScore =
          (hostScore - minHostScore) - (customScore - minCustomScore) + minScoreOdd;
      float deltaBallScore = hostScore + customScore - minBallOdd;

      float hostScoreSum = deltaOddScore >= 0.5f
          ? minScoreOddVictory
          : (deltaOddScore >= 0.25
              ? (0.5f + 0.5f * minScoreOddVictory)
              : 0);
      float customScoreSum = deltaOddScore <= -0.5f
          ? minScoreOddDefeat
          : (deltaOddScore <= -0.25
              ? (0.5f + 0.5f * minScoreOddDefeat)
              : 0);
      totalScoreHostSum += hostScoreSum;
      totalScoreCustomSum += customScoreSum;
      totalScoreHostCount += (deltaOddScore > 0 ? 1 : 0);
      totalScoreCustomCount += (deltaOddScore < 0 ? 1 : 0);
      totalScoreDrewCount += (deltaOddScore == 0 ? 1 : 0);

      float hostBallSum = deltaBallScore >= 0.5f
          ? minBallOddOfVictory
          : (deltaBallScore >= 0.25f ? (0.5f + 0.5f * minBallOddOfVictory) : 0f);
      float customBallSum = deltaBallScore <= -0.5f
          ? minBallOddOfDefeat
          : (deltaBallScore >= 0.25f ? (0.5f + 0.5f * minBallOddOfDefeat) : 0f);
      totalBallHostSum += hostBallSum;
      totalBallCustomSum += customBallSum;
      totalBallHostCount += (deltaBallScore > 0 ? 1 : 0);
      totalBallCustomCount += (deltaBallScore < 0 ? 1 : 0);
      totalBallDrewCount += (deltaBallScore == 0 ? 1 : 0);
    }

    if (totalScoreHostCount + totalScoreCustomCount == 0) {
      return HistorySuggest.EMPTY;
    }

    final int totalCount = matches.size();
    final int scoreValue = totalScoreHostSum > totalScoreCustomSum ? 0 : 2;
    final float scoreProfit = Math.max(totalScoreHostSum, totalScoreCustomSum)
        / (totalScoreHostCount + totalScoreCustomCount);
    final float scoreProb0 = 1.00f * totalScoreHostCount / totalCount;
    final float scoreProb1 = 1.00f * totalScoreDrewCount / totalCount;
    final float scoreProb2 = 1.00f * totalScoreCustomCount / totalCount;

    final int ballValue = totalBallHostSum > totalBallCustomSum ? 0 : 2;
    final float ballProfit = Math.max(totalBallHostSum, totalBallCustomSum)
        / (totalBallHostCount + totalBallCustomCount);
    final float ballProb0 = 1.00f * totalBallHostCount / totalCount;
    final float ballProb1 = 1.00f * totalBallDrewCount / totalCount;
    final float ballProb2 = 1.00f * totalBallCustomCount / totalCount;

    return new HistorySuggest(totalCount, minScoreOdd,
        scoreValue, scoreProfit, scoreProb0, scoreProb1, scoreProb2,
        minBallOdd, ballValue, ballProfit, ballProb0, ballProb1, ballProb2);
  }
}

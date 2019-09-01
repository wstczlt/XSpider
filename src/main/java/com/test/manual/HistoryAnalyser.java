package com.test.manual;

import static com.test.db.QueryHelper.SQL_AND;
import static com.test.db.QueryHelper.SQL_BASE;
import static com.test.db.QueryHelper.SQL_ST;
import static com.test.db.QueryHelper.similarQuery;
import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import com.test.Keys;
import com.test.db.QueryHelper;
import com.test.tools.Pair;

public class HistoryAnalyser implements Keys {

  private static final int MIN_SIMILAR_COUNT = 100;

  static class SumFunc implements ToDoubleFunction<Pair<HistorySuggest, Map<String, Object>>> {

    final int timeMin;

    public SumFunc(int timeMin) {
      this.timeMin = timeMin;
    }

    @Override
    public double applyAsDouble(Pair<HistorySuggest, Map<String, Object>> pair) {
      float hostScore = valueOfFloat(pair.second.get(HOST_SCORE));
      float customScore = valueOfFloat(pair.second.get(CUSTOM_SCORE));
      float minHostScore = valueOfFloat(pair.second.get("min" + timeMin + "_hostScore"));
      float minCustomScore = valueOfFloat(pair.second.get("min" + timeMin + "_customScore"));
      float minScoreOdd = valueOfFloat(pair.second.get("min" + timeMin + "_scoreOdd"));
      float minScoreOddVictory =
          valueOfFloat(pair.second.get("min" + timeMin + "_scoreOddOfVictory"));
      float minScoreOddDefeat =
          valueOfFloat(pair.second.get("min" + timeMin + "_scoreOddOfDefeat"));

      float deltaOddScore =
          (hostScore - minHostScore) - (customScore - minCustomScore) + minScoreOdd;

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

      return pair.first.mScoreValue == 0 ? hostScoreSum : customScoreSum;
    }
  }

  public static void main(String[] args) throws Exception {
    String querySql =
        SQL_BASE + "and league in ('英超', '中超') " + SQL_AND + SQL_ST + "order by RANDOM() ";
    List<Map<String, Object>> matches = QueryHelper.doQuery(querySql, 100);
    List<Pair<HistorySuggest, Map<String, Object>>> min0Suggests = new ArrayList<>();
    List<Pair<HistorySuggest, Map<String, Object>>> min40Suggests = new ArrayList<>();
    List<Pair<HistorySuggest, Map<String, Object>>> min60Suggests = new ArrayList<>();
    for (Map<String, Object> match : matches) {
      System.out
          .println("\n\n\nmatchID=" + match.get(MATCH_ID) + ", " + match.get(HOST_NAME) + " VS "
              + match.get(CUSTOM_NAME) + ", 比分: " + match.get(HOST_SCORE) + " - "
              + match.get(CUSTOM_SCORE));
      int[] timeMins = new int[] {0, 40, 60};
      for (int timeMin : timeMins) {
        HistorySuggest historySuggest = suggest(timeMin, match);
        System.out.println("\n时间: " + timeMin + ", 比分: " + match.get("min" + timeMin + "_hostScore")
            + " - " + match.get("min" + timeMin + "_customScore"));
        if (historySuggest.mTotalScoreCount < MIN_SIMILAR_COUNT) {
          System.out.println(
              String.format("让分(%d), 时间: %d, 推荐: 无", historySuggest.mTotalScoreCount, timeMin));
        } else {
          if (historySuggest.mScoreProfit >= 1.05 && timeMin == 0) {
            min0Suggests.add(new Pair<>(historySuggest, match));
          }
          if (historySuggest.mScoreProfit >= 1.05 && timeMin == 40) {
            min40Suggests.add(new Pair<>(historySuggest, match));
          }
          if (historySuggest.mScoreProfit >= 1.05 && timeMin == 60) {
            min60Suggests.add(new Pair<>(historySuggest, match));
          }
          System.out
              .println(String.format(
                  "让分(%d), 时间: %d, 盘口: %.2f[%s], 胜率: %.2f(%.2f, %.2f, %.2f), 盈利率: %.2f",
                  historySuggest.mTotalScoreCount, timeMin, historySuggest.mScoreOdd,
                  historySuggest.mScoreValue == 0 ? "主" : "客",
                  (historySuggest.mScoreValue == 0
                      ? historySuggest.mScoreProb0
                      : historySuggest.mScoreProb2)
                      / (historySuggest.mScoreProb0 + historySuggest.mScoreProb2),
                  historySuggest.mScoreProb0,
                  historySuggest.mScoreProb1,
                  historySuggest.mScoreProb2,
                  historySuggest.mScoreProfit));
        }
      }
    }

    double min0TotalSum = min0Suggests.stream().mapToDouble(new SumFunc(0)).sum();
    double min40TotalSum = min40Suggests.stream().mapToDouble(new SumFunc(40)).sum();
    double min60TotalSum = min60Suggests.stream().mapToDouble(new SumFunc(60)).sum();

    System.out.println(String.format("分钟: 00, 总场次: %d, 总盈利: %.2f, 盈利率: %.2f",
        min0Suggests.size(), min0TotalSum - min0Suggests.size(),
        (min0TotalSum - min0Suggests.size()) / min0Suggests.size()));

    System.out.println(String.format("分钟: 40, 总场次: %d, 总盈利: %.2f, 盈利率: %.2f",
        min40Suggests.size(), min40TotalSum - min40Suggests.size(),
        (min40TotalSum - min40Suggests.size()) / min40Suggests.size()));

    System.out.println(String.format("分钟: 60, 总场次: %d, 总盈利: %.2f, 盈利率: %.2f",
        min60Suggests.size(), min60TotalSum - min60Suggests.size(),
        (min60TotalSum - min60Suggests.size()) / min60Suggests.size()));
  }

  public static HistorySuggest suggest(int timeMin, Map<String, Object> match) throws Exception {
    // HistorySuggest suggestOfFull = suggest(timeMin, similar(fullKeys(timeMin), match));
    // suggestOfFull.mFullKeys = true;
    // System.out.println("精准查询: " + suggestOfFull.mTotalScoreCount);
    // if (suggestOfFull.mTotalScoreCount >= MIN_SIMILAR_COUNT) {
    // return suggestOfFull;
    // }

    return suggest(timeMin, similarQuery(timeMin, match));
    //
    // HistorySuggest suggestOfScore = suggest(timeMin, similar(oddKeys(timeMin), match));
    // HistorySuggest suggestOfBall = suggest(timeMin, similar(oddKeys(timeMin), match));
    // return new HistorySuggest(suggestOfScore.mScoreOdd, suggestOfScore.mScoreValue,
    // suggestOfScore.mTotalScoreCount, suggestOfScore.mScoreProfit,
    // suggestOfScore.mScoreProb0,
    // suggestOfScore.mScoreProb1, suggestOfScore.mScoreProb2,
    // suggestOfBall.mBallOdd, suggestOfBall.mBallValue, suggestOfBall.mTotalBallCount,
    // suggestOfBall.mBallProfit,
    // suggestOfBall.mBallProb0,
    // suggestOfBall.mBallProb1, suggestOfBall.mBallProb2);
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
          : (deltaBallScore <= -0.25f ? (0.5f + 0.5f * minBallOddOfDefeat) : 0f);
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

    return new HistorySuggest(minScoreOdd,
        scoreValue, totalCount, scoreProfit, scoreProb0, scoreProb1, scoreProb2,
        minBallOdd, ballValue, totalCount, ballProfit, ballProb0, ballProb1, ballProb2);
  }
}

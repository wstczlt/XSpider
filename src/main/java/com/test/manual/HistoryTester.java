package com.test.manual;

import static com.test.Config.SPIDER_THREAD_COUNT;
import static com.test.db.QueryHelper.SQL_AND;
import static com.test.db.QueryHelper.SQL_ORDER;
import static com.test.db.QueryHelper.SQL_SELECT;
import static com.test.db.QueryHelper.SQL_ST;
import static com.test.db.QueryHelper.buildSqlIn;
import static com.test.db.QueryHelper.doQuery;
import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import com.test.dszuqiu.DsHistoryJobFactory;
import com.test.dszuqiu.DsJobBuilder;
import com.test.entity.Estimation;
import com.test.http.HttpEngine;
import com.test.pipeline.DbPipeline;
import com.test.tools.Pair;

public class HistoryTester {

  public static final List<Integer> TEST_MATCHES = Collections.emptyList();
  // public static final List<Integer> TEST_MATCHES = Arrays.asList(657551, 657629);
  private static final RuleEval ruleEval = new RuleEval1();

  public static void testAndDisplay(int startDay, int zoneDays) throws Exception {
    List<Map<String, Object>> matches = queryHistoryMatch(startDay, zoneDays);

    for (int i = 0; i < matches.size(); i++) {
      final Map<String, Object> match = matches.get(i);
      ruleEval.evalRules(80, match)
          .forEach(rule -> new HistoryConsumer().accept(new Estimation(rule, match, rule.value(),
              rule.prob0(), rule.prob1(), rule.prob2(), rule.profitRate())));
    }
  }

  public static void testAndDisplay() throws Exception {
    int random = new Random().nextInt(100) + 12; // 周
    int zoneDays = 28;

    testAndDisplay(random * 7, zoneDays);
  }

  public static void fetchAndDisplay(int zoneDays) throws Exception {
    List<Map<String, Object>> matches = fetchNewMatch(zoneDays);

    for (int i = 0; i < matches.size(); i++) {
      final Map<String, Object> match = matches.get(i);
      ruleEval.evalRules(80, match)
          .forEach(rule -> new HistoryConsumer().accept(new Estimation(rule, match, rule.value(),
              rule.prob0(), rule.prob1(), rule.prob2(), rule.profitRate())));
    }
  }

  public static void testAndEval(int startDay, int zoneDays) throws Exception {
    List<Map<String, Object>> matches = queryHistoryMatch(startDay, zoneDays);

    doTest(matches);
  }

  public static void testAndEval() throws Exception {
    int random = new Random().nextInt(100) + 12; // 周
    int zoneDays = 28;

    testAndEval(random * 7, zoneDays);
  }

  public static void fetchAndEval(int zoneDays) throws Exception {
    List<Map<String, Object>> matches = fetchNewMatch(zoneDays);

    doTest(matches);
  }

  // 查询历史区间比赛
  private static List<Map<String, Object>> queryHistoryMatch(int startDay, int zoneDays)
      throws Exception {
    // startDay = 133;
    // zoneDays = 28;
    // 避不开的大坑
    if (startDay >= 115 && startDay <= 150) {
      startDay += 50;
    }
    SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd");
    sft.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    final long timeStart = sft.parse("2019-08-30").getTime() - (startDay + zoneDays) * 86400000L;
    final long timeEnd = sft.parse("2019-08-30").getTime() - startDay * 86400000L;
    String querySql = SQL_SELECT + SQL_AND + SQL_ST
        + "and cast(matchTime as bigint)>" + timeStart + " "
        + "and cast(matchTime as bigint)<=" + timeEnd + " "
        + SQL_ORDER;

    if (!TEST_MATCHES.isEmpty()) { // 测试目的
      querySql = SQL_SELECT + SQL_AND + SQL_ST + buildSqlIn(TEST_MATCHES);
    }
    System.out.println("startDay=" + startDay + "，zoneDays=" + zoneDays + " ["
        + sft.format(timeStart) + " - " + sft.format(timeEnd) + "]");
    return doQuery(querySql, 4000);
  }


  // 从当前日期开始的长度
  private static List<Map<String, Object>> fetchNewMatch(int zoneDays) throws Exception {
    // 运行爬虫
    final DbPipeline pipeline = new DbPipeline();
    DsHistoryJobFactory factory = new DsHistoryJobFactory(zoneDays, new DsJobBuilder());
    HttpEngine dragon = new HttpEngine(factory.build(), pipeline, SPIDER_THREAD_COUNT);
    dragon.start();

    String querySql = SQL_SELECT + SQL_AND + SQL_ST + buildSqlIn(factory.getMatchIDs());
    // System.out.println(querySql);

    return doQuery(querySql, 4000);
  }


  private static void doTest(List<Map<String, Object>> matches) {
    System.out.println("测试数量: " + matches.size());
    final boolean delay = false;
    final List<Float> thresholds = Arrays.asList(1.05f);

    // final List<Float> thresholds =
    // Arrays.asList(0.55f, 0.58f, 0.60f, 0.62f, 0.65f, 0.70f, 0.75f, 0.8f);

    final Map<Float, Float> sumGain = new HashMap<>();
    final Map<Float, AtomicInteger> allCount = new HashMap<>();
    final Map<Float, AtomicInteger> drewCount = new HashMap<>();
    final Map<Float, AtomicInteger> victoryCount = new HashMap<>();
    final Map<Float, AtomicInteger> defeatCount = new HashMap<>();

    final Map<Float, Float> sumScoreGain = new HashMap<>();
    final Map<Float, AtomicInteger> scoreCount = new HashMap<>();
    final Map<Float, AtomicInteger> drewScoreCount = new HashMap<>();
    final Map<Float, AtomicInteger> victoryScoreCount = new HashMap<>();
    final Map<Float, AtomicInteger> defeatScoreCount = new HashMap<>();


    final Map<Float, Float> sumUpGain = new HashMap<>();
    final Map<Float, AtomicInteger> upCount = new HashMap<>();
    final Map<Float, AtomicInteger> drewUpCount = new HashMap<>();
    final Map<Float, AtomicInteger> victoryUpCount = new HashMap<>();
    final Map<Float, AtomicInteger> defeatUpCount = new HashMap<>();


    final Map<Float, Float> sumBallGain = new HashMap<>();
    final Map<Float, AtomicInteger> ballCount = new HashMap<>();
    final Map<Float, AtomicInteger> drewBallCount = new HashMap<>();
    final Map<Float, AtomicInteger> victoryBallCount = new HashMap<>();
    final Map<Float, AtomicInteger> defeatBallCount = new HashMap<>();


    final Map<Float, Float> sumBigGain = new HashMap<>();
    final Map<Float, AtomicInteger> bigCount = new HashMap<>();
    final Map<Float, AtomicInteger> drewBigCount = new HashMap<>();
    final Map<Float, AtomicInteger> victoryBigCount = new HashMap<>();
    final Map<Float, AtomicInteger> defeatBigCount = new HashMap<>();

    thresholds.forEach(threshold -> {
      sumGain.put(threshold, 0f);
      allCount.put(threshold, new AtomicInteger());
      drewCount.put(threshold, new AtomicInteger());
      victoryCount.put(threshold, new AtomicInteger());
      defeatCount.put(threshold, new AtomicInteger());

      sumScoreGain.put(threshold, 0f);
      scoreCount.put(threshold, new AtomicInteger());
      drewScoreCount.put(threshold, new AtomicInteger());
      victoryScoreCount.put(threshold, new AtomicInteger());
      defeatScoreCount.put(threshold, new AtomicInteger());

      sumUpGain.put(threshold, 0f);
      upCount.put(threshold, new AtomicInteger());
      drewUpCount.put(threshold, new AtomicInteger());
      victoryUpCount.put(threshold, new AtomicInteger());
      defeatUpCount.put(threshold, new AtomicInteger());

      sumBallGain.put(threshold, 0f);
      ballCount.put(threshold, new AtomicInteger());
      drewBallCount.put(threshold, new AtomicInteger());
      victoryBallCount.put(threshold, new AtomicInteger());
      defeatBallCount.put(threshold, new AtomicInteger());

      sumBigGain.put(threshold, 0f);
      bigCount.put(threshold, new AtomicInteger());
      drewBigCount.put(threshold, new AtomicInteger());
      victoryBigCount.put(threshold, new AtomicInteger());
      defeatBigCount.put(threshold, new AtomicInteger());
    });

    for (int i = 0; i < matches.size(); i++) {
      final Map<String, Object> match = matches.get(i);
      ruleEval.evalRules(80, match).stream().filter(rule -> {
        if (!delay) {
          return true;
        }
        // 考虑实际的购买情况，操作是有延迟的，并且有时候需要等水
        final String valueTimePrefix = "min" + rule.mTimeMin + "_";
        final int valueHostScore = valueOfInt(match.get(valueTimePrefix + "hostScore"));
        final int valueCustomScore = valueOfInt(match.get(valueTimePrefix + "customScore"));

        int nowMin = rule.mTimeMin + 5; // 限定五分钟内不进球(方便操作购买，但是会影响大球胜率)
        final String nowTimePrefix = "min" + nowMin + "_";
        final int nowHostScore = valueOfInt(match.get(nowTimePrefix + "hostScore"));
        final int nowCustomScore = valueOfInt(match.get(nowTimePrefix + "customScore"));
        return nowHostScore == valueHostScore && nowCustomScore == valueCustomScore;
      }).forEach(rule -> {
        final Pair<Float, Float> newGain =
            rule.mType.calGain(rule.mTimeMin + (delay ? 5 : 0), match);
        final float minScoreOdd = valueOfFloat(match.get("min" + rule.mTimeMin + "_scoreOdd"));
        final boolean isUp = rule.mType == RuleType.SCORE &&
            ((minScoreOdd >= 0 && rule.value() == 2) || (minScoreOdd <= 0 && rule.value() == 0));
        thresholds.stream().filter(t -> rule.profitRate() >= t).forEach(t -> {
          allCount.get(t).incrementAndGet();
          if (rule.mType == RuleType.SCORE) {
            scoreCount.get(t).incrementAndGet();
          }
          if (isUp) {
            upCount.get(t).incrementAndGet();
          }

          if (rule.mType == RuleType.BALL) {
            ballCount.get(t).incrementAndGet();
          }

          if (rule.mType == RuleType.BALL && rule.value() == 0) {
            bigCount.get(t).incrementAndGet();
          }

          if (rule.value() == 0 && newGain.first > 0 || rule.value() == 2 && newGain.second > 0) {
            float thisGain = rule.value() == 0 ? newGain.first : newGain.second;

            sumGain.put(t, sumGain.get(t) + thisGain);
            if (rule.mType == RuleType.SCORE) {
              sumScoreGain.put(t, sumScoreGain.get(t) + thisGain);
            }
            if (isUp) {
              sumUpGain.put(t, sumUpGain.get(t) + thisGain);
            }
            if (rule.mType == RuleType.BALL) {
              sumBallGain.put(t, sumBallGain.get(t) + thisGain);
            }
            if (rule.mType == RuleType.BALL && rule.value() == 0) {
              sumBigGain.put(t, sumBigGain.get(t) + thisGain);
            }
          }

          if (newGain.first == 0 && newGain.second == 0) {
            drewCount.get(t).incrementAndGet();
            if (rule.mType == RuleType.SCORE) {
              drewScoreCount.get(t).incrementAndGet();
            }
            if (isUp) {
              drewUpCount.get(t).incrementAndGet();
            }
            if (rule.mType == RuleType.BALL) {
              drewBallCount.get(t).incrementAndGet();
            }
            if (rule.mType == RuleType.BALL && rule.value() == 0) {
              drewBigCount.get(t).incrementAndGet();
            }
          } else if ((rule.value() == 0 && newGain.first > 0)
              || (rule.value() == 2 && newGain.second > 0)) {
            victoryCount.get(t).incrementAndGet();
            if (rule.mType == RuleType.SCORE) {
              victoryScoreCount.get(t).incrementAndGet();
            }
            if (isUp) {
              victoryUpCount.get(t).incrementAndGet();
            }
            if (rule.mType == RuleType.BALL) {
              victoryBallCount.get(t).incrementAndGet();
            }
            if (rule.mType == RuleType.BALL && rule.value() == 0) {
              victoryBigCount.get(t).incrementAndGet();
            }
          } else {
            defeatCount.get(t).incrementAndGet();
            if (rule.mType == RuleType.SCORE) {
              defeatScoreCount.get(t).incrementAndGet();
            }
            if (isUp) {
              defeatUpCount.get(t).incrementAndGet();
            }
            if (rule.mType == RuleType.BALL) {
              defeatBallCount.get(t).incrementAndGet();
            }
            if (rule.mType == RuleType.BALL && rule.value() == 0) {
              defeatBigCount.get(t).incrementAndGet();
            }
          }
        });
      });
    }

    thresholds.forEach(threshold -> {
      System.out.println("\n\n");
      //
      // float sumGainValue = sumGain.get(threshold);
      // int estimationCountValue = allCount.get(threshold).get();
      // int drewCountValue = drewCount.get(threshold).get();
      // int victoryCountValue = victoryCount.get(threshold).get();
      // int defeatCountValue = defeatCount.get(threshold).get();
      //
      // System.out.println(
      // String.format("Total, threshold=%.2f, " +
      // "sumGain=%.2f, total=%d," +
      // " drewCount=%d, victoryCount=%d, defeatCount=%d," +
      // " victoryRate=%.2f, profitRate=%.2f, profit=%.2f",
      // threshold, sumGainValue, estimationCountValue,
      // drewCountValue, victoryCountValue, defeatCountValue,
      // victoryCountValue * 1f / (victoryCountValue + defeatCountValue),
      // sumGainValue * 1f / (victoryCountValue + defeatCountValue),
      // sumGainValue - (victoryCountValue + defeatCountValue)));



      float sumScoreGainValue = sumScoreGain.get(threshold);
      int scoreCountValue = scoreCount.get(threshold).get();
      int drewScoreCountValue = drewScoreCount.get(threshold).get();
      int victoryScoreCountValue = victoryScoreCount.get(threshold).get();
      int defeatScoreCountValue = defeatScoreCount.get(threshold).get();

      System.out.println(
          String.format("Score, threshold=%.2f, " +
              "sumGain=%.2f, total=%d," +
              " drewCount=%d, victoryCount=%d, defeatCount=%d," +
              " victoryRate=%.2f, profitRate=%.2f, profit=%.2f",
              threshold, sumScoreGainValue, scoreCountValue,
              drewScoreCountValue, victoryScoreCountValue, defeatScoreCountValue,
              victoryScoreCountValue * 1f / (victoryScoreCountValue + defeatScoreCountValue),
              sumScoreGainValue * 1f / (victoryScoreCountValue + defeatScoreCountValue),
              sumScoreGainValue - (victoryScoreCountValue + defeatScoreCountValue)));


      //
      // float sumUpGainValue = sumUpGain.get(threshold);
      // int upCountValue = upCount.get(threshold).get();
      // int drewUpCountValue = drewUpCount.get(threshold).get();
      // int victoryUpCountValue = victoryUpCount.get(threshold).get();
      // int defeatUpCountValue = defeatUpCount.get(threshold).get();
      //
      // System.out.println(
      // String.format("Score-Up, threshold=%.2f, " +
      // "sumGain=%.2f, total=%d," +
      // " drewCount=%d, victoryCount=%d, defeatCount=%d," +
      // " victoryRate=%.2f, profitRate=%.2f, profit=%.2f",
      // threshold, sumUpGainValue, upCountValue,
      // drewUpCountValue, victoryUpCountValue, defeatUpCountValue,
      // victoryUpCountValue * 1f / (victoryUpCountValue + defeatUpCountValue),
      // sumUpGainValue * 1f / (victoryUpCountValue + defeatUpCountValue),
      // sumUpGainValue - (victoryUpCountValue + defeatUpCountValue)));
      //
      //
      //
      float sumBallGainValue = sumBallGain.get(threshold);
      int ballCountValue = ballCount.get(threshold).get();
      int drewBallCountValue = drewBallCount.get(threshold).get();
      int victoryBallCountValue = victoryBallCount.get(threshold).get();
      int defeatBallCountValue = defeatBallCount.get(threshold).get();

      System.out.println(
          String.format("Ball, threshold=%.2f, " +
              "sumGain=%.2f, total=%d," +
              " drewCount=%d, victoryCount=%d, defeatCount=%d," +
              " victoryRate=%.2f, profitRate=%.2f, profit=%.2f",
              threshold, sumBallGainValue, ballCountValue,
              drewBallCountValue, victoryBallCountValue, defeatBallCountValue,
              victoryBallCountValue * 1f / (victoryBallCountValue + defeatBallCountValue),
              sumBallGainValue * 1f / (victoryBallCountValue + defeatBallCountValue),
              sumBallGainValue - (victoryBallCountValue + defeatBallCountValue)));
      //
      //
      //
      // float sumBigGainValue = sumBigGain.get(threshold);
      // int bigCountValue = bigCount.get(threshold).get();
      // int drewBigCountValue = drewBigCount.get(threshold).get();
      // int victoryBigCountValue = victoryBigCount.get(threshold).get();
      // int defeatBigCountValue = defeatBigCount.get(threshold).get();
      //
      // System.out.println(
      // String.format("Big-Ball, threshold=%.2f, " +
      // "sumGain=%.2f, total=%d," +
      // " drewCount=%d, victoryCount=%d, defeatCount=%d," +
      // " victoryRate=%.2f, profitRate=%.2f, profit=%.2f",
      // threshold, sumBigGainValue, bigCountValue,
      // drewBigCountValue, victoryBigCountValue, defeatBigCountValue,
      // victoryBigCountValue * 1f / (victoryBigCountValue + defeatBigCountValue),
      // sumBigGainValue * 1f / (victoryBigCountValue + defeatBigCountValue),
      // sumBigGainValue - (victoryBigCountValue + defeatBigCountValue)));

    });
  }

}

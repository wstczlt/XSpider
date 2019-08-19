package com.test.manual;

import static com.test.Config.SPIDER_THREAD_COUNT;
import static com.test.db.QueryHelper.SQL_AND;
import static com.test.db.QueryHelper.SQL_SELECT;
import static com.test.db.QueryHelper.SQL_ST;
import static com.test.db.QueryHelper.buildSqlIn;
import static com.test.db.QueryHelper.doQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.test.dszuqiu.DsHistoryJobFactory;
import com.test.dszuqiu.DsJobBuilder;
import com.test.entity.Estimation;
import com.test.http.HttpEngine;
import com.test.pipeline.DbPipeline;
import com.test.tools.Pair;

public class HistoryTester {

  public static void doTest() throws Exception {
    // 运行爬虫
    final DbPipeline pipeline = new DbPipeline();
    // DsHistoryJobFactory factory = new DsHistoryJobFactory(new DsJobBuilder());
    DsHistoryJobFactory factory = new DsHistoryJobFactory(3, new DsJobBuilder());
    HttpEngine dragon = new HttpEngine(factory.build(), pipeline, SPIDER_THREAD_COUNT);
    dragon.start();

    String querySql = SQL_SELECT + SQL_AND + SQL_ST + buildSqlIn(factory.getMatchIDs());
    List<Map<String, Object>> matches = doQuery(querySql, 10_0000);

    System.out.println("比赛数量: " + matches.size());
    final RuleEval ruleEval = new RuleEval();
    final List<Integer> testMinArray = new ArrayList<>();
    for (int i = -1; i <= 80; i++) {
      testMinArray.add(i);
    }

    final List<Float> thresholds =
        Arrays.asList(1f, 1.02f, 1.03f, 1.04f, 1.05f, 1.06f, 1.07f, 1.08f);
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


    final Map<Float, Float> sumBallGain = new HashMap<>();
    final Map<Float, AtomicInteger> ballCount = new HashMap<>();
    final Map<Float, AtomicInteger> drewBallCount = new HashMap<>();
    final Map<Float, AtomicInteger> victoryBallCount = new HashMap<>();
    final Map<Float, AtomicInteger> defeatBallCount = new HashMap<>();

    thresholds.forEach(threshold -> {
      allCount.put(threshold, new AtomicInteger());
      drewCount.put(threshold, new AtomicInteger());
      victoryCount.put(threshold, new AtomicInteger());
      defeatCount.put(threshold, new AtomicInteger());

      scoreCount.put(threshold, new AtomicInteger());
      drewScoreCount.put(threshold, new AtomicInteger());
      victoryScoreCount.put(threshold, new AtomicInteger());
      defeatScoreCount.put(threshold, new AtomicInteger());

      ballCount.put(threshold, new AtomicInteger());
      drewBallCount.put(threshold, new AtomicInteger());
      victoryBallCount.put(threshold, new AtomicInteger());
      defeatBallCount.put(threshold, new AtomicInteger());
    });

    for (int i = 0; i < matches.size(); i++) {
      final Map<String, Object> match = matches.get(i);
      testMinArray.forEach(testMin -> {
        List<Estimation> estimations = ruleEval.eval(testMin, match);
        thresholds.forEach(threshold -> estimations.stream()
            .filter(estimation -> estimation.mProfitRate >= threshold)
            .forEach(estimation -> {
              final Rule rule = (Rule) estimation.mModel;
              final Pair<Float, Float> newGain = rule.mType.calGain(testMin, match);

             allCount.get(threshold).incrementAndGet();

              if (rule.value() == 0 && newGain.first > 0) {
                Float sumGainValue = sumGain.get(threshold);
                if (sumGainValue == null) {
                  sumGainValue = 0f;
                }
                sumGain.put(threshold, sumGainValue + newGain.first);
              }


              if (rule.value() == 2 && newGain.second > 0) {
                Float sumGainValue = sumGain.get(threshold);
                if (sumGainValue == null) {
                  sumGainValue = 0f;
                }
                sumGain.put(threshold, sumGainValue + newGain.second);
              }


              if (newGain.first == 0 && newGain.second == 0) {
                AtomicInteger drewCountValue = drewCount.get(threshold);
                if (drewCountValue == null) {
                  drewCountValue = new AtomicInteger();
                  drewCount.put(threshold, drewCountValue);
                }
                drewCountValue.incrementAndGet();

              } else if ((rule.value() == 0 && newGain.first > 0)
                  || (rule.value() == 2 && newGain.second > 0)) {
                AtomicInteger victoryCountValue = victoryCount.get(threshold);
                if (victoryCountValue == null) {
                  victoryCountValue = new AtomicInteger();
                  victoryCount.put(threshold, victoryCountValue);
                }
                victoryCountValue.incrementAndGet();
              } else {
                AtomicInteger defeatCountValue = defeatCount.get(threshold);
                if (defeatCountValue == null) {
                  defeatCountValue = new AtomicInteger();
                  defeatCount.put(threshold, defeatCountValue);
                }
                defeatCountValue.incrementAndGet();
              }



              if (rule.mType == RuleType.SCORE) {
                AtomicInteger scoreCountValue = scoreCount.get(threshold);
                if (scoreCountValue == null) {
                  scoreCountValue = new AtomicInteger();
                  scoreCount.put(threshold, scoreCountValue);
                }
                scoreCountValue.incrementAndGet();


                if (rule.value() == 0 && newGain.first > 0) {
                  Float sumGainValue = sumScoreGain.get(threshold);
                  if (sumGainValue == null) {
                    sumGainValue = 0f;
                  }
                  sumScoreGain.put(threshold, sumGainValue + newGain.first);
                }


                if (rule.value() == 2 && newGain.second > 0) {
                  Float sumGainValue = sumScoreGain.get(threshold);
                  if (sumGainValue == null) {
                    sumGainValue = 0f;
                  }
                  sumScoreGain.put(threshold, sumGainValue + newGain.second);
                }


                if (newGain.first == 0 && newGain.second == 0) {
                  AtomicInteger drewCountValue = drewScoreCount.get(threshold);
                  if (drewCountValue == null) {
                    drewCountValue = new AtomicInteger();
                    drewScoreCount.put(threshold, drewCountValue);
                  }
                  drewCountValue.incrementAndGet();

                } else if ((rule.value() == 0 && newGain.first > 0)
                    || (rule.value() == 2 && newGain.second > 0)) {
                  AtomicInteger victoryCountValue = victoryScoreCount.get(threshold);
                  if (victoryCountValue == null) {
                    victoryCountValue = new AtomicInteger();
                    victoryScoreCount.put(threshold, victoryCountValue);
                  }
                  victoryCountValue.incrementAndGet();
                } else {
                  AtomicInteger defeatCountValue = defeatScoreCount.get(threshold);
                  if (defeatCountValue == null) {
                    defeatCountValue = new AtomicInteger();
                    defeatScoreCount.put(threshold, defeatCountValue);
                  }
                  defeatCountValue.incrementAndGet();
                }

              }


              if (rule.mType == RuleType.BALL) {
                AtomicInteger ballCountValue = ballCount.get(threshold);
                if (ballCountValue == null) {
                  ballCountValue = new AtomicInteger();
                  ballCount.put(threshold, ballCountValue);
                }
                ballCountValue.incrementAndGet();



                if (rule.value() == 0 && newGain.first > 0) {
                  Float sumGainValue = sumBallGain.get(threshold);
                  if (sumGainValue == null) {
                    sumGainValue = 0f;
                  }
                  sumBallGain.put(threshold, sumGainValue + newGain.first);
                }


                if (rule.value() == 2 && newGain.second > 0) {
                  Float sumGainValue = sumBallGain.get(threshold);
                  if (sumGainValue == null) {
                    sumGainValue = 0f;
                  }
                  sumBallGain.put(threshold, sumGainValue + newGain.second);
                }


                if (newGain.first == 0 && newGain.second == 0) {
                  AtomicInteger drewCountValue = drewBallCount.get(threshold);
                  if (drewCountValue == null) {
                    drewCountValue = new AtomicInteger();
                    drewBallCount.put(threshold, drewCountValue);
                  }
                  drewCountValue.incrementAndGet();

                } else if ((rule.value() == 0 && newGain.first > 0)
                    || (rule.value() == 2 && newGain.second > 0)) {
                  AtomicInteger victoryCountValue = victoryBallCount.get(threshold);
                  if (victoryCountValue == null) {
                    victoryCountValue = new AtomicInteger();
                    victoryBallCount.put(threshold, victoryCountValue);
                  }
                  victoryCountValue.incrementAndGet();
                } else {
                  AtomicInteger defeatCountValue = defeatBallCount.get(threshold);
                  if (defeatCountValue == null) {
                    defeatCountValue = new AtomicInteger();
                    defeatBallCount.put(threshold, defeatCountValue);
                  }
                  defeatCountValue.incrementAndGet();
                }
              }
            }));
      });
    }

    thresholds.forEach(threshold -> {
      System.out.println("\n\n\n");

      if (!sumGain.containsKey(threshold)) {
        System.out.println(String.format("threshold=%.2f, 没有数据.", threshold));
      } else {
        float sumGainValue = sumGain.get(threshold);
        int estimationCountValue = allCount.get(threshold).get();
        int drewCountValue = drewCount.get(threshold).get();
        int victoryCountValue = victoryCount.get(threshold).get();
        int defeatCountValue = defeatCount.get(threshold).get();

        System.out.println(
            String.format("Total, threshold=%.2f, " +
                "sumGain=%.2f, estimationCount=%d," +
                " drewCount=%d, victoryCount=%d, defeatCount=%d," +
                " victoryRate=%.2f, profitRate=%.2f",
                threshold, sumGainValue, estimationCountValue,
                drewCountValue, victoryCountValue, defeatCountValue,
                victoryCountValue * 1f / (victoryCountValue + defeatCountValue),
                sumGainValue * 1f / (victoryCountValue + defeatCountValue)));
      }


      if (!sumScoreGain.containsKey(threshold)) {
        System.out.println(String.format("threshold=%.2f, 没有数据.", threshold));
      } else {
        float sumScoreGainValue = sumScoreGain.get(threshold);
        int scoreCountValue = scoreCount.get(threshold).get();
        int drewScoreCountValue = drewScoreCount.get(threshold).get();
        int victoryScoreCountValue = victoryScoreCount.get(threshold).get();
        int defeatScoreCountValue = defeatScoreCount.get(threshold).get();

        System.out.println(
            String.format("Score, threshold=%.2f, " +
                "sumGain=%.2f, estimationCount=%d," +
                " drewCount=%d, victoryCount=%d, defeatCount=%d," +
                " victoryRate=%.2f, profitRate=%.2f",
                threshold, sumScoreGainValue, scoreCountValue,
                drewScoreCountValue, victoryScoreCountValue, defeatScoreCountValue,
                victoryScoreCountValue * 1f / (victoryScoreCountValue + defeatScoreCountValue),
                sumScoreGainValue * 1f / (victoryScoreCountValue + defeatScoreCountValue)));
      }


      if (!sumBallGain.containsKey(threshold)) {
        System.out.println(String.format("threshold=%.2f, 没有数据.", threshold));
      } else {
        float sumBallGainValue = sumBallGain.get(threshold);
        int ballCountValue = ballCount.get(threshold).get();
        int drewBallCountValue = drewBallCount.get(threshold).get();
        int victoryBallCountValue = victoryBallCount.get(threshold).get();
        int defeatBallCountValue = defeatBallCount.get(threshold).get();

        System.out.println(
            String.format("Ball, threshold=%.2f, " +
                "sumGain=%.2f, estimationCount=%d," +
                " drewCount=%d, victoryCount=%d, defeatCount=%d," +
                " victoryRate=%.2f, profitRate=%.2f",
                threshold, sumBallGainValue, ballCountValue,
                drewBallCountValue, victoryBallCountValue, defeatBallCountValue,
                victoryBallCountValue * 1f / (victoryBallCountValue + defeatBallCountValue),
                sumBallGainValue * 1f / (victoryBallCountValue + defeatBallCountValue)));
      }

    });
  }

}

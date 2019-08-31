package com.test.manual;

import static com.test.Config.RADAR_THREAD_COUNT;
import static com.test.db.QueryHelper.SQL_AND;
import static com.test.db.QueryHelper.SQL_RT;
import static com.test.db.QueryHelper.SQL_SELECT;
import static com.test.db.QueryHelper.buildSqlIn;
import static com.test.db.QueryHelper.doQuery;
import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.test.Config;
import com.test.Keys;
import com.test.bot.BotConsumer;
import com.test.dszuqiu.DsJobBuilder;
import com.test.dszuqiu.DsJobFactory;
import com.test.entity.Estimation;
import com.test.http.HttpEngine;
import com.test.pipeline.DbPipeline;

public class HistoryRadar implements Keys {

  public static final Predicate<Estimation> DISPLAY_FILTER = estimation -> {
    final Rule rule = (Rule) estimation.mModel;
    final Map<String, Object> match = estimation.mMatch;
    final float minScoreOdd = valueOfFloat(match.get("min" + rule.mTimeMin + "_scoreOdd"));
    boolean isScoreUp = rule.mType == RuleType.SCORE &&
        ((minScoreOdd >= 0 && rule.value() == 2)
            || (minScoreOdd <= 0 && rule.value() == 0));

    boolean isScoreLow = rule.mType == RuleType.SCORE &&
        ((minScoreOdd >= 0 && rule.value() == 0)
            || (minScoreOdd <= 0 && rule.value() == 2));

    boolean isBallBig = rule.mType == RuleType.BALL && rule.value() == 0;
    boolean isBallSmall = rule.mType == RuleType.BALL && rule.value() == 2;

    return (Config.SHOW_SCORE_UP && isScoreUp)
        || (Config.SHOW_SCORE_LOW && isScoreLow)
        || (Config.SHOW_BALL_BIG && isBallBig)
        || (Config.SHOW_BALL_SMALL && isBallSmall);
  };

  public static final Predicate<Estimation> THRESHOLD_FILTER = estimation -> {
    final Rule rule = (Rule) estimation.mModel;
    final Map<String, Object> match = estimation.mMatch;
    final float minScoreOdd = valueOfFloat(match.get("min" + rule.mTimeMin + "_scoreOdd"));
    boolean isScoreUp = rule.mType == RuleType.SCORE &&
        ((minScoreOdd >= 0 && rule.value() == 2)
            || (minScoreOdd <= 0 && rule.value() == 0));

    boolean isScoreLow = rule.mType == RuleType.SCORE &&
        ((minScoreOdd >= 0 && rule.value() == 0)
            || (minScoreOdd <= 0 && rule.value() == 2));

    boolean isBallBig = rule.mType == RuleType.BALL && rule.value() == 0;
    boolean isBallSmall = rule.mType == RuleType.BALL && rule.value() == 2;

    return (rule.profitRate() >= Config.SCORE_UP_PROFIT_THRESHOLD && isScoreUp)
        || (rule.profitRate() >= Config.SCORE_LOW_PROFIT_THRESHOLD && isScoreLow)
        || (rule.profitRate() >= Config.BALL_UP_PROFIT_THRESHOLD && isBallBig)
        || (rule.profitRate() >= Config.BALL_LOW_PROFIT_THRESHOLD && isBallSmall);
  };

  private static final List<Consumer<Estimation>> CONSUMERS =
      Arrays.asList(new HistoryConsumer(), new BotConsumer());

  public static void main(String[] args) throws Exception {
    new HistoryRadar().run(1);
  }

  public void run(int loop) throws Exception {
    long sleep = 0;
    while (loop-- > 0) {
      try {
        long start = System.currentTimeMillis();
        loopMain();
        sleep = Config.RADAR_MIN_ONE_LOOP - (System.currentTimeMillis() - start);
      } catch (Throwable e) {
        e.printStackTrace();
      } finally {
        if (sleep > 0) Thread.sleep(sleep);
      }
    }
  }

  private void loopMain() throws Exception {
    System.out.println(
        "\n\n\n\n\n\n当前时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

    // 运行爬虫
    final DbPipeline pipeline = new DbPipeline();
    DsJobFactory factory = new DsJobFactory(new DsJobBuilder());
    HttpEngine dragon = new HttpEngine(factory.build(), pipeline, RADAR_THREAD_COUNT);
    dragon.start();

    final List<Integer> matchIDs = factory.getMatchIDs();
    Config.LOGGER.log("Find MatchIDs: " + matchIDs);
    // 运行AI
    String querySql = SQL_SELECT + SQL_AND + SQL_RT + buildSqlIn(matchIDs);
    // 回查语句
    // String querySql = SQL_SELECT + SQL_AND + SQL_ST + buildSqlIn(matchIDs);
    List<Map<String, Object>> matches = doQuery(querySql, 1000);
    System.out.println("比赛总场次: " + matches.size());

    final NewRulEval newRulEval = new NewRulEval();
    final RuleEval ruleEval = new RuleEval();

    matches.forEach(match -> {
      List<Estimation> list = newRulEval.evalEst(valueOfInt(match.get(TIME_MIN)), match);
      list.addAll(ruleEval.evalEst(valueOfInt(match.get(TIME_MIN)), match));

      list.stream()
          .filter(DISPLAY_FILTER)
          .filter(THRESHOLD_FILTER)
          .sorted((o1, o2) -> (int) (o2.mProfitRate * 1000 - o1.mProfitRate * 1000))
          .forEach(est -> CONSUMERS.forEach(consumer -> consumer.accept(est)));
    });
  }
}

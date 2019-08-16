package com.test.manual;

import static com.test.Config.RADAR_THREAD_COUNT;
import static com.test.db.QueryHelper.SQL_AND;
import static com.test.db.QueryHelper.SQL_RT;
import static com.test.db.QueryHelper.SQL_SELECT;
import static com.test.db.QueryHelper.SQL_ST;
import static com.test.db.QueryHelper.buildSqlIn;
import static com.test.db.QueryHelper.doQuery;
import static com.test.tools.Utils.valueOfInt;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.test.Config;
import com.test.Keys;
import com.test.dszuqiu.DsHistoryJobFactory;
import com.test.dszuqiu.DsJobBuilder;
import com.test.entity.Estimation;
import com.test.http.HttpEngine;
import com.test.pipeline.DbPipeline;

public class HistoryRadar implements Keys {

  private static final RuleEval RULE_EVAL = new RuleEval();
  private static final List<Consumer<Estimation>> CONSUMERS = Arrays.asList(new HistoryConsumer());

  public static void main(String[] args) throws Exception {
    new HistoryRadar().run(1);
  }

  public void run(int loop) throws Exception {
    long sleep = 0;
    while (loop-- > 0) {
      try {
        long start = System.currentTimeMillis();
        loopMain();
        sleep = Config.MIN_ONE_LOOP - (System.currentTimeMillis() - start);
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
    DsHistoryJobFactory factory = new DsHistoryJobFactory(new DsJobBuilder());
    HttpEngine dragon = new HttpEngine(factory.build(), pipeline, RADAR_THREAD_COUNT);
    dragon.start();

    final List<Integer> matchIDs = factory.getMatchIDs();
    Config.LOGGER.log("Find MatchIDs: " + matchIDs);
    // 运行AI
     String querySql = SQL_SELECT + SQL_AND + SQL_RT + buildSqlIn(matchIDs);
    // 回查语句
//    String querySql = SQL_SELECT + SQL_AND + SQL_ST + buildSqlIn(matchIDs);
    List<Map<String, Object>> matches = doQuery(querySql, 1000);
    System.out.println("比赛总场次: " + matches.size());

    matches.forEach(match -> RULE_EVAL.eval(valueOfInt(match.get(TIME_MIN)), match)
        .forEach(est -> CONSUMERS.forEach(consumer -> consumer.accept(est))));
  }
}

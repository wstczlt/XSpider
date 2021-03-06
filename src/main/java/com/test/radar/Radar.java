package com.test.radar;

import static com.test.Config.RADAR_THREAD_COUNT;
import static com.test.db.QueryHelper.SQL_RT;
import static com.test.db.QueryHelper.buildSqlIn;
import static com.test.db.QueryHelper.doQuery;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;

import com.test.Config;
import com.test.Keys;
import com.test.dszuqiu.DsJobBuilder;
import com.test.dszuqiu.DsJobFactory;
import com.test.entity.Estimation;
import com.test.entity.Model;
import com.test.http.HttpEngine;
import com.test.learning.Phoenix;
import com.test.learning.model.Odd45;
import com.test.pipeline.DbPipeline;

public class Radar implements Keys {

  private final List<Model> mModels;
  private final List<Consumer<Estimation>> mConsumers;

  public Radar(List<Model> models, List<Consumer<Estimation>> consumers) {
    mModels = models;
    mConsumers = consumers;
  }

  public static void main(String[] args) throws Exception {
    final List<Model> models = Collections.singletonList(new Odd45());
    final List<Consumer<Estimation>> consumers = Collections.singletonList(new ConsoleConsumer());

    new Radar(models, consumers).run(1);
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
    final SimpleDateFormat sft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    sft.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    System.out.println("\n\n\n\n\n\n当前时间: " + sft.format(new Date()));

    // 运行爬虫
    final DbPipeline pipeline = new DbPipeline();
    DsJobFactory factory = new DsJobFactory(new DsJobBuilder(), false, null);
    HttpEngine dragon = new HttpEngine(factory.build(), pipeline, RADAR_THREAD_COUNT);
    dragon.start();

    final List<Integer> matchIDs = factory.getMatchIDs();
    Config.LOGGER.log("Find MatchIDs: " + matchIDs);
    // 运行AI
    for (Model model : mModels) {
      String andSql = SQL_RT + buildSqlIn(matchIDs);
      // String andSql = SQL_ST + buildSqlIn(matchIDs);
      List<Map<String, Object>> matches = doQuery(model.querySql(andSql), 1000);
      System.out.println("-----------------模型: " + model.name() + "--------------------");
      System.out.println("比赛总场次: " + matches.size());
      loopOne(model, matches);
    }
  }

  private void loopOne(Model model, List<Map<String, Object>> matches) throws Exception {
    if (matches.isEmpty()) {
      return;
    }
    if (matches.size() == 1) { // 单条无法训练, 做一个trick
      matches.add(matches.get(0));
    }

    Phoenix.runEst(model, matches).stream()
        .sorted((o1, o2) -> (int) (o2.mProbability * 1000 - o1.mProbability))
        .forEach(est -> mConsumers.forEach(consumer -> consumer.accept(est)));
  }
}

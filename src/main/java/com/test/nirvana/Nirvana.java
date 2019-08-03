package com.test.nirvana;

import static com.test.tools.QueryHelper.SQL_BASE;
import static com.test.tools.QueryHelper.SQL_ORDER;
import static com.test.tools.QueryHelper.SQL_RT;
import static com.test.tools.QueryHelper.buildSqlIn;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.test.Config;
import com.test.dragon.Dragon;
import com.test.entity.Estimation;
import com.test.entity.Match;
import com.test.entity.Model;
import com.test.phoenix.Phoenix;
import com.test.phoenix.PhoenixInputs;
import com.test.phoenix.model.BallHalfModel;
import com.test.phoenix.model.OddHalfModel;
import com.test.tools.QueryHelper;

public class Nirvana {

  private static final Model[] MODELS = new Model[] {
      new BallHalfModel(), new OddHalfModel()
  };

  private static final EstimationConsumer[] CONSUMERS = new EstimationConsumer[] {
      new ConsoleConsumer(), new FileConsumer()
  };

  public static void main(String[] args) throws Exception {
    runRt(1000);
  }

  public static void runRt(int loop) throws Exception {
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

  private static void loopMain() throws Exception {
    System.out.println(
        "\n\n\n\n\n\n当前时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

    // 运行爬虫
    List<Integer> matchIDs = Dragon.runRt();
    // 查询数据
    String querySql = SQL_BASE + SQL_RT + buildSqlIn(matchIDs) + SQL_ORDER;
    List<Match> matches = QueryHelper.doQuery(querySql);
    System.out.println("进行中的比赛场次: " + matches.size());

    // 运行AI
    for (Model model : MODELS) {
      System.out.println("-----------------模型: " + model.name() + "--------------------");
      loopOne(model, matches);
    }
  }

  private static void loopOne(Model model, List<Match> matches) throws Exception {
    matches = matches.stream().filter(model)
        .sorted((o1, o2) -> o1.mLeague.compareTo(o2.mLeague)).collect(Collectors.toList());
    if (matches.isEmpty()) {
      return;
    }
    boolean trick = false;
    if (matches.size() == 1) { // 单条无法训练, 做一个trick
      matches.add(matches.get(0));
      trick = true;
    }

    PhoenixInputs input = new PhoenixInputs(model, matches, false);
    input.prepare(); // 写入数据
    List<Estimation> results = Phoenix.runEstimate(model, input);

    for (int i = 0; i < results.size(); i++) {
      if (trick && i == 1) { // trick的数据不要
        continue;
      }
      for (EstimationConsumer consumer : CONSUMERS) {
        consumer.accept(input.mMatches.get(i), model, results.get(i));
      }
    }
  }
}

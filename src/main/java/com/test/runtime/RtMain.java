package com.test.runtime;

import static com.test.train.tools.MatchQuery.SQL_BASE;
import static com.test.train.tools.MatchQuery.SQL_ORDER;
import static com.test.train.tools.MatchQuery.SQL_RT;
import static com.test.train.tools.MatchQuery.buildSqlIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.test.dragon.DragonMain;
import com.test.dragon.kernel.ListSupplier;
import com.test.runtime.est.ConsoleEstConsumer;
import com.test.runtime.est.EstimationConsumer;
import com.test.runtime.est.FileEstConsumer;
import com.test.runtime.rt.Rt;
import com.test.runtime.rt.RtBallHalf;
import com.test.runtime.rt.RtOddHalf;
import com.test.spider.SpiderConfig;
import com.test.tools.Utils;
import com.test.train.tools.Estimation;
import com.test.train.tools.Match;
import com.test.train.tools.MatchQuery;
import com.test.train.tools.TrainInputs;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RtMain {

  private static final Rt[] RTS = new Rt[] {
      new RtBallHalf(), new RtOddHalf()
  };

  private static final EstimationConsumer[] CONSUMERS = new EstimationConsumer[] {
      new ConsoleEstConsumer(), new FileEstConsumer()
  };

  public static void main(String[] args) throws Exception {
    final long minOneLoop = 60 * 1000; // 一圈不低于1分钟
    long deltaOneLoop = 0;
    while (true) {
      try {
        long start = System.currentTimeMillis();
        loopMain();
        deltaOneLoop = minOneLoop - (System.currentTimeMillis() - start);
      } catch (Throwable e) {
        e.printStackTrace();
      } finally {
        if (deltaOneLoop > 0) {
          Thread.sleep(deltaOneLoop);
        }
      }
    }
  }

  private static void loopMain() throws Exception {
    System.out.println("\n\n\n\n\n\n当前时间: " + SpiderConfig.DATE_FORMAT.format(new Date()));

    final List<Integer> matchIDs = collectRealTimeMatchIds();
    // 运行爬虫
    DragonMain.run(new ListSupplier(matchIDs));
    String querySql = SQL_BASE + SQL_RT + buildSqlIn(matchIDs) + SQL_ORDER;
    List<Match> matches = MatchQuery.doQuery(querySql);
    System.out.println("进行中的比赛场次: " + matches.size());

    // 运行AI
    loopRts(matches);
  }

  private static void loopRts(List<Match> matches) throws Exception {
    for (Rt rt : RTS) {
      System.out.println("-----------------模型: " + rt.model().name() + "--------------------");
      loopOne(rt, matches);
    }
  }

  private static void loopOne(Rt rt, List<Match> matches) throws Exception {
    matches = matches.stream().filter(rt)
        .sorted((o1, o2) -> o1.mLeague.compareTo(o2.mLeague)).collect(Collectors.toList());
    if (matches.isEmpty()) {
      return;
    }
    boolean trick = false;
    if (matches.size() == 1) { // 单条无法训练, 做一个trick
      matches.add(matches.get(0));
      trick = true;
    }

    TrainInputs input = new TrainInputs(rt.model(), matches, false);
    input.prepare(); // 写入数据
    List<Estimation> results = rt.model().estimate(input);

    for (int i = 0; i < results.size(); i++) {
      if (trick && i == 1) { // trick的数据不要
        continue;
      }
      for (EstimationConsumer consumer : CONSUMERS) {
        consumer.onEstimation(input.mMatches.get(i), rt.model(), results.get(i));
      }
    }
  }


  private static List<Integer> collectRealTimeMatchIds() throws Exception {
    final String requestUrl = "http://score.nowscore.com/data/sbOddsData.js";
    List<Integer> matchIds = new ArrayList<>();
    OkHttpClient httpClient = DragonMain.buildHttpClient();
    Response response = httpClient.newCall(new Request.Builder().url(requestUrl).build()).execute();
    if (!response.isSuccessful() || response.body() == null) {
      return Collections.emptyList();
    }
    String html = response.body().string();
    // System.out.println(html);
    Pattern pattern = Pattern.compile("sData\\[\\d+]");
    Matcher matcher = pattern.matcher(html);
    while (matcher.find()) {
      String matchString = matcher.group();
      matchString = matchString.replace("sData[", "").replace("]", "");
      matchIds.add(Utils.valueOfInt(matchString));
    }
    return matchIds;
  }

}

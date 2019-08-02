package com.test.runtime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.test.dragon.DragonMain;
import com.test.dragon.kernel.ListSupplier;
import com.test.spider.SpiderConfig;
import com.test.tools.Utils;
import com.test.train.tools.Estimation;
import com.test.train.tools.Match;
import com.test.train.tools.MatchQuery;
import com.test.train.tools.TrainInputs;

public class RtMain {

  private static final Rt[] RTS = new Rt[] {
      new RtBallHalf(), new RtOddHalf()
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
    System.out.println(); // 空行
    System.out.println("当前时间: " + SpiderConfig.DATE_FORMAT.format(new Date()));

    final List<Integer> matchIDs = collectRealTimeMatchIds();
    // 运行爬虫
    DragonMain.run(new ListSupplier(matchIDs));
    // 运行AI
    loopRts(matchIDs);
  }

  private static void loopRts(List<Integer> matchIDs) throws Exception {
    for (Rt rt : RTS) {
      loopOne(rt, matchIDs);
    }
  }

  private static void loopOne(Rt rt, List<Integer> matchIDs) throws Exception {
    final String querySql = rt.buildSql(matchIDs);
    List<Match> matches = MatchQuery.doQuery(querySql).stream().filter(rt)
        .sorted((o1, o2) -> o1.mLeague.compareTo(o2.mLeague)).collect(Collectors.toList());

    TrainInputs input = new TrainInputs(rt.model(), matches, false);
    input.prepare(); // 写入数据
    List<Estimation> results = rt.model().estimate(input);

    for (int i = 0; i < results.size(); i++) {
      rt.display(input.mMatches.get(i), results.get(i));
    }

    System.out.println("\n\n----------");
  }


  private static List<Integer> collectRealTimeMatchIds() throws Exception {
    final String requestUrl = "http://score.nowscore.com/data/sbOddsData.js";
    List<Integer> matchIds = new ArrayList<>();
    HttpResponse response = HttpClients.custom().build().execute(new HttpGet(requestUrl));
    HttpEntity entity = response.getEntity();
    String html = EntityUtils.toString(entity, "UTF-8");
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

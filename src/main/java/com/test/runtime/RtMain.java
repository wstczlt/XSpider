package com.test.runtime;

import static com.test.utils.Logger.EMPTY;

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

import com.test.spider.SpiderConfig;
import com.test.spider.tools.SpiderBuilder;
import com.test.train.tools.DataSet;
import com.test.train.tools.Estimation;
import com.test.train.tools.Match;
import com.test.train.tools.QueryHelper;
import com.test.utils.Utils;

import us.codecraft.webmagic.Spider;

public class RtMain {

  private static final Rt[] RTS = new Rt[] {
      new RtBallAt70()
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
    final Spider spider = new SpiderBuilder(matchIDs, EMPTY, mojieMatch -> true).build();

    // 运行爬虫
    spider.run();
    spider.close();
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
    List<Match> matches = QueryHelper.doQuery(querySql).stream().filter(rt)
        .sorted((o1, o2) -> o1.mLeague.compareTo(o2.mLeague)).collect(Collectors.toList());

    DataSet testData = new DataSet(rt.model(), matches, false);
    testData.prepare(); // 写入数据
    List<Estimation> results = rt.model().estimate(testData);

    for (int i = 0; i < results.size(); i++) {
      rt.display(testData.mMatches.get(i), results.get(i));
    }
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
    // System.out.println(matchIds);
    return matchIds;
  }

}

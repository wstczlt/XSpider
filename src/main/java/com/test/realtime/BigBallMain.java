package com.test.realtime;

import static com.test.spider.tools.Logger.SYSTEM;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;

import com.test.spider.FootballSpider;
import com.test.spider.SpiderConfig;
import com.test.spider.SpiderUtils;
import com.test.spider.tools.Pair;
import com.test.train.TrainModel;
import com.test.train.match.Match;
import com.test.train.match.MatchQueryHelper;
import com.test.train.model.BigBallOfMin70;
import com.test.train.utils.TrainUtils;

public class BigBallMain {

  private static final TrainModel TRAIN_MODEL = new BigBallOfMin70();

  public static void main(String[] args) throws Exception {
    // while (true) {
    loopMain();
    // Thread.sleep(30 * 1000L); // 30s一次
    // }
  }

  private static void loopMain() throws Exception {
    final List<Integer> matchIDs = collectRealTimeMatchIds();
    final String querySql = buildSql(matchIDs);
    new FootballSpider(matchIDs, SYSTEM).run();
    List<Match> matches = MatchQueryHelper.doQuery(querySql).stream().filter(match -> {
      return (new Date().getTime() - match.mMatchTime) <= 2 * 3600 * 1000; // 两小时内的比赛
    }).collect(Collectors.toList());
    List<Map<String, Float>> testSet = TrainUtils.trainMaps(matches);

    System.out.println(); // 空行
    System.out.println("Loop: " + SpiderConfig.DATE_FORMAT.format(new Date()));
    if (matches.isEmpty()) {
      System.out.println("       No match hit.");
      return;
    } else {
      System.out.println("       Match Count: " + matches.size());
    }

    doTest(matches, testSet);
  }

  private static void doTest(List<Match> matches, List<Map<String, Float>> testSet)
      throws Exception {
    TrainModel model = TRAIN_MODEL;
    List<Pair<Double, Double>> results = model.predict(testSet);
    // 校验结果集长度
    if (matches.size() != testSet.size() || matches.size() != results.size()) {
      throw new RuntimeException("result not match.");
    }
    for (int i = 0; i < results.size(); i++) {
      int value = results.get(i).first.intValue();
      float prob = results.get(i).second.floatValue();
      if (prob < model.bestThreshold()) {
        continue;
      }
      display(matches.get(i), value, prob);
    }
  }

  private static void display(Match match, int value, float prob) {
    int matchID = match.mMatchID;
    String hostName = match.mHostName;
    String customName = match.mCustomName;
    String league = !TextUtils.isEmpty(match.mLeague) ? match.mLeague : "未知";

    System.out.println(String.format("---> Model: %s", "大小球"));
    System.out.println(String.format("     MatchID: %d, 分钟: %s", matchID, match.mTimeMin));
    System.out.println(String.format("     联赛: %s, %s VS %s", league, hostName, customName));
    System.out.println(String.format("     盘口: %s%.2f, 概率: %.2f", value == 1 ? "大球" : "小球",
        match.mBigOddOfMinOfMin70, prob));
  }

  private static List<Integer> collectRealTimeMatchIds() throws Exception {
    final String requestUrl = "http://score.nowscore.com/data/sbOddsData.js";
    List<Integer> matchIds = new ArrayList<>();
    HttpResponse response = HttpClients.custom().build().execute(new HttpGet(requestUrl));
    HttpEntity entity = response.getEntity();
    String html = EntityUtils.toString(entity, "UTF-8");
    // System.out.println(html);
    Pattern pattern = Pattern.compile("sData\\[\\d+\\]");
    Matcher matcher = pattern.matcher(html);
    while (matcher.find()) {
      String matchString = matcher.group();
      matchString = matchString.replace("sData[", "").replace("]", "");
      matchIds.add(SpiderUtils.valueOfInt(matchString));
    }
    // System.out.println(matchIds);
    return matchIds;
  }


  private static String buildSql(List<Integer> matchIDs) {
    String timeMinSql = " AND timeMin is not null AND timeMin >= 65 AND timeMin <= 80 ";
    return MatchQueryHelper.SQL_QUERY_BASE + timeMinSql + MatchQueryHelper.buildSqlIn(matchIDs)
        + MatchQueryHelper.SQL_ORDER;
  }


}

package com.test.realtime;

import static com.test.spider.tools.Logger.EMPTY;
import static com.test.train.match.QueryHelper.SQL_BASE;
import static com.test.train.match.QueryHelper.SQL_MIDDLE;
import static com.test.train.match.QueryHelper.SQL_MIN_70;

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
import com.test.train.match.Match;
import com.test.train.match.QueryHelper;
import com.test.train.model.BallAt70;
import com.test.train.utils.TrainModel;
import com.test.train.utils.TrainUtils;

public class RtBallOfMin70 {

  private static final TrainModel TRAIN_MODEL = new BallAt70();

  public static void main(String[] args) throws Exception {
    final long minOneLoop = 60 * 1000; // 一圈不低于minOneLoop
    long deltaOneLoop = 0;
    while (true) {
      try {
        long start = System.currentTimeMillis();
        loopMain();
        deltaOneLoop = minOneLoop - (System.currentTimeMillis() - start);
      } catch (Throwable ignore) {} finally {
        if (deltaOneLoop > 0) {
          Thread.sleep(deltaOneLoop);
        }
      }
    }
  }

  private static void loopMain() throws Exception {
    System.out.println(); // 空行
    System.out.println("运行时间: " + SpiderConfig.DATE_FORMAT.format(new Date()));

    final List<Integer> matchIDs = collectRealTimeMatchIds();
    final String querySql = buildSql(matchIDs);
    new FootballSpider(matchIDs, EMPTY, page -> {
      long matchTime = page.getResultItems().get("matchTime");
      return System.currentTimeMillis() > matchTime + 3600 * 1000; // 最少开始1小时的比赛
    }).run();

    List<Match> matches = QueryHelper.doQuery(querySql).stream().filter(match -> {
      if (match.mTimeMin < 68 || match.mTimeMin > 85) {
        return false;
      }
      long timeDis = System.currentTimeMillis() - match.mMatchTime; // 目前数据有误
      return timeDis > 3600 * 1000 && timeDis <= 2 * 3600 * 1000; // 大于1小时，小于2小时内的比赛
    }).sorted((o1, o2) -> o1.mLeague.compareTo(o2.mLeague)).collect(Collectors.toList());
    if (matches.size() == 1) { // 单行数据无法运算
      matches.add(matches.get(0));
    }

    System.out.println("       可选比赛场次: " + matches.size());
    System.out.println(); // 空行

    doTest(matches, TrainUtils.trainMaps(matches));
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
    if (value != 1) { // 只展示大球
      return;
    }
    // int matchID = match.mMatchID;
    String hostName = match.mHostName;
    String customName = match.mCustomName;
    String league = !TextUtils.isEmpty(match.mLeague) ? match.mLeague : "野鸡";

    System.out.println();
    System.out.println(
        String.format("%d', [%s], %s VS %s", match.mTimeMin, league, hostName, customName));
    System.out.println(String.format("     比分: %d : %d", match.mHostScore, match.mCustomScore));
    System.out.println(String.format("     盘口: %s， 概率: %.2f[历史命中率: %s]",
        ((value == 1 ? "大" : "小") + match.mBigOddOfMin70),
        prob, ((int) (prob * 100 + 10)) + "%"));
    System.out.println();
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
    String timeMinSql = " AND timeMin is not null AND timeMin >= 70 AND timeMin <= 85 ";
    return SQL_BASE + SQL_MIDDLE + SQL_MIN_70
        + QueryHelper.buildSqlIn(matchIDs)
        + QueryHelper.SQL_ORDER;
  }
}

package com.test.spider.consumer;

import static com.test.spider.SpiderConfig.DATE_FORMAT;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import com.github.promeg.pinyinhelper.Pinyin;
import com.test.spider.model.UrlType;
import com.test.spider.tools.SpiderUtils;
import com.test.tools.Logger;
import com.test.tools.Pair;
import com.test.tools.Utils;

import us.codecraft.webmagic.Page;

// => http://score.nowscore.com/detail/1662654cn.html
// 只爬取已结束的比赛
public class DetailConsumer implements Consumer {

  private final Logger mLogger;
  private final Predicate<Page> mPredicate;

  public DetailConsumer(Logger logger, Predicate<Page> predicate) {
    mLogger = logger;
    mPredicate = predicate;
  }

  @Override
  public void accept(Page page) {
    UrlType urlType = UrlType.formUrl(page.getUrl().toString());
    if (urlType != UrlType.DETAIL) {
      return;
    }
    final int matchID = SpiderUtils.extractMatchID(page);
    if (matchID <= 0) {
      return;
    }
    try { // hostName, 主队名称; customName, 客队名称
      String hostName =
          page.getHtml().xpath("//div[@id=home]/a/span[@class=name]/text()").toString();
      String customName =
          page.getHtml().xpath("//div[@id=guest]/a/span[@class=name]/text()").toString();
      page.putField("hostName", hostName.replace("(中)", "")); // 去掉中立场
      page.putField("hostNamePinyin", Pinyin.toPinyin(hostName, ""));
      page.putField("customName", customName);
      page.putField("customNamePinyin", Pinyin.toPinyin(customName, ""));
      // mLogger.log("matchID = " + matchID + " => (" + hostName + " VS " + customName +
      // ")");
    } catch (Throwable e) { // 缺少基本信息
      SpiderUtils.log(e);
      page.setSkip(true);
      return;
    }

    try { // matchTime, 比赛时间，=> 开赛时间：2019-07-11 17:00
      int matchTimeStart = page.getRawText().indexOf("开赛时间：");
      int matchTimeEnd = page.getRawText().indexOf("</span>", matchTimeStart);
      String matchTimeString = page.getRawText().substring(matchTimeStart, matchTimeEnd);
      matchTimeString = matchTimeString.substring("开赛时间：".length());
      Date date = DATE_FORMAT.parse(matchTimeString);
      // if (date.getTime() > System.currentTimeMillis()) {
      // mLogger.log("matchID = " + matchID + ", 时间超出范围 => " + matchTimeString);
      // page.setSkip(true);
      // return;
      // }
      // // 要求在时间范围内
      // if (date.getTime() < MIN_DATE.getTime()
      // || date.getTime() > MAX_DATE.getTime()) {
      // mLogger.log("matchID = " + matchID + ", 时间超出范围 => " + matchTimeString);
      // page.setSkip(true);
      // return;
      // }
      page.putField("matchTime", date.getTime());
    } catch (Throwable e) { // 缺少基本信息
      SpiderUtils.log(e);
      page.setSkip(true);
      return;
    }


    try { // weather, 天气(原文未加工) => 天气：多云 温度：24℃～25℃
      int weatherStart = page.getRawText().indexOf("天气：");
      int weatherEnd = page.getRawText().indexOf("<", weatherStart);
      if (weatherStart >= 0 && weatherEnd > weatherStart) {
        String weather = page.getRawText().substring(weatherStart, weatherEnd);
        weather = weather.replace("\r", "");
        weather = weather.replace("\n", "");
        page.putField("weather", weather);
      }
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    try { // hostScore, customScore, 主客队比分
      List<String> scores = page.getHtml().xpath("//span[@class='b t15']/text()").all();
      page.putField("hostScore", scores.get(0));
      page.putField("customScore", scores.get(1));
    } catch (Throwable e) {
      SpiderUtils.log(e);
      page.setSkip(true);
      return;
    }

    // ------ 技术统计 ------
    try { // hostCornerScore, customCornerScore, 主客队角球数
      Pair<String, String> cornerScorePair =
          SpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "角球");
      page.putField("hostCornerScore", Utils.valueOfInt(cornerScorePair.first));
      page.putField("customCornerScore", Utils.valueOfInt(cornerScorePair.second));
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    try { // hostYellowCard, customYellowCard, 主客队黄卡数
      Pair<String, String> yellowCardPair =
          SpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "黄牌");
      page.putField("hostYellowCard", Utils.valueOfInt(yellowCardPair.first));
      page.putField("customYellowCard", Utils.valueOfInt(yellowCardPair.second));
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    try { // hostRedCard, customRedCard, 主客队红卡数
      Pair<String, String> redCardPair = SpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "红牌");
      page.putField("hostRedCard", Utils.valueOfInt(redCardPair.first));
      page.putField("customRedCard", Utils.valueOfInt(redCardPair.second));
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    try { // hostShoot, customShoot, 主客队射门数
      Pair<String, String> shootPair = SpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "射门");
      page.putField("hostShoot", Utils.valueOfInt(shootPair.first));
      page.putField("customShoot", Utils.valueOfInt(shootPair.second));
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    try { // hostBestShoot, customBestShoot, 主客队射正数
      Pair<String, String> bestShootPair =
          SpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "射正");
      page.putField("hostBestShoot", Utils.valueOfInt(bestShootPair.first));
      page.putField("customBestShoot", Utils.valueOfInt(bestShootPair.second));
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    try { // hostAttack, customAttach, 主客队进攻数
      Pair<String, String> attackPair = SpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "进攻");
      page.putField("hostAttack", Utils.valueOfInt(attackPair.first));
      page.putField("customAttach", Utils.valueOfInt(attackPair.second));
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    try { // hostBestAttack, customBestAttack, 主客队危险进攻数
      Pair<String, String> bestAttackPair =
          SpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "危险进攻");
      page.putField("hostBestAttack", Utils.valueOfInt(bestAttackPair.first));
      page.putField("customBestAttack", Utils.valueOfInt(bestAttackPair.second));
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    try { // hostControlRate, customControlRate, 主客队危险进攻数
      Pair<String, String> controlRatePair =
          SpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "控球率");
      page.putField("hostControlRate",
          Utils.valueOfFloat(controlRatePair.first.replace("%", "")));
      page.putField("customControlRate",
          Utils.valueOfFloat(controlRatePair.second.replace("%", "")));
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }


    // ------ 技统数据 -----

    // hostScoreOf3, 主队近3场平均进球数
    // customScoreOf3, 客队近3场平均进球数
    // hostScoreOf10, 主队近10场平均进球数
    // customScoreOf10, 客队近10场平均进球数
    try {
      Pair<String, String> scorePair = SpiderUtils.selectTableOfTd(page.getHtml(), "技统数据", "进球");
      String[] hostScoreArray = scorePair.first.split("/");
      if (hostScoreArray.length >= 2) {
        page.putField("hostScoreOf3", Utils.valueOfFloat(hostScoreArray[0]));
        page.putField("hostScoreOf10", Utils.valueOfFloat(hostScoreArray[1]));
      }

      String[] customScoreArray = scorePair.second.split("/");
      if (customScoreArray.length >= 2) {
        page.putField("customScoreOf3", Utils.valueOfFloat(customScoreArray[0]));
        page.putField("customScoreOf10", Utils.valueOfFloat(customScoreArray[1]));
      }
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    // hostLossOf3, 主队近3场平均丢球数
    // customLossOf3, 客队近3场平均丢球数
    // hostLossOf10, 主队近10场平均丢球数
    // customLossOf10, 客队近10场平均丢球数
    try {
      Pair<String, String> lossPair = SpiderUtils.selectTableOfTd(page.getHtml(), "技统数据", "失球");
      String[] hostLossArray = lossPair.first.split("/");
      if (hostLossArray.length >= 2) {
        page.putField("hostLossOf3", Utils.valueOfFloat(hostLossArray[0]));
        page.putField("hostLossOf10", Utils.valueOfFloat(hostLossArray[1]));
      }

      String[] customLossArray = lossPair.second.split("/");
      if (customLossArray.length >= 2) {
        page.putField("customLossOf3", Utils.valueOfFloat(customLossArray[0]));
        page.putField("customLossOf10", Utils.valueOfFloat(customLossArray[1]));
      }
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }


    // hostCornerOf3, 主队近3场平均脚球数
    // customCornerOf3, 客队近3场平均角球数
    // hostCornerOf10, 主队近10场平均角球数
    // customCornerOf10, 客队近10场平均角球数
    try {
      Pair<String, String> cornerPair = SpiderUtils.selectTableOfTd(page.getHtml(), "技统数据", "角球");
      String[] hostCornerArray = cornerPair.first.split("/");
      if (hostCornerArray.length >= 2) {
        page.putField("hostCornerOf3", Utils.valueOfFloat(hostCornerArray[0]));
        page.putField("hostCornerOf10", Utils.valueOfFloat(hostCornerArray[1]));
      }

      String[] customCornerArray = cornerPair.second.split("/");
      if (customCornerArray.length >= 2) {
        page.putField("customCornerOf3", Utils.valueOfFloat(customCornerArray[0]));
        page.putField("customCornerOf10", Utils.valueOfFloat(customCornerArray[1]));
      }
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    // hostYellowCardOf3, 主队近3场平均黄卡数
    // customYellowCardOf3, 客队近3场平均黄卡数
    // hostYellowCardOf10, 主队近10场平均黄卡数
    // customYellowCardOf10, 客队近10场平均黄卡数
    try {
      Pair<String, String> yellowCardPair =
          SpiderUtils.selectTableOfTd(page.getHtml(), "技统数据", "黄牌");
      String[] hostYellowCardArray = yellowCardPair.first.split("/");
      if (hostYellowCardArray.length >= 2) {
        page.putField("hostYellowCardOf3", Utils.valueOfFloat(hostYellowCardArray[0]));
        page.putField("hostYellowCardOf10", Utils.valueOfFloat(hostYellowCardArray[1]));
      }

      String[] customYellowCardArray = yellowCardPair.second.split("/");
      if (customYellowCardArray.length >= 2) {
        page.putField("customYellowCardOf3", Utils.valueOfFloat(customYellowCardArray[0]));
        page.putField("customYellowCardOf10", Utils.valueOfFloat(customYellowCardArray[1]));
      }
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    // hostControlRateOf3, 主队近3场平均控球率
    // customControlRateOf3, 客队近3场平均控球率
    // hostControlRateOf10, 主队近10场平均控球率
    // customControlRateOf10, 客队近10场平均控球率
    try {
      Pair<String, String> controlRatePair =
          SpiderUtils.selectTableOfTd(page.getHtml(), "技统数据", "控球率");
      String[] hostControlRateArray = controlRatePair.first.split("/");
      if (hostControlRateArray.length >= 2) {
        page.putField("hostControlRateOf3",
            Utils.valueOfFloat(hostControlRateArray[0].replace("%", "")));
        page.putField("hostControlRateOf10",
            Utils.valueOfFloat(hostControlRateArray[1].replace("%", "")));
      }

      String[] customControlRateArray = controlRatePair.second.split("/");
      if (customControlRateArray.length >= 2) {
        page.putField("customControlRateOf3",
            Utils.valueOfFloat(customControlRateArray[0].replace("%", "")));
        page.putField("customControlRateOf10",
            Utils.valueOfFloat(customControlRateArray[1].replace("%", "")));
      }
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    if (!mPredicate.test(page)) {
      page.setSkip(true);
      return;
    }

    // 如果页面复合条件，则继续抓取, matchID越小，优先级越高
    List<String> requests = new ArrayList<>();
    requests.add(UrlType.ANALYSIS.buildUrl(matchID));
    // requests.add(UrlType.SCORE.buildUrl(matchID));
    requests.add(UrlType.SCORE_ODD.buildUrl(matchID));
    requests.add(UrlType.CORNER_ODD.buildUrl(matchID));
    page.addTargetRequests(requests, Integer.MAX_VALUE - matchID);

    // mLogger.log("(Detail) => " + matchID);
    // mLogger.log(page.getResultItems().getAll());
    int cnt = mDetailCount.getAndIncrement();
    mLogger.log(String.format("GET: matchID=%d, valueCount=%d", matchID, cnt));
  }


  private AtomicInteger mDetailCount = new AtomicInteger(0);
}

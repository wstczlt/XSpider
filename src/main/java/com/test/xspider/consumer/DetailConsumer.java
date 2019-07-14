package com.test.xspider.consumer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.github.promeg.pinyinhelper.Pinyin;
import com.test.xspider.PageConsumer;
import com.test.xspider.Pair;
import com.test.xspider.XSpiderUtils;
import com.test.xspider.model.UrlType;

import us.codecraft.webmagic.Page;

// => http://score.nowscore.com/detail/1662654cn.html
// 只爬取已结束的比赛
public class DetailConsumer implements PageConsumer {

  // ----- 静态信息 ---
  // matchID, 比赛ID
  // matchTime, 比赛时间
  // weather, 天气(原文未加工)
  // hostName, 主队名称
  // hostNamePinyin, 主队名称拼音, 方便匹配
  // customName, 客队名称
  // customNamePinyin, 客队名称拼音
  // hostScore, 主队得分, 如果比赛已结束(未结束)
  // customScore, 客队得分，如果比赛已结束

  // ----- 技术统计 ----
  // hostCornerScore, 主队角球数
  // customCornerScore, 客队角球数
  // hostYellowCard, 主队黄卡数
  // customYellowCard, 客队黄卡数
  // hostRedCard, 主队红卡数
  // customRedCard, 客队红卡数
  // hostShoot, 主队射门数
  // customShoot, 客队射门数
  // hostBestShoot, 主队射正次数
  // customBestShoot, 客队射正次数
  // hostAttack, 主队进攻次数
  // customAttach, 客队进攻次数
  // hostBestAttack, 主队危险进攻
  // customBestAttack, 客队危险进攻
  // hostControlRate, 主队控球率
  // customControlRate, 客队控球率

  // ----- 历史技术统计 ---
  // hostScoreOf3, 主队近3场平均进球数
  // customScoreOf3, 客队近3场平均进球数
  // hostScoreOf10, 主队近10场平均进球数
  // customScoreOf10, 客队近10场平均进球数
  // hostLossOf3, 主队近3场平均丢球数
  // customLossOf3, 客队近3场平均丢球数
  // hostLossOf10, 主队近10场平均丢球数
  // customLossOf10, 客队近10场平均丢球数
  // hostCornerOf3, 主队近3场平均脚球数
  // customCornerOf3, 客队近3场平均角球数
  // hostCornerOf10, 主队近10场平均角球数
  // customCornerOf10, 客队近10场平均角球数
  // hostYellowCardOf3, 主队近3场平均黄卡数
  // customYellowCardOf3, 客队近3场平均黄卡数
  // hostYellowCardOf10, 主队近10场平均黄卡数
  // customYellowCardOf10, 客队近10场平均黄卡数
  // hostControlRateOf3, 主队近3场平均控球率
  // customControlRateOf3, 客队近3场平均控球率
  // hostControlRateOf10, 主队近10场平均控球率
  // customControlRateOf10, 客队近10场平均控球率

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd HH:mm");

  @Override
  public void accept(Page page) {
    UrlType urlType = UrlType.formUrl(page.getUrl().toString());
    if (urlType != UrlType.DETAIL) {
      return;
    }
    System.out.println("DetailConsumer => " + page.getUrl());

    try { // matchID, 比赛ID => /detail/1747187cn.html
      String matchIDString = page.getUrl().regex("[0-9]+").toString();
      page.putField("matchID", Long.parseLong(matchIDString));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
      page.setSkip(true); // 没有matchID直接抛弃
      return;
    }

    try { // matchTime, 比赛时间，=> 开赛时间：2019-07-11 17:00
      int matchTimeStart = page.getRawText().indexOf("开赛时间：");
      int matchTimeEnd = page.getRawText().indexOf("</span>", matchTimeStart);
      String matchTimeString = page.getRawText().substring(matchTimeStart, matchTimeEnd);
      matchTimeString = matchTimeString.substring("开赛时间：".length());
      Date date = DATE_FORMAT.parse(matchTimeString);
      page.putField("matchTime", date.getTime());
    } catch (Throwable e) { // 可忽略
      XSpiderUtils.log(e);
    }

    try { // weather, 天气(原文未加工) => 天气：多云 温度：24℃～25℃
      int weatherStart = page.getRawText().indexOf("天气：");
      int weatherEnd = page.getRawText().indexOf("<", weatherStart);
      String weather = page.getRawText().substring(weatherStart, weatherEnd);
      weather = weather.replace("\r", "");
      weather = weather.replace("\n", "");
      page.putField("weather", weather);
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    try { // hostName, 主队名称; customName, 客队名称
      String hostName =
          page.getHtml().xpath("//div[@id=home]/a/span[@class=name]/text()").toString();
      String customName =
          page.getHtml().xpath("//div[@id=guest]/a/span[@class=name]/text()").toString();
      page.putField("hostName", hostName.replace("(中)", "")); // 去掉中立场
      page.putField("hostNamePinyin", Pinyin.toPinyin(hostName, ""));
      page.putField("customName", customName);
      page.putField("customNamePinyin", Pinyin.toPinyin(hostName, ""));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    try { // hostScore, customScore, 主客队比分
      List<String> scores = page.getHtml().xpath("//span[@class='b t15']/text()").all();
      page.putField("hostScore", scores.get(0));
      page.putField("customScore", scores.get(1));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    // ------ 技术统计 ------

    try { // hostCornerScore, customCornerScore, 主客队角球数
      Pair<String, String> cornerScorePair =
          XSpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "角球");
      page.putField("hostCornerScore", Integer.parseInt(cornerScorePair.first));
      page.putField("customCornerScore", Integer.parseInt(cornerScorePair.second));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    try { // hostYellowCard, customYellowCard, 主客队黄卡数
      Pair<String, String> yellowCardPair =
          XSpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "黄牌");
      page.putField("hostYellowCard", Integer.parseInt(yellowCardPair.first));
      page.putField("customYellowCard", Integer.parseInt(yellowCardPair.second));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    try { // hostRedCard, customRedCard, 主客队红卡数
      Pair<String, String> redCardPair = XSpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "红牌");
      page.putField("hostRedCard", Integer.parseInt(redCardPair.first));
      page.putField("customRedCard", Integer.parseInt(redCardPair.second));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    try { // hostShoot, customShoot, 主客队射门数
      Pair<String, String> shootPair = XSpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "射门");
      page.putField("hostShoot", Integer.parseInt(shootPair.first));
      page.putField("customShoot", Integer.parseInt(shootPair.second));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    try { // hostBestShoot, customBestShoot, 主客队射正数
      Pair<String, String> bestShootPair =
          XSpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "射正");
      page.putField("hostBestShoot", Integer.parseInt(bestShootPair.first));
      page.putField("customBestShoot", Integer.parseInt(bestShootPair.second));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    try { // hostAttack, customAttach, 主客队进攻数
      Pair<String, String> attackPair = XSpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "进攻");
      page.putField("hostAttack", Integer.parseInt(attackPair.first));
      page.putField("customAttach", Integer.parseInt(attackPair.second));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    try { // hostBestAttack, customBestAttack, 主客队危险进攻数
      Pair<String, String> bestAttackPair =
          XSpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "危险进攻");
      page.putField("hostBestAttack", Integer.parseInt(bestAttackPair.first));
      page.putField("customBestAttack", Integer.parseInt(bestAttackPair.second));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    try { // hostControlRate, customControlRate, 主客队危险进攻数
      Pair<String, String> controlRatePair =
          XSpiderUtils.selectTableOfTd(page.getHtml(), "技术统计", "控球率");
      page.putField("hostControlRate", Float.parseFloat(controlRatePair.first.replace("%", "")));
      page.putField("customControlRate", Float.parseFloat(controlRatePair.second.replace("%", "")));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }


    // ------ 技统数据 -----

    // hostScoreOf3, 主队近3场平均进球数
    // customScoreOf3, 客队近3场平均进球数
    // hostScoreOf10, 主队近10场平均进球数
    // customScoreOf10, 客队近10场平均进球数
    try {
      Pair<String, String> scorePair = XSpiderUtils.selectTableOfTd(page.getHtml(), "技统数据", "进球");
      String[] hostScoreArray = scorePair.first.split("/");
      page.putField("hostScoreOf3", Float.parseFloat(hostScoreArray[0]));
      page.putField("hostScoreOf10", Float.parseFloat(hostScoreArray[1]));

      String[] customScoreArray = scorePair.second.split("/");
      page.putField("customScoreOf3", Float.parseFloat(customScoreArray[0]));
      page.putField("customScoreOf10", Float.parseFloat(customScoreArray[1]));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    // hostLossOf3, 主队近3场平均丢球数
    // customLossOf3, 客队近3场平均丢球数
    // hostLossOf10, 主队近10场平均丢球数
    // customLossOf10, 客队近10场平均丢球数
    try {
      Pair<String, String> lossPair = XSpiderUtils.selectTableOfTd(page.getHtml(), "技统数据", "失球");
      String[] hostLossArray = lossPair.first.split("/");
      page.putField("hostLossOf3", Float.parseFloat(hostLossArray[0]));
      page.putField("hostLossOf10", Float.parseFloat(hostLossArray[1]));

      String[] customLossArray = lossPair.second.split("/");
      page.putField("customLossOf3", Float.parseFloat(customLossArray[0]));
      page.putField("customLossOf10", Float.parseFloat(customLossArray[1]));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }


    // hostCornerOf3, 主队近3场平均脚球数
    // customCornerOf3, 客队近3场平均角球数
    // hostCornerOf10, 主队近10场平均角球数
    // customCornerOf10, 客队近10场平均角球数
    try {
      Pair<String, String> cornerPair = XSpiderUtils.selectTableOfTd(page.getHtml(), "技统数据", "角球");
      String[] hostCornerArray = cornerPair.first.split("/");
      page.putField("hostCornerOf3", Float.parseFloat(hostCornerArray[0]));
      page.putField("hostCornerOf10", Float.parseFloat(hostCornerArray[1]));

      String[] customCornerArray = cornerPair.second.split("/");
      page.putField("customCornerOf3", Float.parseFloat(customCornerArray[0]));
      page.putField("customCornerOf10", Float.parseFloat(customCornerArray[1]));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    // hostYellowCardOf3, 主队近3场平均黄卡数
    // customYellowCardOf3, 客队近3场平均黄卡数
    // hostYellowCardOf10, 主队近10场平均黄卡数
    // customYellowCardOf10, 客队近10场平均黄卡数
    try {
      Pair<String, String> yellowCardPair =
          XSpiderUtils.selectTableOfTd(page.getHtml(), "技统数据", "黄牌");
      String[] hostYellowCardArray = yellowCardPair.first.split("/");
      page.putField("hostYellowCardOf3", Float.parseFloat(hostYellowCardArray[0]));
      page.putField("hostYellowCardOf10", Float.parseFloat(hostYellowCardArray[1]));

      String[] customYellowCardArray = yellowCardPair.second.split("/");
      page.putField("customYellowCardOf3", Float.parseFloat(customYellowCardArray[0]));
      page.putField("customYellowCardOf10", Float.parseFloat(customYellowCardArray[1]));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    // hostControlRateOf3, 主队近3场平均控球率
    // customControlRateOf3, 客队近3场平均控球率
    // hostControlRateOf10, 主队近10场平均控球率
    // customControlRateOf10, 客队近10场平均控球率
    try {
      Pair<String, String> controlRatePair =
          XSpiderUtils.selectTableOfTd(page.getHtml(), "技统数据", "控球率");
      String[] hostControlRateArray = controlRatePair.first.split("/");
      page.putField("hostControlRateOf3",
          Float.parseFloat(hostControlRateArray[0].replace("%", "")));
      page.putField("hostControlRateOf10",
          Float.parseFloat(hostControlRateArray[1].replace("%", "")));

      String[] customControlRateArray = controlRatePair.second.split("/");
      page.putField("customControlRateOf3",
          Float.parseFloat(customControlRateArray[0].replace("%", "")));
      page.putField("customControlRateOf10",
          Float.parseFloat(customControlRateArray[1].replace("%", "")));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

  }


}

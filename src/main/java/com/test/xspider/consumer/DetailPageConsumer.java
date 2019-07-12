package com.test.xspider.consumer;

import com.github.promeg.pinyinhelper.Pinyin;
import com.test.xspider.PageConsumer;
import com.test.xspider.model.UrlType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import us.codecraft.webmagic.Page;

// => http://score.nowscore.com/detail/1662654cn.html
// 只爬取已结束的比赛
public class DetailPageConsumer implements PageConsumer {

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
  // customScore, 客队角球数
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

  private static void log(Throwable e) {
    e.printStackTrace();
  }

  @Override
  public void accept(Page page) {
    UrlType urlType = UrlType.formUrl(page.getUrl().toString());
    if (urlType != UrlType.DETAIL) {
      return;
    }
    System.out.println("DetailPageConsumer => " + page.getUrl());

    try { // matchID, 比赛ID => /detail/1747187cn.html
      String matchIDString = page.getUrl().regex("[0-9]+").toString();
      page.putField("matchID", Long.parseLong(matchIDString));
    } catch (Throwable e) {
      log(e);
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
      log(e);
    }

    try { // weather, 天气(原文未加工) => 天气：多云 温度：24℃～25℃
      int weatherStart = page.getRawText().indexOf("天气：");
      int weatherEnd = page.getRawText().indexOf("<", weatherStart);
      String weather = page.getRawText().substring(weatherStart, weatherEnd);
      weather = weather.replace("\r", "");
      weather = weather.replace("\n", "");
      page.putField("weather", weather);
    } catch (Throwable e) {
      log(e);
    }

    // hostName, 主队名称
    // hostNamePinyin, 主队名称拼音, 方便匹配
    // customName, 客队名称
    // customNamePinyin, 客队名称拼音
    // hostScore, 主队得分, 如果比赛已结束(未结束)
    // customScore, 客队得分，如果比赛已结束

    try { // hostName, 主队名称; customName, 客队名称
      String hostName = page.getHtml().xpath("//div[@id=home]/a/span[@class=name]/text()").toString();
      String customName = page.getHtml().xpath("//div[@id=guest]/a/span[@class=name]/text()").toString();
      page.putField("hostName", hostName);
      page.putField("hostNamePinyin", Pinyin.toPinyin(hostName, ""));
      page.putField("customName", customName);
      page.putField("customNamePinyin", Pinyin.toPinyin(hostName, ""));
    } catch (Throwable e) {
      log(e);
    }

    try { // hostScore, customScore, 主客队比分
      List<String> scores = page.getHtml().xpath("//span[@class='b t15']/text()").all();
      page.putField("hostScore", scores.get(0));
      page.putField("customScore", scores.get(1));
    } catch (Throwable e) {
      log(e);
    }

  }
}

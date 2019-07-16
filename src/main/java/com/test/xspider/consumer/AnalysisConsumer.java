package com.test.xspider.consumer;

import java.util.List;

import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import com.test.xspider.PageConsumer;
import com.test.xspider.XSpiderUtils;
import com.test.xspider.model.UrlType;

import us.codecraft.webmagic.Page;

// => "http://score.nowscore.com/analysis/1662654cn.html"
public class AnalysisConsumer implements PageConsumer {

  // matchID, 比赛ID
  // league, 联赛名称
  // hostLeagueRank, 主队联赛排名
  // hostLeagueRateOfVictory, 主队联赛总胜率
  // hostLeagueOnHostRank, 主队联赛主场排名
  // hostLeagueOnHostRateOfVictory, 主队联赛主场总胜率
  // customLeagueRank, 客队联赛排名
  // customLeagueRateOfVictory, 客队联赛总胜率
  // customLeagueOnCustomRank, 客队联赛客场排名
  // customLeagueOnCustomRateOfVictory, 客队联赛客场总胜率


  // hostPreviousMatchDay, customPreviousMatchDay, 主客队上场比赛间隔天数
  // hostNextMatchDay, customNextMatchDay, 主客队下场比赛间隔天数
  // bifaOddOfVictory(胜), bifaOddOfDraw(平), bifaOddOfDefeat(负), 必发指数胜平负
  // bifaHotOfVictory(胜), bifaHotOfDraw(平), bifaHotOfDefeat(负), 必发冷热指数胜平负


  // 客队近10场胜率、让胜率、大球率
  // customHistoryRateOfVictory(胜), customHistoryRateOfLetVictory(让胜), customHistoryRateOfBig(大球),
  // 主队近10场胜率、让胜率、大球率
  // hostHistoryRateOfVictory(胜), hostHistoryRateOfLetVictory(让胜), hostHistoryRateOfBig(大球),

  @Override
  public void accept(Page page) {
    UrlType urlType = UrlType.formUrl(page.getUrl().toString());
    if (urlType != UrlType.ANALYSIS) {
      return;
    }
    final JXDocument xpath = JXDocument.create(page.getRawText());
    System.out.println("Consumer(Analysis) => " + page.getUrl());
    final int matchID = XSpiderUtils.extractMatchID(page);
    if (matchID <= 0) {
      return;
    }

    try { // league, 联赛名称
      String leagueString = xpath.selNOne("//a[@style='color:#f00;']/text()").asString();
      page.putField("league", leagueString.replace("资料", ""));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    // hostLeagueRank, 主队联赛排名
    // hostLeagueRateOfVictory, 主队联赛总胜率
    // hostLeagueOnHostRank, 主队联赛主场排名
    // hostLeagueOnHostRateOfVictory, 主队联赛主场总胜率
    try {
      // <td width='43px' height=20>全场</td> -> 0
      // <td width='25px'>赛</td> -> 1
      // <td width='25px'>胜</td> -> 2
      // <td width='25px'>平</td> -> 3
      // <td width='25px'>负</td> -> 4
      // <td width='25px'>得</td> -> 5
      // <td width='25px'>失</td> -> 6
      // <td width='25px'>净</td> -> 7
      // <td width='48px'>得分</td> -> 8
      // <td width='30px'>排名</td> -> 9
      // <td>胜率</td> -> 10

      // 获取主队(总)：总排名、总胜率
      List<JXNode> hostData = xpath.selN("//tr[@bgcolor='#FFECEC']").get(0).sel("//td/text()");
      page.putField("hostLeagueRank", XSpiderUtils.valueOfInt(hostData.get(9).asString()));
      page.putField("hostLeagueRateOfVictory",
          XSpiderUtils.valueOfFloat(hostData.get(10).asString().replace("%", "")));
      // 获取主队(主场)：总排名、总胜率
      List<JXNode> hostDataOfHost =
          xpath.selN("//tr[@bgcolor='#FFECEC']").get(1).sel("//td/text()");
      page.putField("hostLeagueOnHostRank",
          XSpiderUtils.valueOfInt(hostDataOfHost.get(9).asString()));
      page.putField("hostLeagueOnHostRateOfVictory",
          XSpiderUtils.valueOfFloat(hostDataOfHost.get(10).asString().replace("%", "")));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

    // customLeagueRank, 客队联赛排名
    // customLeagueRateOfVictory, 客队联赛总胜率
    // customLeagueOnCustomRank, 客队联赛客场排名
    // customLeagueOnCustomRateOfVictory, 客队联赛客场总胜率
    try {
      // 获取客队(总)：总排名、总胜率
      List<JXNode> hostData = xpath.selN("//tr[@bgcolor='#CCCCFF']").get(0).sel("//td/text()");
      page.putField("customLeagueRank", XSpiderUtils.valueOfInt(hostData.get(9).asString()));
      page.putField("customLeagueRateOfVictory",
          XSpiderUtils.valueOfFloat(hostData.get(10).asString().replace("%", "")));
      // 获取客队(客场)：总排名、总胜率
      List<JXNode> hostDataOfHost =
          xpath.selN("//tr[@bgcolor='#CCCCFF']").get(2).sel("//td/text()");
      page.putField("customLeagueOnCustomRank",
          XSpiderUtils.valueOfInt(hostDataOfHost.get(9).asString()));
      page.putField("customLeagueOnCustomRateOfVictory",
          XSpiderUtils.valueOfFloat(hostDataOfHost.get(10).asString().replace("%", "")));
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }



    // historyRateOfVictory(胜), historyRateOfLetVictory(让胜), historyRateOfBig(大球), 交战历史主队胜率、让胜率、大球率
    // try {
    // String historyRateOfVictoryString =
    // xpath.selNOne("//span[@id='winPre_v']/text()").asString();
    // page.putField("historyRateOfVictory", historyRateOfVictoryString.replace("%", ""));
    // String historyRateOfLetVictoryString =
    // xpath.selNOne("//span[@id='awin_v']/text()").asString();
    // page.putField("historyRateOfLetVictory", historyRateOfLetVictoryString.replace("%", ""));
    // String historyRateOfBigString = xpath.selNOne("//span[@id='big_v']/text()").asString();
    // page.putField("historyRateOfBig", historyRateOfBigString.replace("%", ""));
    // } catch (Throwable e) {
    // XSpiderUtils.log(e);
    // }

    // => 必发指数：http://score.nowscore.com/analysis/odds/1662654.htm?1563269160000=
  }
}

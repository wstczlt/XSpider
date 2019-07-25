package com.test.spider.consumer;

import java.util.List;

import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import com.test.spider.SpiderUtils;
import com.test.spider.model.UrlType;
import com.test.spider.tools.Logger;

import us.codecraft.webmagic.Page;

// => "http://score.nowscore.com/analysis/1662654cn.html"
public class AnalysisConsumer implements Consumer {

  private final Logger mLogger;

  public AnalysisConsumer(Logger logger) {
    mLogger = logger;
  }

  @Override
  public void accept(Page page) {
    UrlType urlType = UrlType.formUrl(page.getUrl().toString());
    if (urlType != UrlType.ANALYSIS) {
      return;
    }
    final JXDocument xpath = JXDocument.create(page.getRawText());
    final int matchID = SpiderUtils.extractMatchID(page);
    if (matchID <= 0) {
      return;
    }

    try { // league, 联赛名称
      JXNode leagueNode = xpath.selNOne("//a[@style='color:#f00;']/text()");
      if (leagueNode != null) {
        page.putField("league", leagueNode.toString().replace("资料", ""));
      }
    } catch (Throwable e) {
      SpiderUtils.log(e);
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
      page.putField("hostLeagueRank", SpiderUtils.valueOfInt(hostData.get(9).asString()));
      page.putField("hostLeagueRateOfVictory",
          SpiderUtils.valueOfFloat(hostData.get(10).asString().replace("%", "")));
      // 获取主队(主场)：总排名、总胜率
      List<JXNode> hostDataOfHost =
          xpath.selN("//tr[@bgcolor='#FFECEC']").get(1).sel("//td/text()");
      page.putField("hostLeagueOnHostRank",
          SpiderUtils.valueOfInt(hostDataOfHost.get(9).asString()));
      page.putField("hostLeagueOnHostRateOfVictory",
          SpiderUtils.valueOfFloat(hostDataOfHost.get(10).asString().replace("%", "")));
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    // customLeagueRank, 客队联赛排名
    // customLeagueRateOfVictory, 客队联赛总胜率
    // customLeagueOnCustomRank, 客队联赛客场排名
    // customLeagueOnCustomRateOfVictory, 客队联赛客场总胜率
    try {
      // 获取客队(总)：总排名、总胜率
      List<JXNode> hostData = xpath.selN("//tr[@bgcolor='#CCCCFF']").get(0).sel("//td/text()");
      page.putField("customLeagueRank", SpiderUtils.valueOfInt(hostData.get(9).asString()));
      page.putField("customLeagueRateOfVictory",
          SpiderUtils.valueOfFloat(hostData.get(10).asString().replace("%", "")));
      // 获取客队(客场)：总排名、总胜率
      List<JXNode> hostDataOfHost =
          xpath.selN("//tr[@bgcolor='#CCCCFF']").get(2).sel("//td/text()");
      page.putField("customLeagueOnCustomRank",
          SpiderUtils.valueOfInt(hostDataOfHost.get(9).asString()));
      page.putField("customLeagueOnCustomRateOfVictory",
          SpiderUtils.valueOfFloat(hostDataOfHost.get(10).asString().replace("%", "")));
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }
//    mLogger.log("(Analysis) => " + matchID);
    // mLogger.log(page.getResultItems().getAll());



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

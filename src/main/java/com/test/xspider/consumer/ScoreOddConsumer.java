package com.test.xspider.consumer;

import java.util.List;

import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import com.test.xspider.PageConsumer;
import com.test.xspider.XSpiderUtils;
import com.test.xspider.model.UrlType;

import us.codecraft.webmagic.Page;

// => http://score.nowscore.com/odds/3in1Odds.aspx?companyid=3&id=1662653
// 皇冠指数集合
public class ScoreOddConsumer implements PageConsumer {

  // 亚盘指数：初盘、即时盘、中场盘、00~100分钟盘口
  // 大小球指数: 初盘、即时盘、中场盘、00~100分钟盘口
  // 欧盘胜平负指数：初盘、即时盘、中场盘、00~100分钟盘口
  @Override
  public void accept(Page page) {
    UrlType urlType = UrlType.formUrl(page.getUrl().toString());
    if (urlType != UrlType.SCORE_ODD) {
      return;
    }
    final JXDocument xpath = JXDocument.create(page.getRawText());

    try {
      List<JXNode> trs = xpath.selN("//table[@bgcolor='#DDDDDD']").get(0).sel("//tr");
      for (int i = trs.size() - 1; i >= 0; i--) { // 倒着遍历
        // 时间(0), 未开赛可能为空
        // 比分(1), 未开赛可能为空
        // 上盘赔率(2), 封盘可能为空
        // 盘口(3), 封盘为封
        // 下盘赔率(4), 封盘可能为空
        List<JXNode> tds = trs.get(i).sel("//td/text()");
        String timePrefix = tds.get(0).asString(); // 时间
        if (timePrefix.trim().equals("")) { // 空
          timePrefix = "-" + i; // 例如: -33
        }
        String scoreString = tds.get(1).asString(); // 比分
        if (scoreString.equals("-")) {
          scoreString = "0-0";
        }
        int hostScore = Integer.parseInt(scoreString.split("-")[0]);
        int customScore = Integer.parseInt(scoreString.split("-")[1]);

        String oddString = tds.get(3).asString(); // 盘口
        if (oddString.equals("封")) { // 封盘，扔掉这条
          continue;
        }
        float odd = XSpiderUtils.convertOdd(oddString); // 盘口
        String oddOfVictoryString = tds.get(2).asString(); // 上盘赔率
        float oddOfVictory = Float.parseFloat(oddOfVictoryString);
        String oddOfDefeatString = tds.get(2).asString(); // 下盘赔率
        float oddOfDefeat = Float.parseFloat(oddOfDefeatString);


        if (i == trs.size()) { // 初盘
          timePrefix = "original";
        }
        page.putField(timePrefix + "_" + "hostScore", hostScore);
        page.putField(timePrefix + "_" + "customScore", customScore);
        page.putField(timePrefix + "_" + "odd", odd);
        page.putField(timePrefix + "_" + "oddOfVictory", oddOfVictory);
        page.putField(timePrefix + "_" + "oddOfDefeat", oddOfDefeat);
      }
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }
  }
}

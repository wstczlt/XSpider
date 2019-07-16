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
public class CornerOddConsumer implements PageConsumer {

  private static final String PREFIX_ORIGINAL = "original"; // 初盘
  private static final String PREFIX_OPENING = "opening"; // 比赛开始前的即时盘
  private static final String PREFIX_MIDDLE = "middle"; // 中场盘


  // 角球大小指数: 初盘、即时盘、中场盘、00~100分钟盘口
  @Override
  public void accept(Page page) {
    UrlType urlType = UrlType.formUrl(page.getUrl().toString());
    if (urlType != UrlType.CORNER_ODD) {
      return;
    }
    final JXDocument xpath = JXDocument.create(page.getRawText());
    System.out.println("Consumer(CornerOdd) => " + page.getUrl());
    final int matchID = XSpiderUtils.extractMatchID(page);
    if (matchID <= 0) {
      return;
    }

    try { // 角球大小盘口
      List<JXNode> trs = xpath.selN("//table[@bgcolor='#DDDDDD']").get(1).sel("//tr");
      boolean firstMin = false;
      for (int i = trs.size() - 1; i > 0; i--) { // 倒着遍历, index=0是标题，不要
        // <td width="40">时间</td> => 未开赛可能为空
        // <td width="40">比数</td>=> 未开赛可能为空
        // <td width="70">大球</td>=> 封盘可能为空
        // <td width="60">大小</td>=> 封盘为封
        // <td width="70">小球</td>=> 封盘可能为空
        // <td width="70">变化时间</td>
        // <td>状态</td>
        List<JXNode> tds = trs.get(i).sel("//td/text()");
        if (tds.size() != 7) {
          continue;
        }
        String stringOfMin = tds.get(0).asString(); // 时间
        if (i == trs.size() - 1) { // 初盘
          stringOfMin = PREFIX_ORIGINAL;
        } else if (!firstMin && XSpiderUtils.valueOfInt(stringOfMin) >= 0) {
          // 首个出现的分钟, 作为即时盘
          stringOfMin = PREFIX_OPENING;
          firstMin = true;
        } else if (stringOfMin.trim().equals("中场") || stringOfMin.trim().equals("半")) {
          stringOfMin = PREFIX_MIDDLE;
        } else {
          continue; // 丢弃，本次不做滚球盘
        }
        stringOfMin = stringOfMin + "_";

        String scoreString = tds.get(1).asString(); // 比分
        if (scoreString.equals("-") || scoreString.equals("")) {
          scoreString = "0-0";
        }
        int hostCornerScore = XSpiderUtils.valueOfInt(scoreString.split("-")[0]);
        int customCornerScore = XSpiderUtils.valueOfInt(scoreString.split("-")[1]);
        if (hostCornerScore < 0 || customCornerScore < 0) {
          continue;
        }

        String bigOddString = tds.get(3).asString(); // 盘口
        if (bigOddString.equals("封")) { // 封盘，扔掉这条
          continue;
        }
        float cornerOdd = XSpiderUtils.valueOfFloat(bigOddString); // 盘口
        String cornerOddOfVictoryString = tds.get(2).asString(); // 上盘赔率
        float cornerOddOfVictory = XSpiderUtils.valueOfFloat(cornerOddOfVictoryString);
        String cornerOddOfDefeatString = tds.get(4).asString(); // 下盘赔率
        float cornerOddOfDefeat = XSpiderUtils.valueOfFloat(cornerOddOfDefeatString);

        page.putField(stringOfMin + "hostCornerScore", hostCornerScore);
        page.putField(stringOfMin + "customCornerScore", customCornerScore);
        page.putField(stringOfMin + "cornerOdd", cornerOdd);
        page.putField(stringOfMin + "cornerOddOfVictory", cornerOddOfVictory);
        page.putField(stringOfMin + "cornerOddOfDefeat", cornerOddOfDefeat);
      }
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }

  }
}

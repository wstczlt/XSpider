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

  private static final String PREFIX_ORIGINAL = "original"; // 初盘
  private static final String PREFIX_OPENING = "opening"; // 比赛开始前的即时盘
  private static final String PREFIX_MIDDLE = "middle"; // 中场盘


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
    System.out.println("Consumer(ScoreOdd) => " + page.getUrl());
    final int matchID = XSpiderUtils.extractMatchID(page);
    if (matchID <= 0) {
      return;
    }

    try { // 让球盘口
      List<JXNode> trs = xpath.selN("//table[@bgcolor='#DDDDDD']").get(0).sel("//tr");
      boolean firstMin = false;
      for (int i = trs.size() - 1; i > 0; i--) { // 倒着遍历, index=0是标题，不要
        // <td width="9%">时</td> => 未开赛可能为空
        // <td width="15%">比分</td> => 未开赛可能为空
        // <td width="17%">主</td> => 封盘可能为空
        // <td width="17%">盘</td> => 封盘为封
        // <td width="17%">客</td> => 封盘可能为空
        // <td width="16%">变化</td>
        // <td width="9%">状</td>
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
        if (scoreString.equals("-")) {
          scoreString = "0-0";
        }
        int hostScore = XSpiderUtils.valueOfInt(scoreString.split("-")[0]);
        int customScore = XSpiderUtils.valueOfInt(scoreString.split("-")[1]);
        if (hostScore < 0 || customScore < 0) {
          continue;
        }
        String scoreOddString = tds.get(3).asString(); // 盘口
        if (scoreOddString.equals("封")) { // 封盘，扔掉这条
          continue;
        }
        float scoreOdd = XSpiderUtils.convertOdd(scoreOddString); // 盘口
        String scoreOddOfVictoryString = tds.get(2).asString(); // 上盘赔率
        float scoreOddOfVictory = XSpiderUtils.valueOfFloat(scoreOddOfVictoryString);
        String scoreOddOfDefeatString = tds.get(4).asString(); // 下盘赔率
        float scoreOddOfDefeat = XSpiderUtils.valueOfFloat(scoreOddOfDefeatString);


        page.putField(stringOfMin + "hostScore", hostScore);
        page.putField(stringOfMin + "customScore", customScore);
        page.putField(stringOfMin + "scoreOdd", scoreOdd);
        page.putField(stringOfMin + "scoreOddOfVictory", scoreOddOfVictory);
        page.putField(stringOfMin + "scoreOddOfDefeat", scoreOddOfDefeat);
      }
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }


    try { // 大小球盘口
      List<JXNode> trs = xpath.selN("//table[@bgcolor='#DDDDDD']").get(1).sel("//tr");
      boolean firstMin = false;
      for (int i = trs.size() - 1; i > 0; i--) { // 倒着遍历, index=0是标题，不要
        // <td width="9%">时</td> => 未开赛可能为空
        // <td width="15%">比分</td> => 未开赛可能为空
        // <td width="17%">大</td> => 封盘可能为空
        // <td width="17%">盘</td> => 封盘为封
        // <td width="17%">小</td> => 封盘可能为空
        // <td width="16%">变化</td>
        // <td width="9%">状</td>

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
        if (scoreString.equals("-")) {
          scoreString = "0-0";
        }
        int hostScore = XSpiderUtils.valueOfInt(scoreString.split("-")[0]);
        int customScore = XSpiderUtils.valueOfInt(scoreString.split("-")[1]);
        if (hostScore < 0 || customScore < 0) {
          continue;
        }

        String bigOddString = tds.get(3).asString(); // 盘口
        if (bigOddString.equals("封")) { // 封盘，扔掉这条
          continue;
        }
        float bigOdd = XSpiderUtils.valueOfFloat(bigOddString); // 盘口
        String bigOddOfVictoryString = tds.get(2).asString(); // 上盘赔率
        float bigOddOfVictory = XSpiderUtils.valueOfFloat(bigOddOfVictoryString);
        String bigOddOfDefeatString = tds.get(4).asString(); // 下盘赔率
        float bigOddOfDefeat = XSpiderUtils.valueOfFloat(bigOddOfDefeatString);


        page.putField(stringOfMin + "hostScore", hostScore);
        page.putField(stringOfMin + "customScore", customScore);
        page.putField(stringOfMin + "bigOdd", bigOdd);
        page.putField(stringOfMin + "bigOddOfVictory", bigOddOfVictory);
        page.putField(stringOfMin + "bigOddOfDefeat", bigOddOfDefeat);
      }
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }



    try { // 胜平负欧盘盘口
      List<JXNode> trs = xpath.selN("//table[@bgcolor='#DDDDDD']").get(2).sel("//tr");
      boolean firstMin = false;
      for (int i = trs.size() - 1; i > 0; i--) { // 倒着遍历, index=0是标题，不要
        // <td width="9%">时</td> => 未开赛可能为空
        // <td width="15%">比分</td> => 未开赛可能为空
        // <td width="17%">主</td> => 封盘可能为空
        // <td width="17%">和局</td>=> 封盘为封
        // <td width="17%">客</td> => 封盘可能为空
        // <td width="16%">变化</td>
        // <td width="9%">状</td>

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
        if (scoreString.equals("-")) {
          scoreString = "0-0";
        }
        int hostScore = XSpiderUtils.valueOfInt(scoreString.split("-")[0]);
        int customScore = XSpiderUtils.valueOfInt(scoreString.split("-")[1]);
        if (hostScore < 0 || customScore < 0) {
          continue;
        }

        String drawOddString = tds.get(3).asString(); // 平赔盘口
        if (drawOddString.equals("封")) { // 封盘，扔掉这条
          continue;
        }
        float drawOdd = XSpiderUtils.valueOfFloat(drawOddString); // 平赔盘口
        String victoryOddString = tds.get(2).asString(); // 胜赔率
        float victoryOdd = XSpiderUtils.valueOfFloat(victoryOddString);
        String defeatOddString = tds.get(4).asString(); // 负赔率
        float defeatOdd = XSpiderUtils.valueOfFloat(defeatOddString);

        page.putField(stringOfMin + "hostScore", hostScore);
        page.putField(stringOfMin + "customScore", customScore);
        page.putField(stringOfMin + "drawOdd", drawOdd);
        page.putField(stringOfMin + "victoryOdd", victoryOdd);
        page.putField(stringOfMin + "defeatOdd", defeatOdd);
      }
    } catch (Throwable e) {
      XSpiderUtils.log(e);
    }
  }
}

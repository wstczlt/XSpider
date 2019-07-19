package com.test.spider.consumer;

import org.seimicrawler.xpath.JXDocument;

import com.test.spider.SpiderUtils;
import com.test.spider.model.TableModel;
import com.test.spider.model.UrlType;

import us.codecraft.webmagic.Page;

// => http://score.nowscore.com/odds/3in1Odds.aspx?companyid=3&id=1662653
// 皇冠指数集合
public class ScoreOddConsumer implements Consumer {

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
    final int matchID = SpiderUtils.extractMatchID(page);
    if (matchID <= 0) {
      return;
    }
    try { // 让球
      String rawText = xpath.selN("//table[@bgcolor='#DDDDDD']").get(0).toString();
      TableModel tableModel = new TableModel(TableModel.TableType.SCORE, rawText);
      tableModel.fillPage(page);
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    try { // 大小球
      String rawText = xpath.selN("//table[@bgcolor='#DDDDDD']").get(1).toString();
      TableModel tableModel = new TableModel(TableModel.TableType.BALL, rawText);
      tableModel.fillPage(page);
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    try { // 欧盘胜平负
      String rawText = xpath.selN("//table[@bgcolor='#DDDDDD']").get(2).toString();
      TableModel tableModel = new TableModel(TableModel.TableType.EUROPE, rawText);
      tableModel.fillPage(page);
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

    System.out.println("(Score Odd) => " + matchID);
    // System.out.println(page.getResultItems().getAll());
  }
}

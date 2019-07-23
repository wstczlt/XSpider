package com.test.spider.consumer;

import org.seimicrawler.xpath.JXDocument;

import com.test.spider.SpiderUtils;
import com.test.spider.model.TableModel;
import com.test.spider.model.UrlType;

import us.codecraft.webmagic.Page;

// => http://score.nowscore.com/odds/3in1Odds.aspx?companyid=3&id=1662653
// 皇冠指数集合
public class CornerOddConsumer implements Consumer {

  // 角球大小指数: 初盘、即时盘、中场盘、00~100分钟盘口
  @Override
  public void accept(Page page) {
    UrlType urlType = UrlType.formUrl(page.getUrl().toString());
    if (urlType != UrlType.CORNER_ODD) {
      return;
    }
    final JXDocument xpath = JXDocument.create(page.getRawText());
    final int matchID = SpiderUtils.extractMatchID(page);
    if (matchID <= 0) {
      return;
    }

    try { // 角球
      String rawText = xpath.selN("//table[@bgcolor='#DDDDDD']").get(1).toString();
      TableModel tableModel = new TableModel(TableModel.TableType.CORNER, rawText);
      tableModel.fillPage(page);
    } catch (Throwable e) {
      SpiderUtils.log(e);
    }

//    System.out.println("(Corner) => " + matchID);
    // System.out.println(page.getResultItems().getAll());
  }
}

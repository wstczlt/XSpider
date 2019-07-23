package com.test.spider.consumer;

import java.util.List;

import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import com.test.spider.SpiderUtils;
import com.test.spider.model.UrlType;

import us.codecraft.webmagic.Page;

public class ScoreConsumer implements Consumer {

  @Override
  public void accept(Page page) {
    UrlType urlType = UrlType.formUrl(page.getUrl().toString());
    if (urlType != UrlType.SCORE) {
      return;
    }
    final JXDocument xpath = JXDocument.create(page.getRawText());
    final int matchID = SpiderUtils.extractMatchID(page);
    if (matchID <= 0) {
      return;
    }

    List<JXNode> trNodes = xpath.selN("//tr");
    JXNode bet365Node = null;
    for (JXNode jxNode : trNodes) {
      if (jxNode.toString().contains(">Bet365 <")) {
        bet365Node = jxNode;
        break;
      }
    }
    if (bet365Node == null) {
      return;
    }
    List<JXNode> tdNodes = bet365Node.sel("//td/allText()");
    float originalScoreOddOfVictory = SpiderUtils.valueOfFloat(tdNodes.get(1).toString());
    float originalScoreOdd = SpiderUtils.convertOdd(tdNodes.get(2).toString());
    float originalScoreOddOfDefeat = SpiderUtils.valueOfFloat(tdNodes.get(3).toString());

    float openingScoreOddOfVictory = SpiderUtils.valueOfFloat(tdNodes.get(4).toString());
    float openingScoreOdd = SpiderUtils.convertOdd(tdNodes.get(5).toString());
    float openingScoreOddOfDefeat = SpiderUtils.valueOfFloat(tdNodes.get(6).toString());

    float originalVictoryOdd = SpiderUtils.valueOfFloat(tdNodes.get(7).toString());
    float originalDrewOdd = SpiderUtils.convertOdd(tdNodes.get(8).toString());
    float originalDefeatOdd = SpiderUtils.valueOfFloat(tdNodes.get(9).toString());
//    System.out.println("(Score New) => " + matchID);
  }
}

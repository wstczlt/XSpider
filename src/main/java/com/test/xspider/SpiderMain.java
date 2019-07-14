package com.test.xspider;

import java.util.ArrayList;
import java.util.List;

import com.test.xspider.consumer.ScoreOddConsumer;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class SpiderMain {

  public static void main(String[] args) {
    // build urls
    final long startMatchID = 1662660;
    final long endMatchID = 1662670;
    final List<String> matchUrls = new UrlProducer(startMatchID, endMatchID).buildUrls();

    // build consumers
    final List<PageConsumer> consumers = new ArrayList<>();
    // consumers.add(new DetailConsumer());
    // consumers.add(new AnalysisConsumer());
    consumers.add(new ScoreOddConsumer());
    PageProcessor processor = new MatchProcessor(consumers);

    // build spider
    final Spider spider = Spider.create(processor).thread(1);
    for (String matchUrl : matchUrls) {
      spider.addUrl(matchUrl);
    }
    spider.run();
  }
}

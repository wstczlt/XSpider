package com.test.xspider;

import com.test.xspider.consumer.DetailPageConsumer;

import java.util.ArrayList;
import java.util.List;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class SpiderMain {

  public static void main(String[] args) {
    // build urls
    final long startMatchID = 1662650;
    final long endMatchID = 1662660;
    final List<String> matchUrls = new UrlProducer(startMatchID, endMatchID).buildUrls();

    // build consumers
    final List<PageConsumer> consumers = new ArrayList<>();
    consumers.add(new DetailPageConsumer());
    PageProcessor processor = new MatchProcessor(consumers);

    // build spider
    final Spider spider = Spider.create(processor).thread(1);
    for (String matchUrl : matchUrls) {
      spider.addUrl(matchUrl);
    }
    spider.run();
  }
}

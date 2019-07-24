package com.test.spider;

import static com.test.spider.SpiderConfig.TOTAL_THREAD_COUNT;

import java.util.ArrayList;
import java.util.List;

import com.test.spider.consumer.AnalysisConsumer;
import com.test.spider.consumer.Consumer;
import com.test.spider.consumer.CornerOddConsumer;
import com.test.spider.consumer.DetailConsumer;
import com.test.spider.consumer.ScoreConsumer;
import com.test.spider.consumer.ScoreOddConsumer;
import com.test.spider.model.UrlType;
import com.test.spider.pipline.SQLitePipeline;
import com.test.spider.tools.SpiderDownloader;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

public class FootballSpider {

  private final List<Integer> mMatchIds;

  public FootballSpider(List<Integer> matchIds) {
    mMatchIds = matchIds;
  }

  public void run() {
    build().run();
  }

  private Spider build() {
    // build consumers
    final List<Consumer> consumers = new ArrayList<>();
    consumers.add(new DetailConsumer());
    consumers.add(new ScoreConsumer());
    consumers.add(new ScoreOddConsumer());
    consumers.add(new AnalysisConsumer());
    consumers.add(new CornerOddConsumer());
    PageProcessor processor = new SpiderProcessor(consumers);

    // build spider
    final Spider spider = Spider.create(processor)
        .setScheduler(new PriorityScheduler())
        .setDownloader(new SpiderDownloader().setProxyProvider(new SpiderProxy()))
        .thread(TOTAL_THREAD_COUNT);
    // build urls
    final List<Request> requests = collectRequests(mMatchIds);
    for (Request request : requests) {
      spider.addRequest(request);
    }
    spider.addPipeline(new SQLitePipeline());

    return spider;
  }


  private static List<Request> collectRequests(List<Integer> matchIds) {
    List<Request> requests = new ArrayList<>();
    for (int matchID : matchIds) {
      requests.add(new Request(UrlType.DETAIL.buildUrl(matchID)).setPriority(matchID));
    }

    return requests;
  }
}

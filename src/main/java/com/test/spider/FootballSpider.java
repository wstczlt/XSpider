package com.test.spider;

import static com.test.spider.SpiderConfig.TOTAL_THREAD_COUNT;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.test.spider.consumer.AnalysisConsumer;
import com.test.spider.consumer.Consumer;
import com.test.spider.consumer.CornerOddConsumer;
import com.test.spider.consumer.DetailConsumer;
import com.test.spider.consumer.ScoreConsumer;
import com.test.spider.consumer.ScoreOddConsumer;
import com.test.spider.model.UrlType;
import com.test.spider.pipline.SQLitePipeline;
import com.test.spider.tools.Logger;
import com.test.spider.tools.SpiderDownloader;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

public class FootballSpider {

  private final List<Integer> mMatchIds;
  private final Logger mLogger;
  private final Predicate<Page> mPredicate;

  public FootballSpider(List<Integer> matchIds, Logger logger, Predicate<Page> predicate) {
    mMatchIds = matchIds;
    mLogger = logger;
    mPredicate = predicate;
  }

  public void run() {
    build().run();
  }

  private Spider build() {
    // build consumers
    final List<Consumer> consumers = new ArrayList<>();
    consumers.add(new DetailConsumer(mLogger, mPredicate));
    consumers.add(new ScoreConsumer(mLogger));
    consumers.add(new ScoreOddConsumer(mLogger));
    consumers.add(new AnalysisConsumer(mLogger));
    consumers.add(new CornerOddConsumer(mLogger));
    PageProcessor processor = new SpiderProcessor(consumers);

    // build spider
    final Spider spider = Spider.create(processor)
        .setScheduler(new PriorityScheduler())
        // .setDownloader(new SpiderDownloader())
        .setDownloader(new SpiderDownloader().setProxyProvider(new SpiderProxy()))
        .thread(TOTAL_THREAD_COUNT);
    // build urls
    final List<Request> requests = collectRequests(mMatchIds);
    for (Request request : requests) {
      spider.addRequest(request);
    }
    spider.addPipeline(new SQLitePipeline(mLogger));

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

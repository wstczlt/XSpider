package com.test.deprecated.tools;

import static com.test.deprecated.SpiderConfig.TOTAL_THREAD_COUNT;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.test.deprecated.consumer.AnalysisConsumer;
import com.test.deprecated.consumer.Consumer;
import com.test.deprecated.consumer.CornerOddConsumer;
import com.test.deprecated.consumer.DetailConsumer;
import com.test.deprecated.consumer.ScoreConsumer;
import com.test.deprecated.consumer.ScoreOddConsumer;
import com.test.deprecated.model.UrlType;
import com.test.deprecated.pipline.SQLitePipeline;
import com.test.tools.Logger;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

public class SpiderBuilder {

  private final List<Integer> mMatchIds;
  private final Logger mLogger;
  private final Predicate<Page> mPredicate;

  public SpiderBuilder(List<Integer> matchIds, Logger logger, Predicate<Page> predicate) {
    mMatchIds = matchIds;
    mLogger = logger;
    mPredicate = predicate;
  }

  public Spider build() {
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

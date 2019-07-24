package com.test.spider;

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

public class SpiderMain {

  public static void main(String[] args) {
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
        .thread(SpiderConfig.SPIDER_THREAD_COUNT);
    // build urls
    final List<Request> requests = collectRequests();
    for (Request request : requests) {
      spider.addRequest(request);
    }
    spider.addPipeline(new SQLitePipeline());
    // spider.addPipeline(new ConsolePipeline());
    spider.run();
  }

  private static List<Request> collectRequests() {
    List<Request> requests = new ArrayList<>();
    for (int matchID =
        SpiderConfig.MATCH_ID_START; matchID < SpiderConfig.MATCH_ID_END; matchID++) {
      // detail的处理优先级最低，这样可以保证每个matchID的几个关联页面都能被尽快处理
      // 从大到小执行(最新 => 最旧)
      requests.add(new Request(UrlType.SCORE_ODD.buildUrl(matchID)).setPriority(matchID));
    }

    return requests;
  }

}

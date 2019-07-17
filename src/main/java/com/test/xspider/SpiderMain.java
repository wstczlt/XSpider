package com.test.xspider;

import java.util.ArrayList;
import java.util.List;

import com.test.xspider.consumer.AnalysisConsumer;
import com.test.xspider.consumer.CornerOddConsumer;
import com.test.xspider.consumer.DetailConsumer;
import com.test.xspider.consumer.ScoreOddConsumer;
import com.test.xspider.model.UrlType;
import com.test.xspider.pipline.SQLitePipeline;
import com.test.xspider.utils.Consumer;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

public class SpiderMain {

  public static void main(String[] args) {
    // build consumers
    final List<Consumer> consumers = new ArrayList<>();
    consumers.add(new DetailConsumer());
    consumers.add(new AnalysisConsumer());
    consumers.add(new ScoreOddConsumer());
    consumers.add(new CornerOddConsumer());
    PageProcessor processor = new XSpiderProcessor(consumers);

    final HttpClientDownloader downloader = new HttpClientDownloader() {

      @Override
      protected void onError(Request request) {
        System.out.println("Failed: => " + request);
        super.onError(request);
      }
    };
    downloader.setProxyProvider(new XSpiderProxyProvider());
    // build spider
    final Spider spider = Spider.create(processor)
        .setScheduler(new PriorityScheduler())
        .setDownloader(downloader)
        .thread(XSpiderConfig.SPIDER_THREAD_COUNT);
    // build urls
    final List<Request> requests = collectRequests();
    for (Request request : requests) {
      spider.addRequest(request);
    }
    spider.addPipeline(new SQLitePipeline());
    spider.run();
  }

  private static List<Request> collectRequests() {
    List<Request> requests = new ArrayList<>();
    for (int matchID =
        XSpiderConfig.MATCH_ID_START; matchID < XSpiderConfig.MATCH_ID_END; matchID++) {
      // detail的处理优先级最低，这样可以保证每个matchID的几个关联页面都能被尽快处理
      // 从大到小执行(最新 => 最旧)
      requests.add(new Request(UrlType.DETAIL.buildUrl(matchID)).setPriority(matchID));
    }

    return requests;
  }

}

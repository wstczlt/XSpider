package com.test.spider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

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
    List<Integer> matchIds = collectStaticMatchIds();
    List<Request> requests = new ArrayList<>();
    for (int matchID : matchIds) {
      requests.add(new Request(UrlType.DETAIL.buildUrl(matchID)).setPriority(matchID));
    }

    return requests;
  }

  private static List<Integer> collectStaticMatchIds() {
    List<Integer> matchIds = new ArrayList<>();
    for (int matchID =
        SpiderConfig.MATCH_ID_END; matchID > SpiderConfig.MATCH_ID_START; matchID--) {
      // 从大到小执行(最新 => 最旧)
      matchIds.add(matchID);
    }

    return matchIds;
  }

}

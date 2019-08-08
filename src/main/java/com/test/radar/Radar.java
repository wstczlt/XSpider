package com.test.radar;

import static com.test.tools.Utils.valueOfLong;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.test.Config;
import com.test.Keys;
import com.test.dszuqiu.DsJobBuilder;
import com.test.dszuqiu.DsJobFactory;
import com.test.entity.Estimation;
import com.test.entity.Model;
import com.test.http.HttpEngine;
import com.test.learning.Phoenix;
import com.test.learning.model.OddModel;
import com.test.pipeline.MemoryPipeline;

public class Radar implements Keys {

  private final Model[] mModels;
  private final EstimationConsumer[] mConsumers;

  public Radar(Model[] models, EstimationConsumer[] consumers) {
    mModels = models;
    mConsumers = consumers;
  }

  public static void main(String[] args) throws Exception {
    new Radar(
        new Model[] {new OddModel(-1)},
        new EstimationConsumer[] {new ConsoleConsumer()}).run(1);
  }

  public void run(int loop) throws Exception {
    long sleep = 0;
    while (loop-- > 0) {
      try {
        long start = System.currentTimeMillis();
        loopMain();
        sleep = Config.MIN_ONE_LOOP - (System.currentTimeMillis() - start);
      } catch (Throwable e) {
        e.printStackTrace();
      } finally {
        if (sleep > 0) Thread.sleep(sleep);
      }
    }
  }

  private void loopMain() throws Exception {
    System.out.println(
        "\n\n\n\n\n\n当前时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));

    // 运行爬虫
    final MemoryPipeline pipeline = new MemoryPipeline();
    DsJobFactory factory = new DsJobFactory(new DsJobBuilder());
    HttpEngine dragon = new HttpEngine(factory.build(), pipeline);
    dragon.start();

    List<Map<String, Object>> matches = pipeline.getMaps();
    System.out.println("进行中的比赛场次: " + matches.size());
    // 运行AI
    for (Model model : mModels) {
      System.out.println("-----------------模型: " + model.name() + "--------------------");
      loopOne(model, matches);
    }
  }

  private void loopOne(Model model, List<Map<String, Object>> matches) throws Exception {
    matches = matches.stream()
        .sorted(
            (o1, o2) -> (int) (valueOfLong(o2.get(MATCH_TIME)) - valueOfLong(o1.get(MATCH_TIME))))
        .collect(Collectors.toList());
    if (matches.isEmpty()) {
      return;
    }
    boolean trick = false;
    if (matches.size() == 1) { // 单条无法训练, 做一个trick
      matches.add(matches.get(0));
      trick = true;
    }

    List<Estimation> results = Phoenix.runEst(model, matches);

    for (int i = 0; i < results.size(); i++) {
      if (trick && i == 1) { // trick的数据不要
        continue;
      }
      for (EstimationConsumer consumer : mConsumers) {
        consumer.accept(matches.get(i), model, results.get(i));
      }
    }
  }
}

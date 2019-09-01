package com.test.win007;

import static com.test.Config.SPIDER_THREAD_COUNT;

import java.util.List;

import com.test.http.HttpEngine;
import com.test.http.ListJobFactory;
import com.test.http.RangeJobFactory;
import com.test.pipeline.DbPipeline;

public class Win007Spider {

  public static void main(String[] args) throws Exception {
    runSt(1600000, 1639600);
  }

  public static void run(List<Integer> matchIDs) throws Exception {
    ListJobFactory factory = new ListJobFactory(matchIDs, new Win007JobBuilder());
    HttpEngine dragon = new HttpEngine(factory.build(), new DbPipeline(), SPIDER_THREAD_COUNT);

    dragon.start();
  }

  public static List<Integer> runRt() throws Exception {
    Win007JobFactory factory = new Win007JobFactory(new Win007JobBuilder());
    HttpEngine dragon = new HttpEngine(factory.build(), new DbPipeline(), SPIDER_THREAD_COUNT);

    dragon.start();

    return factory.getMatchIDs();
  }


  public static void runSt(int matchStartID, int matchEndID) throws Exception {
    RangeJobFactory factory = new RangeJobFactory(new Win007JobBuilder(), matchStartID, matchEndID);
    HttpEngine dragon = new HttpEngine(factory.build(), new DbPipeline(), SPIDER_THREAD_COUNT);

    dragon.start();
  }

}

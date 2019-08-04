package com.test.dszuqiu;

import com.test.http.HttpEngine;
import com.test.http.RangeJobFactory;
import com.test.pipeline.FilePipeline;

public class DsSpider {

  public static void main(String[] args) throws Exception {

    runSt(446859, 446860);
  }


  public static void runSt(int matchStartID, int matchEndID) throws Exception {
    RangeJobFactory factory = new RangeJobFactory(new DsJobBuilder(), matchStartID, matchEndID);
    HttpEngine dragon = new HttpEngine(factory.build(), new FilePipeline("dszuqiu"));

    dragon.start();
  }
}

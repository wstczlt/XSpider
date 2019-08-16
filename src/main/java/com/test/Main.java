package com.test;

import com.test.manual.RuleFactory;
import com.test.manual.RuleType;

public class Main {

  public static void main(String[] args) throws Exception {

    // DsSpider.runSt(240000, 588848);

    // DsHelper.read("/Users/Jesse/Desktop/odd"); // ~511531


    // QueryHelper.queryLeagues();

    // Radar.main(null);

    new RuleFactory(RuleType.SCORE).build();
    new RuleFactory(RuleType.BALL).build();
  }

}

package com.test;

import com.test.manual.HistoryRadar;

public class Main {

  public static void main(String[] args) throws Exception {

    Config.class.getSimpleName();

    // DsSpider.runSt(240000, 588848);

    // DsHelper.read("/Users/Jesse/Desktop/odd"); // ~511531


    // QueryHelper.queryLeagues();

    // Radar.main(null);

    // new RuleFactory(RuleType.SCORE).build();
    // new RuleFactory(RuleType.BALL).build();

    new HistoryRadar().run(1);

    // HistoryTester.testOnLast1Weeks();
    // HistoryTester.testOnLastDay();

    // HistoryTester.testOnNewHistory(7);

    // HistoryTester.testOnRandomHistory();

    // HistoryTester.testOnNewHistory(1);
  }

}

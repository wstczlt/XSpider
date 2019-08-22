package com.test;

import com.test.manual.HistoryRadar;
import com.test.manual.HistoryTester;

public class Main {

  public static void main(String[] args) throws Exception {

    Config.class.getSimpleName();

    // DsSpider.runSt(240000, 588848);

    // DsHelper.read("/Users/Jesse/Desktop/odd"); // ~511531


    // QueryHelper.queryLeagues();

    // Radar.main(null);
    //
    // new RuleFactory(RuleType.SCORE).build();
    // new RuleFactory(RuleType.BALL).build();


    System.out.println("\n昨日数据: ");
    HistoryTester.testOnLastDay();

    System.out.println("\n近3日数据: ");
    HistoryTester.testOnLast3Day();

    System.out.println("\n上周数据: ");
    HistoryTester.testOnLast1Weeks();

    System.out.println("\n上两周数据: ");
    HistoryTester.testOnLast2Weeks();

    System.out.println("\n随机数据A: ");
    HistoryTester.testOnRandomHistory();

    System.out.println("\n随机数据B: ");
    HistoryTester.testOnRandomHistory();

    System.out.println("\n随机数据C: ");
    HistoryTester.testOnRandomHistory();


    // HistoryTester.testOnNewHistory(3);
    // HistoryTester.testOnNewHistory(14);

    // HistoryTester.testDisplay();
    // HistoryTester.testHistoryDisplay(1);

//    new HistoryRadar().run(1000);
  }

}

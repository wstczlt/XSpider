package com.test;

import com.test.manual.HistoryRadar;

public class Main {

  public static void main(String[] args) throws Exception {

    Config.class.getSimpleName();

    // new RuleFactory(RuleType.SCORE).build();
    // new RuleFactory(RuleType.BALL).build();

    //
    // System.out.println("\n昨日数据: ");
    // HistoryTester.testAndEval(0, 1);
    //
    // System.out.println("\n近3日数据: ");
    // HistoryTester.testAndEval(0, 3);
    //
    // System.out.println("\n上周数据: ");
    // HistoryTester.testAndEval(0, 7);
    //
    // System.out.println("\n上两周数据: ");
    // HistoryTester.testAndEval(0, 14);
    //
    // System.out.println("\n上月数据: ");
    // HistoryTester.testAndEval(0, 28);
    //
    // for (int i = 0; i < 10; i++) {
    // System.out.println("\n随机数据(" + i + "): ");
    // HistoryTester.testAndEval();
    // }


    // HistoryTester.fetchAndDisplay(30);

    new HistoryRadar().run(10000);
  }

}

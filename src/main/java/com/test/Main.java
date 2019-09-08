package com.test;

import com.test.manual.HistoryRadar;

public class Main {

  public static void main(String[] args) throws Exception {

    Config.class.getSimpleName();

    // new RuleFactory(RuleType.SCORE).build();
    // new RuleFactory(RuleType.BALL).build();

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
    //
    // // 正向
    // HistoryTester.testAndEval(175, 28);
    // HistoryTester.testAndEval(231, 28);
    // HistoryTester.testAndEval(490, 28);
    //
    // // 负向
    // HistoryTester.testAndEval(245, 28);
    // HistoryTester.testAndEval(133, 28);

    // 散点分布测试
    // List<Float> list = new ArrayList<>();
    // for (int i = 10; i <= 100; i++) {
    // System.out.println(i);
    // float rate = HistoryTester.testAndEval(i * 7, 7);
    // if (rate > 0) {
    // list.add(rate);
    // }
    // }
    // System.out.println(list);
    // System.out.println(
    // String.format("均值:%.3f, 最小值:%.3f, 最大值:%.3f, 25分位数:%.3f, 50分位数:%.3f, 75分位数:%.3f, 标准差:%.3f",
    // Utils.calMean(list),
    // Utils.calMin(list),
    // Utils.calMax(list),
    // Utils.calPercentile(list, 0.25f),
    // Utils.calPercentile(list, 0.5f),
    // Utils.calPercentile(list, 0.75f),
    // Utils.calStd(list)));

    // HistoryTester.fetchAndDisplay(10);



    new HistoryRadar().run(10000);
  }
}

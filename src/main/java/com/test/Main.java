package com.test;

import com.test.manual.RuleHelper;
import com.test.manual.RuleType;

public class Main {

  public static void main(String[] args) throws Exception {

    // DsSpider.runSt(240000, 588848);

    // DsHelper.read("/Users/Jesse/Desktop/odd"); // ~511531


    // QueryHelper.queryLeagues();

    // Radar.main(null);

    new RuleHelper(RuleType.SCORE).calRules();
    new RuleHelper(RuleType.BALL).calRules();
  }

}

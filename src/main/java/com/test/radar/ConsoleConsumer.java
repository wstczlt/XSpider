package com.test.radar;

import org.apache.http.util.TextUtils;

import com.test.entity.Estimation;
import com.test.entity.Match;
import com.test.entity.Model;

public class ConsoleConsumer implements EstimationConsumer {

  @Override
  public void accept(Match match, Model model, Estimation est) {
//    if (model instanceof OddModel45) {
//      displayOddHalf(match, model, est);
//    } else if (model instanceof BallModel45) {
//      displayBallHalf(match, model, est);
//    }
  }

  private void displayBallHalf(Match match, Model model, Estimation est) {
    if (est.mProbability < model.bestThreshold()) { // 只展示高概率比赛
      return;
    }
    String hostName = match.mHostName;
    String customName = match.mCustomName;
    String league = !TextUtils.isEmpty(match.mLeague) ? match.mLeague : "野鸡";

    System.out.println(
        String.format("%d', [%s], %s VS %s", match.mTimeMin, league, hostName, customName));
    System.out.println(
        String.format("     中场比分: %d : %d", match.mMiddleHostScore, match.mMiddleCustomScore));
    System.out.println(String.format("     当前比分: %d : %d", match.mHostScore, match.mCustomScore));
    System.out.println(String.format("     盘口: %s， 概率: %.2f[历史命中率: %s]",
        match.mMiddleBigOdd + "[" + (est.mValue == 0 ? "小" : "大") + "]",
        est.mProbability,
        ((int) (est.mProbability * 100 + 5)) + "%"));

    System.out.println("\n\n");
  }

  private void displayOddHalf(Match match, Model model, Estimation est) {
    if (est.mProbability < model.bestThreshold()) { // 只展示高概率比赛
      return;
    }
    // int matchID = match.mMatchID;
    String hostName = match.mHostName;
    String customName = match.mCustomName;
    String league = !TextUtils.isEmpty(match.mLeague) ? match.mLeague : "野鸡";

    System.out.println("\n\n");
    System.out.println(
        String.format("%d', [%s], %s VS %s", match.mTimeMin, league, hostName, customName));
    System.out.println(
        String.format("     中场比分: %d : %d", match.mMiddleHostScore, match.mMiddleCustomScore));
    System.out.println(String.format("     当前比分: %d : %d", match.mHostScore, match.mCustomScore));
    System.out.println(String.format("     盘口: %s， 概率: %.2f[历史命中率: %s]",
        match.mMiddleScoreOdd + "[" + (est.mValue == 0 ? "客" : "主") + "]",
        est.mProbability,
        ((int) (est.mProbability * 100 + 5)) + "%"));
  }
}

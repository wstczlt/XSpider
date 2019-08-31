package com.test.bot;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;
import static com.test.tools.Utils.valueOfLong;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;

import com.test.Config;
import com.test.Keys;
import com.test.entity.Estimation;
import com.test.manual.Rule;
import com.test.manual.RuleType;
import com.test.tools.Utils;

public class BotConsumer implements Consumer<Estimation>, Keys {

  private static final String PATH = "bot/wechat.dat";

  @Override
  public void accept(Estimation est) {
    if (!(est.mModel instanceof Rule)) {
      return;
    }

    try {
      send(buildText(est));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void send(String text) throws Exception {
    File dat = new File(PATH);
    // 等待删除
    while (true) {
      if (dat.isFile()) {
        Config.LOGGER.log("WAITING...");
        Thread.sleep(200);
        continue;
      }
      break;
    }
    // 写入文件
    FileUtils.writeStringToFile(dat, text, "utf-8");
  }

  private static String buildText(Estimation est) {
    final StringBuilder sb = new StringBuilder();
    final Rule rule = (Rule) est.mModel;
    final Map<String, Object> match = est.mMatch;
    String hostName = (String) match.get(HOST_NAME);
    String customName = (String) match.get(CUSTOM_NAME);
    String league = (String) match.get(LEAGUE);
    String matchID = match.get(MATCH_ID) + "";

    int timeMin = rule.mTimeMin;
    int nowMin = Utils.valueOfInt(match.get(TIME_MIN));
    long matchTime = valueOfLong(match.get(MATCH_TIME));
    int matchStatus = valueOfInt(match.get(MATCH_STATUS));
    final int hostScore = valueOfInt(match.get(HOST_SCORE));
    final int customScore = valueOfInt(match.get(CUSTOM_SCORE));

    final String timePrefix = "min" + timeMin + "_";
    final String nowTimePrefix = "min" + nowMin + "_";
    int minHostScore = valueOfInt(match.get(timePrefix + "hostScore"));
    int minCustomScore = valueOfInt(match.get(timePrefix + "customScore"));
    float minScoreOdd = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOdd"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD));
    float minScoreOddVictory = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOddOfVictory"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_VICTORY));
    float minScoreOddDefeat = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "scoreOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_DEFEAT));
    float minBallOdd = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOdd"))
        : valueOfFloat(match.get(OPENING_BIG_ODD));
    float minBallOddVictory = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOddOfVictory"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_VICTORY));
    float minBallOddDefeat = timeMin > 0
        ? valueOfFloat(match.get(timePrefix + "bigOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_DEFEAT));


    float nowScoreOdd = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "scoreOdd"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD));
    float nowScoreOddVictory = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "scoreOddOfVictory"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_VICTORY));
    float nowScoreOddDefeat = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "scoreOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_SCORE_ODD_OF_DEFEAT));
    float nowBallOdd = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "bigOdd"))
        : valueOfFloat(match.get(OPENING_BIG_ODD));
    float nowBallOddVictory = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "bigOddOfVictory"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_VICTORY));
    float nowBallOddDefeat = nowMin > 0
        ? valueOfFloat(match.get(nowTimePrefix + "bigOddOfDefeat"))
        : valueOfFloat(match.get(OPENING_BIG_ODD_OF_DEFEAT));

    String matchTimeStr =
        new SimpleDateFormat("yyyy-MM-dd HH:mm").format(matchTime - 8 * 3600 * 1000);

    sb.append(String.format("ID [%s]\n", matchID));
    sb.append(String.format("%s\n", matchTimeStr));
    sb.append(String.format("[%s]\n", league));
    sb.append(String.format("%s VS %s\n", hostName, customName));
    sb.append("\n");


    sb.append(String.format("当前时刻: %d' [%d : %d]\n", nowMin, hostScore, customScore));
    sb.append(String.format("预测时刻: %d' [%d : %d]\n", timeMin, minHostScore, minCustomScore));
    sb.append("\n");

    sb.append("购买建议:\n");
    sb.append(String.format("%s,  赔率: %.2f\n",
        rule.mType == RuleType.SCORE
            ? "让球" + (minScoreOdd + "[" + (est.mValue == 0 ? "主" : "客") + "]")
            : "大小" + (minBallOdd + "[" + (est.mValue == 0 ? "大" : "小") + "]"),
        rule.mType == RuleType.SCORE
            ? (est.mValue == 0 ? minScoreOddVictory : minScoreOddDefeat)
            : (est.mValue == 0 ? minBallOddVictory : minBallOddDefeat)));

    sb.append(String.format("预计盈利率: %.2f\n", est.mProfitRate));
    return sb.toString();
  }
}

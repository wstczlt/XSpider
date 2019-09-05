package com.test.bot;

import static com.test.tools.Utils.valueOfFloat;
import static com.test.tools.Utils.valueOfInt;
import static com.test.tools.Utils.valueOfLong;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.io.FileUtils;
import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.test.Config;
import com.test.Keys;
import com.test.entity.Estimation;
import com.test.http.HttpUtils;
import com.test.manual.HistoryConsumer;
import com.test.manual.Rule;
import com.test.manual.RuleType;
import com.test.tools.Utils;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class BotConsumer implements Consumer<Estimation>, Keys {

  private static final String UID_WSTCZLT = "wstczlt";
  private static final String UID_SAOHUO = "11339123190@chatroom"; // 扫货
  private static final String UID_SANRENYOU = "14192966472@chatroom"; // 三人游
  private static final String UID_LIWEIMIN = "wxid_61qtomt6qgb622"; // 李维民

  private static final String PATH = "bot/wechat.dat";
  private static final String LOG_PATH = "bot/r.log";
  private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Estimation.class,
      (JsonDeserializer<Estimation>) (json, typeOfT, context) -> {
        final JsonObject res = (JsonObject) json;
        JsonObject ruleJson = (JsonObject) res.remove("mModel");
        Gson newGson = new Gson();
        Estimation estimation = newGson.fromJson(res, Estimation.class);
        Rule rule = newGson.fromJson(ruleJson, Rule.class);
        return new Estimation(rule, estimation.mMatch, estimation.mValue, estimation.mProb0,
            estimation.mProb1, estimation.mProb2, estimation.mProfitRate);
      }).create();


  public static void main(String[] args) throws Exception {
    List<Estimation> estimations = new BotConsumer().readLog();
    final HistoryConsumer consumer = new HistoryConsumer();
    // final BotConsumer consumer = new BotConsumer();
    estimations.forEach(consumer);

     new BotConsumer().sendByMac("@焦功进 @朱蓝天 Test: PM 威武雄壮XXX");
  }

  @Override
  public void accept(Estimation est) {
    if (!(est.mModel instanceof Rule)) {
      return;
    }

    try {
      List<Estimation> list = readLog();

      float newMatchID = valueOfFloat(est.mMatch.get(MATCH_ID));
      String newType = ((Rule) est.mModel).mType.name();

      for (Estimation item : list) {
        float matchID = valueOfFloat(item.mMatch.get(MATCH_ID));
        String type = ((Rule) item.mModel).mType.name();

        // 相同比赛，相同推荐类型
        if (newMatchID == matchID && newType.equals(type)) {
          return;
        }
      }
      // 保存结果
      list.add(est);
      saveLog(list);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      final String text = buildText(est);
      sendByMac(text);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public List<Estimation> readLog() throws Exception {
    final List<Estimation> list = new ArrayList<>();
    File log = new File(LOG_PATH);
    if (!log.isFile()) {
      return list;
    }
    String json = FileUtils.readFileToString(log, "utf-8").trim();
    if (TextUtils.isEmpty(json)) {
      return list;
    }

    if (JSONObject.parse(json) == null) {
      return list;
    }

    return GSON.fromJson(json, new TypeToken<ArrayList<Estimation>>() {}.getType());
  }

  public static void saveLog(List<Estimation> list) throws Exception {
    String json = new Gson().toJson(list);
    FileUtils.writeStringToFile(new File(LOG_PATH), json, "utf-8");
  }

  private void sendByPython(String text) throws Exception {
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

  private void sendByMac(String text) throws Exception {
    final String[] list = new String[] {UID_WSTCZLT, UID_SANRENYOU};
    for (String uid : list) {
      OkHttpClient client = HttpUtils.buildHttpClient();
      FormBody body = new FormBody.Builder()
          .add("content", text)
          .add("userId", uid)
          .add("srvId", "0")
          .build();
      Request request =
          new Request.Builder().url("http://127.0.0.1:52700/wechat-plugin/send-message")
              .post(body).build();
      client.newCall(request).execute();
    }
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

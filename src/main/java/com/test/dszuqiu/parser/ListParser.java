package com.test.dszuqiu.parser;

import static com.test.tools.Utils.valueOfInt;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.test.Config;
import com.test.tools.Utils;

public class ListParser {

  public static final Predicate<JSONObject> TIME_FILTER = jsonObject -> {
    String statusString = jsonObject.getString("status");
    int timeMin = Utils.timeMin(statusString);

    return !Objects.equals(statusString, "全")
        && !Objects.equals(statusString, "未")
        && timeMin >= 0 && timeMin <= 80;
  };

  public static final Predicate<JSONObject> RACE_FILTER = jsonObject -> {
    JSONObject league = jsonObject.getJSONObject("league");
    if (league == null) {
      return false;
    }
    int zc = league.getInteger("zc");
    int jc = league.getInteger("jc");
    int bd = league.getInteger("bd");

    return zc + jc + bd > 0; // 有一个不是0就行
  };

  private final String mRawText;
  private final Predicate<JSONObject> mPredicate;

  public ListParser(String rawText, Predicate<JSONObject> predicate) {
    mRawText = rawText;
    mPredicate = predicate;
  }

  public List<Integer> doParse() {
    final JSONObject json = JSON.parseObject(mRawText);
    if (json == null) {
      return Collections.emptyList();
    }
    String error = json.getString("error");
    if (!TextUtils.isEmpty(error)) {
      Config.LOGGER.log("调用异常: " + error);
      return Collections.emptyList();
    }
    JSONArray races = json.getJSONArray("races");
    if (races == null || races.isEmpty()) {
      races = json.getJSONArray("rs");
    }
    if (races == null || races.isEmpty()) {
      return Collections.emptyList();
    }

    return races.stream()
        .map(o -> (JSONObject) o)
        // .filter(RACE_FILTER)
        .filter(mPredicate)
        .map(value -> valueOfInt(value.getString("id")))
        .collect(Collectors.toList());
  }
}

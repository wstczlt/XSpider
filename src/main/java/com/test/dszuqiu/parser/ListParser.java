package com.test.dszuqiu.parser;

import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ListParser {

  private final String mRawText;
  private static final Predicate<JSONObject> FILTER = jsonObject -> {
    // 123
    return true;
  };

  public ListParser(String rawText) {
    mRawText = rawText;
  }

  public List<Integer> doParse() {
    final JSONObject json = JSON.parseObject(mRawText);
    if (json == null) {
      return Collections.emptyList();
    }
    JSONArray races = json.getJSONArray("races");
    if (races == null || races.isEmpty()) {
      races =  json.getJSONArray("rs");
    }
    if (races == null || races.isEmpty()) {
      return Collections.emptyList();
    }
    final List<Integer> matchIDs = new ArrayList<>();
    for (int i = 0; i < races.size(); i++) {
      JSONObject race = races.getJSONObject(i);
      if (!FILTER.test(race)) {
        continue;
      }
      matchIDs.add(valueOfInt(race.getString("id")));
    }

    return matchIDs;
  }
}

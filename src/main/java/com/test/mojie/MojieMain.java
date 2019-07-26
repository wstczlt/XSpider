package com.test.mojie;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

public class MojieMain {

  public static void main(String[] args) throws Exception {
    for (int i = 0; i < 100; i++) {
      String filename = "matchs/api%3f" + (i > 0 ? i : "");
      File rawFile = new File(filename);
      if (!rawFile.isFile()) {
        continue;
      }
      String text = FileUtils.readFileToString(rawFile);
      Map<String, Object> jsonMap = (Map<String, Object>) JSON.parse(text);
      JSONArray respArray = (JSONArray) jsonMap.get("resp");
      Map<String, Object> resp = (Map<String, Object>) respArray.get(0);
      JSONArray matchArray = (JSONArray) resp.get("match_results");
      System.out.println(matchArray);
    }

  }
}

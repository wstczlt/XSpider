package com.test.dszuqiu;

import static com.test.tools.Utils.isSkip;
import static com.test.tools.Utils.setSkip;
import static com.test.tools.Utils.valueOfInt;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.Config;
import com.test.dszuqiu.parser.OddParser;
import com.test.dszuqiu.parser.RaceParser;
import com.test.pipeline.DbPipeline;

public class DsHelper {

  private static final String FILENAME = "/Users/Jesse/Desktop/dszuqiu";

  public static void main(String[] args) throws Exception {
    read(FILENAME);
  }

  public static void read(String filepath) throws Exception {
    String[] names = new File(filepath).list();
    if (names == null) {
      return;
    }
    final DbPipeline pipeline = new DbPipeline();
    List<String> sorted = Arrays.stream(names)
        .filter(s -> s.endsWith(".txt"))
        .map(s -> s.replace("_odd.txt", "").replace(".txt", ""))
        .distinct()
        // .filter(s -> valueOfInt(s) < 511531)
        .sorted((o1, o2) -> valueOfInt(o2) - valueOfInt(o1))
        .collect(Collectors.toList());

    for (String name : sorted) {
      final Map<String, String> items = new HashMap<>();

      // 处理Race信息
      File raceFile = new File(filepath, name + ".txt");
      if (raceFile.isFile()) {
        String raceRawText = FileUtils.readFileToString(raceFile, "utf-8");
        JSONObject json = JSON.parseObject(raceRawText);
        String error = RaceParser.isLegalRace(json);
        if (!TextUtils.isEmpty(error)) {
          setSkip(items);
          Config.LOGGER.log(
              String.format("[%s], matchID=%d, Error=%s", "RaceJob", -1, error));
          continue;
        }

        new RaceParser(raceRawText, items).doParse();
      }


      // 处理ODD指数信息
      File oddFile = new File(filepath, name + "_odd.txt");
      if (oddFile.isFile()) {
        String oddRawText = FileUtils.readFileToString(oddFile, "utf-8");
        JSONObject json = JSON.parseObject(oddRawText);
        String error = RaceParser.isLegalOdd(json);
        if (!TextUtils.isEmpty(error)) {
          setSkip(items);
          Config.LOGGER.log(
              String.format("[%s], matchID=%d, Error=%s", "OddJob", -1, error));
          continue;
        }

        new OddParser(oddRawText, items).doParse();
      }



      // 写入Database
      if (!isSkip(items)) {
        pipeline.process(items);
        // sKeys.addAll(items.keySet());
        // sInt++;
        //
        // if (sInt >= 1000) {
        // sKeys.stream().distinct().sorted()
        // .filter(s -> !s.equalsIgnoreCase("matchID"))
        // .forEach(s -> System.out.print(s + ","));
        // return;
        // }
      }
    }
  }

  private static Set<String> sKeys = new HashSet<>();
  private static int sInt = 0;
}

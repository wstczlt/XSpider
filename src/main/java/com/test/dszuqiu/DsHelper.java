package com.test.dszuqiu;

import static com.test.tools.Utils.isSkip;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.test.pipeline.DbPipeline;

public class DsHelper {

  private static final String FILENAME = "dszuqiu";

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
        .filter(s -> !s.endsWith("_odd.txt"))
        .sorted((o1, o2) -> {
          o1 = o1.replace(".txt", "");
          o2 = o2.replace(".txt", "");
          return o2.compareTo(o1);
        }).collect(Collectors.toList());

    for (String name : sorted) {
      final Map<String, String> items = new HashMap<>();

      // 处理Race信息
      File raceFile = new File(filepath, name);
      String raceRawText = FileUtils.readFileToString(raceFile, "utf-8");
      new RaceParser(raceRawText, items).doParse();

      // 处理ODD指数信息
      File oddFile = new File(filepath, name.replace(".txt", "") + "_odd.txt");
      if (oddFile.isFile()) {
        String oddRawText = FileUtils.readFileToString(oddFile, "utf-8");
        new OddParser(oddRawText, items).doParse();
      }

      // 写入Database
      if (!isSkip(items)) {
        pipeline.process(items);
      }
    }
  }
}
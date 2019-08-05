package com.test.dszuqiu;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.test.pipeline.EmptyPipeline;
import com.test.pipeline.HttpPipeline;

public class RaceReader {

  private static final String FILENAME = "dszuqiu";

  public static void main(String[] args) throws Exception {
    read();
  }


  public static void read() throws Exception {
    final HttpPipeline pipeline = new EmptyPipeline();
    File[] files = new File(FILENAME).listFiles();
    for (File f : files) {
      String rawText = FileUtils.readFileToString(f, "utf-8");
      Map<String, String> items = new HashMap<>();
      new RaceParser(rawText, items).doParse();
//      pipeline.process(items);
    }
  }
}

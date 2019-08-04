package com.test.pipeline;

import java.util.Map;

public class ConsolePipeline implements HttpPipeline {

  @Override
  public void process(Map<String, String> items) {
    System.out.println(items);
  }
}

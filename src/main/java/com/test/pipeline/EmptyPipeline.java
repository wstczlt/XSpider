package com.test.pipeline;

import java.util.Map;

public class EmptyPipeline implements HttpPipeline {

  @Override
  public void process(Map<String, String> items) {}
}

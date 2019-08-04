package com.test.pipeline;

import java.util.Map;

import com.test.Keys;

public interface HttpPipeline extends Keys {

  void process(Map<String, String> items);
}

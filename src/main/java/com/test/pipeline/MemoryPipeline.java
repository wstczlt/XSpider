package com.test.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryPipeline implements HttpPipeline {

  private final List<Map<String, Object>> mMaps = new ArrayList<>();

  @Override
  public synchronized void process(Map<String, String> items) {
    Map<String, Object> item = new HashMap<>();
    for (Map.Entry<String, String> entry : items.entrySet()) {
      item.put(entry.getKey(), entry.getValue());
    }

    mMaps.add(item);
  }

  public List<Map<String, Object>> getMaps() {
    return mMaps;
  }
}

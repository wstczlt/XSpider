package com.test.radar;

import java.util.Map;

import com.test.Keys;
import com.test.entity.Estimation;
import com.test.entity.Model;

public interface EstimationConsumer extends Keys {

  void accept(Map<String, Object> match, Model model, Estimation est);
}

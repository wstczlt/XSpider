package com.test.runtime.est;

import com.test.train.model.Model;
import com.test.train.tools.Estimation;
import com.test.train.tools.Match;

public interface EstimationConsumer {

  void onEstimation(Match match, Model model, Estimation est);
}

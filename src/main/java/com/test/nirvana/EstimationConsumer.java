package com.test.nirvana;

import com.test.entity.Estimation;
import com.test.entity.Match;
import com.test.entity.Model;

public interface EstimationConsumer {

  void accept(Match match, Model model, Estimation est);
}

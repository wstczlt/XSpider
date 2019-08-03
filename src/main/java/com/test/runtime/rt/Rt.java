package com.test.runtime.rt;

import java.util.function.Predicate;

import com.test.train.model.Model;
import com.test.train.tools.Match;

public interface Rt extends Predicate<Match> {

  /**
   * 对应的模型.
   */
  Model model();
}

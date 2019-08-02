package com.test.runtime.rt;

import java.util.List;
import java.util.function.Predicate;

import com.test.train.model.Model;
import com.test.train.tools.Match;

public interface Rt extends Predicate<Match> {

  /**
   * 对应的模型.
   */
  Model model();

  /**
   * 查询语句.
   * 
   * @param matchIDs 当前最新的比赛ID列表.
   */
  String buildSql(List<Integer> matchIDs);
}

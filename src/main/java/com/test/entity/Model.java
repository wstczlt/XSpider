package com.test.entity;

import java.util.List;
import java.util.function.Predicate;

import com.test.phoenix.PhoenixMapper;

/**
 * 足球AI模型基类.
 */
public abstract class Model implements Predicate<Match> {


  /**
   * 模型名称.
   */
  public abstract String name();

  /**
   * 需要训练的数据集X.
   */
  public abstract List<PhoenixMapper> mapOfX();

  /**
   * 训练集的结果集的Y.
   */
  public abstract PhoenixMapper mapOfY();

  /**
   * 判断该Match是否符合这个模型的训练要求.
   */
  @Override
  public boolean test(Match match) {
    return true;
  }

  /**
   * 优选高概率的结果.
   */
  public float bestThreshold() { // 大于等于此概率, 才会选择这场比赛
    return 0.50f;
  }

  /**
   * 计算盈利.
   */
  public abstract float calGain(Match match, Estimation est);
}

package com.test.entity;

import java.util.List;

import com.test.Keys;

/**
 * 足球AI模型基类.
 */
public abstract class Model implements Keys {


  /**
   * 模型名称.
   */
  public abstract String name();

  /**
   * 需要训练的数据集X.
   */
  public abstract List<Float> xValues(Match match);

  /**
   * 训练集的结果集的Y.
   */
  public abstract Float yValue(Match match);


  /**
   * 查询语句.
   */
  public abstract String querySql();

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

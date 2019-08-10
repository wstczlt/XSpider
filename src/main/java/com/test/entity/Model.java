package com.test.entity;

import java.util.List;
import java.util.Map;

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
  public abstract List<Float> xValues(Map<String, Object> match);

  /**
   * 训练集的结果集的Y.
   */
  public abstract Float yValue(Map<String, Object> match);

  /**
   * 查询语句.
   */
  public abstract String querySql(String andSql);

  /**
   * 计算盈利.
   */
  public abstract float calGain(Map<String, Object> attrs, Estimation est);
}

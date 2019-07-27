package com.test.train.tools;

/**
 * 预测结果.
 */
public class Estimation {

  public final float mValue; // 预测值
  public final float mProbability; // 预测概率

  public Estimation(float value, float probability) {
    mValue = value;
    mProbability = probability;
  }
}

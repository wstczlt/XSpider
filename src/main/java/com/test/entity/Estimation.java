package com.test.entity;

/**
 * 预测结果.
 */
public class Estimation {

  public final float mValue; // 预测值 0=主, 1=走，2=客
  public final float mProbability; // 预测概率

  public Estimation(float value, float probability) {
    mValue = value;
    mProbability = probability;
  }
}

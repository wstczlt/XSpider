package com.test.entity;

import java.util.Map;

/**
 * 预测结果.
 */
public class Estimation {

  public final Model mModel;
  public final Map<String, Object> mMatch;
  public final float mValue; // 预测值 0=主, 1=走，2=客
  public final float mProbability; // 预测概率
  public final float mProb0; // 0的概率
  public final float mProb1; // 1的概率
  public final float mProb2; // 2的概率

  public Estimation(Model model, Map<String, Object> match, float value,
      float prob0, float prob1, float prob2) {
    mModel = model;
    mMatch = match;
    mValue = value;
    mProb0 = prob0;
    mProb1 = prob1;
    mProb2 = prob2;
    mProbability = Math.abs(prob0 - prob2);
    // mProbability = prob0 > prob2 ? (prob0 + prob1) : (prob2 + prob1);
  }
}

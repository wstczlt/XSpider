package com.test.train.match;

public class PredictResult {

  public final float mTotalCount; // 总数
  public final float mHitCount; // 命中次数
  public final float mPositiveHitCount; // 正向盘命中率
  public final float mProfit; // 盈利

  public PredictResult(float totalCount, float hitCount, float positiveHitCount, float profit) {
    mTotalCount = totalCount;
    mHitCount = hitCount;
    mPositiveHitCount = positiveHitCount;
    mProfit = profit;
  }
}

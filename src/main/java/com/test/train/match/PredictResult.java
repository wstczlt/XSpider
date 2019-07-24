package com.test.train.match;

public class PredictResult {

  public final float mTotalCount; // 总数
  public final float mHitCount; // 命中次数
  public final float mPositiveHitCount; // 正向盘命中率
  public final float mProfit; // 盈利
  public final float mMaxContinueHitCount; // 最大连红次数
  public final float mMaxContinueMissCount; // 最大连黑次数

  public PredictResult(float totalCount, float hitCount, float positiveHitCount, float profit,
      float maxContinueHitCount, float maxContinueMissCount) {
    mTotalCount = totalCount;
    mHitCount = hitCount;
    mPositiveHitCount = positiveHitCount;
    mProfit = profit;
    mMaxContinueHitCount = maxContinueHitCount;
    mMaxContinueMissCount = maxContinueMissCount;
  }
}

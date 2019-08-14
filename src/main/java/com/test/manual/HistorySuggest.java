package com.test.manual;

public class HistorySuggest {

  public boolean mFullKeys;

  public final float mScoreOdd;
  public final int mScoreValue;
  public final int mTotalScoreCount;
  public final float mScoreProfit;
  public final float mScoreProb0;
  public final float mScoreProb1;
  public final float mScoreProb2;

  public final float mBallOdd;
  public final int mBallValue;
  public final int mTotalBallCount;
  public final float mBallProfit;
  public final float mBallProb0;
  public final float mBallProb1;
  public final float mBallProb2;

  public HistorySuggest(float scoreOdd, int scoreValue, int totalScoreCount, float scoreProfit,
      float scoreProb0, float scoreProb1, float scoreProb2, float ballOdd, int ballValue,
      int totalBallCount, float ballProfit, float ballProb0, float ballProb1, float ballProb2) {
    mScoreOdd = scoreOdd;
    mScoreValue = scoreValue;
    mTotalScoreCount = totalScoreCount;
    mScoreProfit = scoreProfit;
    mScoreProb0 = scoreProb0;
    mScoreProb1 = scoreProb1;
    mScoreProb2 = scoreProb2;
    mBallOdd = ballOdd;
    mBallValue = ballValue;
    mTotalBallCount = totalBallCount;
    mBallProfit = ballProfit;
    mBallProb0 = ballProb0;
    mBallProb1 = ballProb1;
    mBallProb2 = ballProb2;
  }

  public static HistorySuggest EMPTY = new HistorySuggest(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
}

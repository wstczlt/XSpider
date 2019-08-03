package com.test.deprecated.model;

public enum WeatherType {

  UNKNOWN(0), // 未知
  SUNNY(1), // 晴天
  SMALL_RAIN(2), // 小雨
  HEAVY_RAIN(3); // 暴雨

  public final int mValue;

  WeatherType(int value) {
    mValue = value;
  }
}

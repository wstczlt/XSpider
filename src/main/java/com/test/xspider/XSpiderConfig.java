package com.test.xspider;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XSpiderConfig {

  // public static final int MATCH_ID_START = 1200000; // 起始ID
  // public static final int MATCH_ID_END = 1730000; // 结束ID
  // public static final int MAX_CACHE_ITEMS = 1000; // 先攒数据，用于创建数据库的初始key
  // public static final int SPIDER_THREAD_COUNT = 10; // 线程数

  public static final int MATCH_ID_START = 1661600; // 起始ID
  public static final int MATCH_ID_END = 1661610; // 结束ID
  public static final int MAX_CACHE_ITEMS = 10; // 先攒数据，用于创建数据库的初始key
  public static final int SPIDER_THREAD_COUNT = 1; // 线程数

  public static final String MIN_DATE_STRING = "2016-01-01 00:00:00"; // 最小比赛时间
  public static final String MAX_DATE_STRING = "2019-06-30 23:00:00"; // 最大比赛时间
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd HH:mm");


  public static Date MIN_DATE = null;
  public static Date MAX_DATE = null;
  static {
    try {
      XSpiderConfig.MIN_DATE = XSpiderConfig.DATE_FORMAT.parse(MIN_DATE_STRING);
      XSpiderConfig.MAX_DATE = XSpiderConfig.DATE_FORMAT.parse(MAX_DATE_STRING);
    } catch (Throwable ignore) {}
  }



}

package com.test.spider;

import java.text.SimpleDateFormat;

public class SpiderConfig {

  public static final String USER_AGENT =
      "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36";

  public static final int DOWNLOAD_RETRY_COUNT = 1;
  public static final int THREAD_SLEEP_TIME = 1000;
  public static final int TOTAL_THREAD_COUNT = 10; // 抓数据线程
  public static final int MAX_CACHE_ITEMS = 1000; // 先攒数据，用于创建数据库的初始key

  public static final int STATIC_ID_START = 1756889; // 起始ID
  public static final int STATIC_ID_END = 1756899; // 结束ID
  public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_x.db";



  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-HH-dd HH:mm");

  // public static final String MIN_DATE_STRING = "2016-01-01 00:00"; // 最小比赛时间
  // public static final String MAX_DATE_STRING = "2019-07-18 23:00"; // 最大比赛时间
  //
  // public static Date MIN_DATE = null;
  // public static Date MAX_DATE = null;
  // static {
  // try {
  // MIN_DATE = DATE_FORMAT.parse(MIN_DATE_STRING);
  // MAX_DATE = DATE_FORMAT.parse(MAX_DATE_STRING);
  // } catch (Throwable ignore) {}
  // }
}

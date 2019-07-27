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
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

  public static final String PROXY_STRING =
      "58.218.214.170:9545 58.218.214.182:9571 58.218.214.170:9516 58.218.214.171:9869 58.218.214.169:9504 58.218.214.180:9788 58.218.214.180:9957 58.218.214.173:9897 58.218.214.171:9821 58.218.214.169:9659 58.218.214.180:9539 58.218.214.170:9867 58.218.214.170:9940 58.218.214.181:9591 58.218.214.173:9545 58.218.214.181:9562 58.218.214.168:9837 58.218.214.181:9893 58.218.214.168:9663 58.218.214.170:9631 58.218.214.172:9774 58.218.214.172:9969 58.218.214.168:9983 58.218.214.171:9897 58.218.214.172:9930 58.218.214.182:9668 58.218.214.171:9546 58.218.214.173:9821 58.218.214.171:9904 58.218.214.168:9998";

}

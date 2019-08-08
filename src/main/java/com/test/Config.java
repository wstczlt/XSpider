package com.test;

import com.test.tools.Logger;

public class Config {

  // 线程总数
  public static final int MAX_THREAD_COUNT = 10;

  // public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_x.db";
  // public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_dragon.db";
  public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_ds.db";

  // 日志输出
  public static final Logger LOGGER = Logger.SYSTEM;

  // 实时抓取比赛时, 一圈不低于1分钟
  public static final long MIN_ONE_LOOP = 60 * 1000;

  public static final String PROXY_STRING =
      "58.218.200.223:6326 58.218.200.228:3488 58.218.200.228:9101 58.218.200.223:3969 58.218.200.227:9196 58.218.200.223:8462 58.218.200.228:9091 58.218.200.227:9061 58.218.200.226:9104 58.218.200.228:3616 58.218.200.228:2910 58.218.200.228:9149 58.218.200.228:4569 58.218.200.227:9119 58.218.200.227:9156 58.218.200.227:9198 58.218.200.226:9028 58.218.200.223:9096 58.218.200.227:9170 58.218.200.228:9113 58.218.200.226:9066 58.218.200.227:9175 58.218.200.228:7020 58.218.200.223:4743 58.218.200.228:9157 58.218.200.223:9102 58.218.200.228:2388 58.218.200.226:9114 58.218.200.228:9110 58.218.200.228:9080 58.218.200.228:8035 58.218.200.223:9138 58.218.200.226:9138 58.218.200.226:9069 58.218.200.228:8631 58.218.200.227:9141 58.218.200.227:9124 58.218.200.228:9162 58.218.200.223:9196 58.218.200.223:7863 58.218.200.226:9199 58.218.200.227:9149 58.218.200.223:6705 58.218.200.223:9179 58.218.200.226:9111 58.218.200.227:9181";
}
package com.test;

import com.test.tools.Logger;

public class Config {

  // 线程总数
  public static final int MAX_THREAD_COUNT = 10;

  // public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_x.db";
  public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_dragon.db";

  // 日志输出
  public static final Logger LOGGER = Logger.EMPTY;

  // 实时抓取比赛时, 一圈不低于1分钟
  public static final long MIN_ONE_LOOP = 60 * 1000;
}

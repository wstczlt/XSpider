package com.test;

import com.test.tools.Logger;

public class Config {

  // 线程总数
  public static final int SPIDER_THREAD_COUNT = 8;

  public static final int RADAR_THREAD_COUNT = 1;

  // public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_x.db";
  // public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_dragon.db";
  public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_ds.db";

  // 日志输出
  public static final Logger LOGGER = Logger.SYSTEM;

  // 实时抓取比赛时, 一圈不低于1分钟
  public static final long MIN_ONE_LOOP = 60 * 1000;

  public static final String PROXY_STRING =
      "58.218.200.220:6174 58.218.201.122:9174 58.218.200.214:9152 58.218.200.220:6171 58.218.201.114:9173 58.218.201.122:9160 58.218.201.122:9171 58.218.201.114:9159 58.218.200.253:9190 58.218.200.220:6189 58.218.201.122:9187 58.218.201.114:9155 58.218.200.253:9181 58.218.200.220:6193 58.218.201.114:9187 58.218.201.122:9182 58.218.200.214:9188 58.218.201.122:9151 58.218.200.253:9154 58.218.201.74:9170";
}

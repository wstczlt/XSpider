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

  public static final String PROXY_STRING = "58.218.200.229:9150 58.218.200.247:9175 58.218.200.247:9159 58.218.200.229:9168 58.218.200.248:9193 58.218.200.247:9180 58.218.200.229:9161 58.218.200.249:9178 58.218.200.247:9167 58.218.200.229:9183 58.218.200.249:9165 58.218.200.229:9165 58.218.200.248:9165 58.218.200.248:9170 58.218.200.229:9172 58.218.200.229:9179 58.218.200.247:9195 58.218.200.248:9175 58.218.200.247:9196 58.218.200.229:9175";
}

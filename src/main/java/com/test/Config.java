package com.test;

import com.test.tools.Logger;

public class Config {

  // 线程总数
  public static final int MAX_THREAD_COUNT = 5;

  // public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_x.db";
  // public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_dragon.db";
  public static final String DATABASE_URL = "jdbc:sqlite:sqlite/football_ds.db";

  // 日志输出
  public static final Logger LOGGER = Logger.SYSTEM;

  // 实时抓取比赛时, 一圈不低于1分钟
  public static final long MIN_ONE_LOOP = 60 * 1000;

  public static final String PROXY_STRING =
      "58.218.200.237:5162 58.218.200.237:5194 58.218.200.248:9158 58.218.200.237:5184 58.218.200.247:9196 58.218.200.237:5152 58.218.200.229:9184 58.218.200.237:5190 58.218.200.237:5172 58.218.200.247:9186 58.218.200.247:9156 58.218.200.237:5189 58.218.200.229:9193 58.218.200.249:9196 58.218.200.249:9171 58.218.200.247:9182 58.218.200.249:9176 58.218.200.249:9195 58.218.200.247:9171 58.218.200.229:9182 58.218.200.237:5191 58.218.200.249:9193 58.218.200.249:9191 58.218.200.229:9194 58.218.200.247:9159 58.218.200.247:9155 58.218.200.229:9191 58.218.200.229:9174 58.218.200.229:9168 58.218.200.247:9158";
}

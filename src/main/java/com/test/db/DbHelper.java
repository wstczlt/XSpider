package com.test.db;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

import com.test.Config;

public class DbHelper {

  private BasicDataSource mDataSource;

  public synchronized DataSource open() {
    if (mDataSource == null) {
      mDataSource = new BasicDataSource();
      // 基本设置
      mDataSource.setDriverClassName("org.sqlite.JDBC");
      String databaseUrl = Config.DATABASE_URL;
      mDataSource.setUrl(databaseUrl);
      // 高级设置
      mDataSource.setInitialSize(10);// 初始化连接
      mDataSource.setMinIdle(5);// 最小空闲连接
      mDataSource.setMaxIdle(20);// 最大空闲连接
      mDataSource.setMaxActive(50);// 最大连接数量
    }

    return mDataSource;
  }
}

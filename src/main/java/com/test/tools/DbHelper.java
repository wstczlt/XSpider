package com.test.tools;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class DbHelper {

  private final String mDatabaseUrl;
  private BasicDataSource mDataSource;

  public DbHelper(String databaseUrl) {
    mDatabaseUrl = databaseUrl;
  }

  public synchronized DataSource open() {
    if (mDataSource == null) {
      mDataSource = new BasicDataSource();
      // 基本设置
      mDataSource.setDriverClassName("org.sqlite.JDBC");
      mDataSource.setUrl(mDatabaseUrl);
      // 高级设置
      mDataSource.setInitialSize(10);// 初始化连接
      mDataSource.setMinIdle(5);// 最小空闲连接
      mDataSource.setMaxIdle(20);// 最大空闲连接
      mDataSource.setMaxActive(50);// 最大连接数量
    }

    return mDataSource;
  }
}

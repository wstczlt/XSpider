package com.test.dragon.tools;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class DragonDatabase {

  private final String mDatabaseUrl;
  private BasicDataSource mDataSource;

  public DragonDatabase(String databaseUrl) {
    mDatabaseUrl = databaseUrl;
  }

  public synchronized DataSource getDataSource() {
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

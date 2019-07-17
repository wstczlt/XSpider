package com.test.xspider.pipline;

import com.test.xspider.XSpiderConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class SQLitePipeline implements Pipeline {

  private BasicDataSource mDataSource;
  private List<String> mColumns = new ArrayList<>();
  private List<ResultItems> mResultItems = new ArrayList<>(); // 前期积累元素, 得到一个全量的key集合

  private boolean mTableCreated; // 标记是否创建了数据表

  @Override
  public synchronized void process(ResultItems resultItems, Task task) {
    if (resultItems.isSkip()) { // 废弃的结果
      return;
    }
    if (!mTableCreated && mResultItems.size() < XSpiderConfig.MAX_CACHE_ITEMS) {
      mResultItems.add(resultItems);
      return;
    }
    if (!mTableCreated) { // 创建数据表并把缓存数据写进去
      createTable();
      mTableCreated = true;
      mResultItems.forEach(this::updateDatabase);
      mResultItems.clear(); // 避免占内存
    }
    updateDatabase(resultItems);
  }

  private void createTable() {
    try {
      mColumns = obtainColumns();
      StringBuilder sb = new StringBuilder("CREATE TABLE football(matchID INTEGER PRIMARY KEY");
      for (String column : mColumns) {
        sb.append(", ").append(column).append(" TEXT");
      }
      sb.append(")");
      final QueryRunner runner = new QueryRunner(obtainDataSource());
      runner.update("DROP TABLE if exists football");
      runner.update(sb.toString());
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private void updateDatabase(ResultItems items) {
    try {
      final Integer matchID = items.get("matchID"); // matchID必须要
      QueryRunner runner = new QueryRunner(obtainDataSource());
      Map<String, Object> resultMap =
          runner.query("select matchID from football where matchID=" + matchID, new MapHandler());
      if (resultMap != null && resultMap.size() > 0) {// 做update
        runner.update(buildUpdateSQL(matchID, items));
      } else { // 做insert
        runner.update(buildInsertSQL(matchID, items));
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private List<String> obtainColumns() {
    Set<String> columnSet = new HashSet<>();
    for (ResultItems items : mResultItems) { // 去重
      columnSet.addAll(items.getAll().keySet());
    }
    columnSet.remove("matchID"); // matchID单独处理
    String[] columnArray = new String[columnSet.size()];
    columnSet.toArray(columnArray);
    Arrays.sort(columnArray); // 排序
    return Arrays.asList(columnArray);
  }

  private String buildInsertSQL(int matchID, ResultItems items) {
    StringBuilder sb = new StringBuilder("INSERT INTO  football(matchID");
    for (String key : mColumns) {
      if (items.getAll().containsKey(key)) {
        sb.append(",").append(key);
      }
    }
    sb.append(") VALUES(").append(matchID);
    for (String key : mColumns) {
      if (items.getAll().containsKey(key)) {
        sb.append(",").append("'").append(items.get(key).toString()).append("'");
      }
    }
    sb.append(")");
    return sb.toString();
  }

  private String buildUpdateSQL(int matchID, ResultItems items) {
    StringBuilder sb =
        new StringBuilder("UPDATE football SET matchID=").append(matchID).append(",");
    for (String key : mColumns) {
      if (items.getAll().containsKey(key)) {
        sb.append(key).append("=").append("'").append(items.get(key).toString()).append("'")
            .append(",");
      }
    }
    sb.deleteCharAt(sb.length() - 1); // 删掉最后一个逗号;
    sb.append(" WHERE matchID=").append(matchID);
    return sb.toString();
  }

  private DataSource obtainDataSource() {
    if (mDataSource == null) {
      mDataSource = new BasicDataSource();
      // 基本设置
      mDataSource.setDriverClassName("org.sqlite.JDBC");
      mDataSource.setUrl("jdbc:sqlite:sqlite/football_1.db");
      // 高级设置
      mDataSource.setInitialSize(10);// 初始化连接
      mDataSource.setMinIdle(5);// 最小空闲连接
      mDataSource.setMaxIdle(20);// 最大空闲连接
      mDataSource.setMaxActive(50);// 最大连接数量
    }

    return mDataSource;
  }
}
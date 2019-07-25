package com.test.spider.pipline;

import static com.test.spider.SpiderDB.getDataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;

import com.test.spider.SpiderConfig;
import com.test.spider.tools.Logger;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class SQLitePipeline implements Pipeline {

  private final Logger mLogger;

  public SQLitePipeline(Logger logger) {
    mLogger = logger;
  }

  private static final Set<String> MUST_HAVE_COLUMNS = new HashSet<>();
  static {
    MUST_HAVE_COLUMNS.add("matchID");
    MUST_HAVE_COLUMNS.add("matchTime");
    MUST_HAVE_COLUMNS.add("hostName");
    MUST_HAVE_COLUMNS.add("hostNamePinyin");
    MUST_HAVE_COLUMNS.add("customName");
    MUST_HAVE_COLUMNS.add("customNamePinyin");
    MUST_HAVE_COLUMNS.add("hostScore");
    MUST_HAVE_COLUMNS.add("customScore");
    MUST_HAVE_COLUMNS.add("hostCornerScore");
    MUST_HAVE_COLUMNS.add("customCornerScore");
    MUST_HAVE_COLUMNS.add("league");
    MUST_HAVE_COLUMNS.add("hostLeagueRank");
    MUST_HAVE_COLUMNS.add("hostLeagueRateOfVictory");
    MUST_HAVE_COLUMNS.add("hostLeagueOnHostRank");
    MUST_HAVE_COLUMNS.add("hostLeagueOnHostRateOfVictory");
    MUST_HAVE_COLUMNS.add("customLeagueRank");
    MUST_HAVE_COLUMNS.add("customLeagueRateOfVictory");
    MUST_HAVE_COLUMNS.add("customLeagueOnCustomRank");
    MUST_HAVE_COLUMNS.add("customLeagueOnCustomRateOfVictory");
    MUST_HAVE_COLUMNS.add("original_scoreOdd");
    MUST_HAVE_COLUMNS.add("original_scoreOddOfVictory");
    MUST_HAVE_COLUMNS.add("original_scoreOddOfDefeat");
    MUST_HAVE_COLUMNS.add("opening_scoreOdd");
    MUST_HAVE_COLUMNS.add("opening_scoreOddOfVictory");
    MUST_HAVE_COLUMNS.add("opening_scoreOddOfDefeat");

    MUST_HAVE_COLUMNS.add("original_bigOdd");
    MUST_HAVE_COLUMNS.add("original_bigOddOfVictory");
    MUST_HAVE_COLUMNS.add("original_bigOddOfDefeat");
    MUST_HAVE_COLUMNS.add("opening_bigOdd");
    MUST_HAVE_COLUMNS.add("opening_bigOddOfVictory");
    MUST_HAVE_COLUMNS.add("opening_bigOddOfDefeat");

    MUST_HAVE_COLUMNS.add("original_drawOdd");
    MUST_HAVE_COLUMNS.add("original_victoryOdd");
    MUST_HAVE_COLUMNS.add("original_defeatOdd");
    MUST_HAVE_COLUMNS.add("opening_drawOdd");
    MUST_HAVE_COLUMNS.add("opening_victoryOdd");
    MUST_HAVE_COLUMNS.add("opening_defeatOdd");

    MUST_HAVE_COLUMNS.add("original_cornerOdd");
    MUST_HAVE_COLUMNS.add("original_cornerOddOfVictory");
    MUST_HAVE_COLUMNS.add("original_cornerOddOfDefeat");
    MUST_HAVE_COLUMNS.add("opening_cornerOdd");
    MUST_HAVE_COLUMNS.add("opening_cornerOddOfVictory");
    MUST_HAVE_COLUMNS.add("opening_cornerOddOfDefeat");

    MUST_HAVE_COLUMNS.add("timeMin");

  }

  private List<String> mColumns = new ArrayList<>();
  private List<ResultItems> mResultItems = new ArrayList<>(); // 前期积累元素, 得到一个全量的key集合

  private boolean mTableCreated = isExistTable(); // 标记是否创建了数据表
  private AtomicInteger mValueCount = new AtomicInteger(0);

  @Override
  public synchronized void process(ResultItems resultItems, Task task) {
    if (resultItems.isSkip()) { // 废弃的结果
      return;
    }
    if (!mTableCreated && mResultItems.size() < SpiderConfig.MAX_CACHE_ITEMS) {
      mResultItems.add(resultItems);
      return;
    }
    ensureColumns();
    if (!mTableCreated) { // 创建数据表并把缓存数据写进去
      createTable();
      mTableCreated = true;
      mResultItems.forEach(this::updateDatabase);
      mResultItems.clear(); // 避免占内存
    }
    updateDatabase(resultItems);
  }

  private void openDatabase() {

  }


  private boolean isExistTable() {
    try {
      QueryRunner runner = new QueryRunner(getDataSource());
      Map<String, Object> map =
          runner.query("select count(*) as cnt from football", new MapHandler());
      return ((Integer) map.get("cnt")) > 0;
    } catch (Throwable e) {
      return false;
    }
  }

  private void createTable() {
    try {
      StringBuilder sb = new StringBuilder("CREATE TABLE football(matchID INTEGER PRIMARY KEY");
      for (String column : mColumns) {
        sb.append(", ").append(column).append(" TEXT");
      }
      sb.append(")");
      mLogger.log("SQL: " + sb.toString());
      // System.exit(0);
      final QueryRunner runner = new QueryRunner(getDataSource());
      // runner.update("DROP TABLE if exists football");
      runner.update(sb.toString());
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private void updateDatabase(ResultItems items) {
    try {
      final Integer matchID = items.get("matchID"); // matchID必须要
      QueryRunner runner = new QueryRunner(getDataSource());
      Map<String, Object> resultMap =
          runner.query("select matchID from football where matchID=" + matchID, new MapHandler());
      if (resultMap != null && resultMap.size() > 0) {// 做update
        runner.update(buildUpdateSQL(matchID, items));
      } else { // 做insert
        runner.update(buildInsertSQL(matchID, items));
      }

      final Float scoreOddOfVictory = items.get("original_scoreOddOfVictory");
      if (scoreOddOfVictory != null && scoreOddOfVictory > 0) {
        int cnt = mValueCount.getAndIncrement();
        System.out
            .println(String.format("DATABASE: matchID=%d, valueCount=%d, scoreOddOfVictory=%.2f",
                matchID, cnt, scoreOddOfVictory));
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  private void ensureColumns() {
    if (mColumns != null && !mColumns.isEmpty()) {
      return;
    }
    Set<String> columnSet = new HashSet<>(MUST_HAVE_COLUMNS);
    if (mTableCreated) {
      QueryRunner runner = new QueryRunner(getDataSource());
      try {
        Map<String, Object> map = runner.query("select * from football limit 1", new MapHandler());
        columnSet.addAll(map.keySet());
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      for (ResultItems items : mResultItems) { // 去重
        columnSet.addAll(items.getAll().keySet());
      }
    }
    columnSet.remove("matchID"); // matchID单独处理
    String[] columnArray = new String[columnSet.size()];
    columnSet.toArray(columnArray);
    Arrays.sort(columnArray); // 排序
    mColumns = Arrays.asList(columnArray);
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
}

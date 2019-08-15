package com.test.tools;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.io.IOUtils;

import com.test.Config;
import com.test.Keys;
import com.test.entity.Estimation;
import com.test.entity.Model;
import com.test.win007_deprecated.tools.SpiderUtils;

public class Utils {

  private static ThreadLocal<SimpleDateFormat> SDF = new ThreadLocal<>();

  public static int valueOfInt(Object str) {
    try {
      return Integer.parseInt(str != null ? str.toString() : "");
    } catch (Throwable e) {
      return -1;
    }
  }

  public static long valueOfLong(Object str) {
    try {
      return Long.parseLong(str != null ? str.toString() : "");
    } catch (Throwable e) {
      return 999;
    }
  }

  public static float valueOfFloat(Object str) {
    try {
      return Float.parseFloat(str != null ? str.toString() : "");
    } catch (Throwable e) {
      return 999;
    }
  }

  public static long valueOfDate(String time) throws Exception {
    SimpleDateFormat sdf = SDF.get();
    if (sdf == null) {
      sdf = new SimpleDateFormat("yyyyMMddhhmmss");
      SDF.set(sdf);
    }
    return sdf.parse(time).getTime();
  }

  public static String exec(String cmd) throws Exception {
    Process process = Runtime.getRuntime().exec(cmd);
    String output = IOUtils.toString(process.getInputStream());
    process.destroyForcibly();

    return output;
  }

  public static List<Estimation> readResult(String result, Model model,
      List<Map<String, Object>> matches) {
    final List<Estimation> estimations = new ArrayList<>();
    final String[] lines = result.replace("\r", "").split("\n");
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      Estimation est;
      String[] arr = line.replace("[", "").replace("]", "").split("\\s+");
      if (arr.length == 2) {
        arr = new String[] {arr[0], "0", arr[1]};
      }
      if (arr.length != 3) {
        throw new RuntimeException(line);
      }
      float prob0 = valueOfFloat(arr[0]);
      float prob1 = valueOfFloat(arr[1]);
      float prob2 = valueOfFloat(arr[2]);
      if (prob0 > prob2) {
        est = new Estimation(matches.get(i),
            0, prob0, prob1, prob2, 1);
      } else {
        est = new Estimation(matches.get(i),
            2, prob0, prob1, prob2, 1);
      }
      estimations.add(est);
    }

    return estimations;
  }

  public static String yMetric(float yValue) {
    switch ((int) yValue) {
      case 0:
        return "1   0   0";
      case 1:
        return "0   1   0";
      case 2:
        return "0   0   1";
      default:
        throw new RuntimeException(yValue + "");
    }
  }

  /**
   * 输出错误日志.
   */
  public static void log(Throwable e) {
    StringWriter writer = new StringWriter();
    e.printStackTrace(new PrintWriter(writer));

    Config.LOGGER.log(writer.toString());
  }


  public static String nameOfX(Model model) {
    return "temp/" + model.name() + "_x" + ".dat";
  }

  public static String nameOfY(Model model) {
    return "temp/" + model.name() + "_y" + ".dat";
  }

  public static String nameOfYMetric(Model model) {
    return "temp/" + model.name() + "_y_metric" + ".dat";
  }

  public static String nameOfTestX(Model model) {
    return "temp/" + model.name() + "_x_test" + ".dat";
  }

  public static String nameOfTestY(Model model) {
    return "temp/" + model.name() + "_y_test" + ".dat";
  }

  public static String nameOfTestYMetric(Model model) {
    return "temp/" + model.name() + "_y_metric_test" + ".dat";
  }

  public static String nameOfModel(Model model) {
    return "temp/" + model.name() + ".m";
  }

  public static boolean isSkip(Map<String, String> items) {
    return items.containsKey(Keys.SKIP);
  }

  public static void setSkip(Map<String, String> items) {
    items.put(Keys.SKIP, String.valueOf(true));
  }

  public static float convertDsOdd(String oddString) {
    if (oddString == null) {
      return 999;
    }
    try {
      return Float.parseFloat(oddString);
    } catch (Exception e) {
      String[] pair;
      if (oddString.contains(",")) {
        pair = oddString.split(",");
      } else {
        pair = oddString.split("/");
      }

      if (pair.length == 2) {
        try {
          return (Float.parseFloat(pair[0]) + Float.parseFloat(pair[1])) / 2;
        } catch (Exception ignore) {
          float ret = SpiderUtils.convertOdd(oddString);
          if (ret != 999) {
            return ret;
          }
          if (oddString.contains("/") && oddString.contains("球") && oddString.endsWith("球")) {
            oddString = oddString.substring(0, oddString.length() - 1);
          }
          ret = SpiderUtils.convertOdd(oddString);
          if (ret != 999) {
            return ret;
          }
          if (oddString.contains("/") && oddString.contains("球")) {
            oddString = oddString.replaceFirst("球", "");
          }
          ret = SpiderUtils.convertOdd(oddString);
          if (ret != 999) {
            return ret;
          }
          System.out.println(oddString);
          System.out.println(Arrays.toString(pair));
        }
      }
    }

    return 999;
  }

  public static float deltaScore(int timeMin, Map<String, Object> match) {
    String timePrefix = "min" + timeMin + "_";
    int hostScore = valueOfInt(match.get(Keys.HOST_SCORE));
    int customScore = valueOfInt(match.get(Keys.CUSTOM_SCORE));
    if (timeMin < 0) {
      float timeScoreOdd = valueOfFloat(match.get(Keys.ORIGINAL_SCORE_ODD));
      return (hostScore - customScore) + timeScoreOdd;
    } else {
      int timeHostScore = valueOfInt(match.get(timePrefix + "hostScore"));
      int timeCustomScore = valueOfInt(match.get(timePrefix + "customScore"));
      float timeScoreOdd = valueOfFloat(match.get(timePrefix + "scoreOdd"));
      return (hostScore - timeHostScore) - (customScore - timeCustomScore) + timeScoreOdd;
    }
  }

  public static float calGain(int timeMin, Map<String, Object> dbMap, Estimation est) {
    // 让球算法
    String timePrefix = "min" + timeMin + "_";
    float victory = valueOfFloat(dbMap.get(timePrefix + "scoreOddOfVictory")) - 1;
    float defeat = valueOfFloat(dbMap.get(timePrefix + "scoreOddOfDefeat")) - 1;
    float deltaScore = deltaScore(timeMin, dbMap);

    if (est.mValue == 0) { // 判断主队
      if (deltaScore >= 0.5) return victory;
      if (deltaScore >= 0.25) return victory * 0.5f;
      if (deltaScore == 0) return 0;
      if (deltaScore >= -0.25) return -0.5f;
      if (deltaScore <= -0.5) return -1;
    }
    if (est.mValue == 1) { // 不买
      return 0;
    }
    if (est.mValue == 2) { // 判断客队
      if (deltaScore >= 0.5) return -1;
      if (deltaScore >= 0.25) return -0.5f;
      if (deltaScore == 0) return 0;
      if (deltaScore >= -0.25) return defeat * 0.5f;
      if (deltaScore <= -0.5) return defeat;
    }

    return 0;
  }


  public static <T> Predicate<T> distinctBy(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }
}

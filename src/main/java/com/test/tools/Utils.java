package com.test.tools;

import static com.test.Config.LOGGER;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.test.Keys;
import com.test.entity.Estimation;
import com.test.entity.Model;

public class Utils {

  private static ThreadLocal<SimpleDateFormat> SDF = new ThreadLocal<>();

  public static int valueOfInt(Object str) {
    try {
      return Integer.parseInt(str != null ? str.toString() : "");
    } catch (Throwable e) {
      return -1;
    }
  }

  public static float valueOfFloat(Object str) {
    try {
      return Float.parseFloat(str != null ? str.toString() : "");
    } catch (Throwable e) {
      return -1;
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

  public static List<Estimation> readResult(String result) {
    final List<Estimation> estimations = new ArrayList<>();
    final String[] lines = result.replace("\r", "").split("\n");
    Arrays.stream(lines).forEach(line -> {
      Estimation est;
      String[] arr = line.replace("[", "").replace("]", "").split(" ");
      if (arr.length != 2) {
        return;
      }
      float probOf0 = valueOfFloat(arr[0]);
      float probOf1 = valueOfFloat(arr[1]);
      if (probOf0 > probOf1) {
        est = new Estimation(0f, probOf0);
      } else {
        est = new Estimation(1f, probOf1);
      }
      estimations.add(est);
    });

    return estimations;
  }

  /**
   * 输出错误日志.
   */
  public static void log(Throwable e) {
    StringWriter writer = new StringWriter();
    e.printStackTrace(new PrintWriter(writer));

    LOGGER.log(writer.toString());
  }


  public static String nameOfX(Model model) {
    return "temp/" + model.name() + "_x" + ".dat";
  }

  public static String nameOfY(Model model) {
    return "temp/" + model.name() + "_y" + ".dat";
  }

  public static String nameOfTestX(Model model) {
    return "temp/" + model.name() + "_x_test" + ".dat";
  }

  public static String nameOfTestY(Model model) {
    return "temp/" + model.name() + "_y_test" + ".dat";
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
}

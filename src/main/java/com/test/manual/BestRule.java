package com.test.manual;

import static com.test.tools.Utils.valueOfInt;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

import com.test.Keys;

public class BestRule implements Keys {

  public static void main(String[] args) throws Exception {

  }

  public static boolean test(Map<String, Object> match) throws Exception {

   return false;
  }

  public static Predicate<Map<String, Object>> MIN_FILTER = match -> {
    int timeMin = valueOfInt(match.get(TIME_MIN));
    return timeMin >= 20 && timeMin <= 70;
  };


  public static Predicate<Map<String, Object>> SHOOT_FILTER = new Predicate<Map<String, Object>>() {
    @Override
    public boolean test(Map<String, Object> match) {
      int hostBestShoot = valueOfInt(match.get(HOST_BEST_SHOOT));
      int customBestShoot = valueOfInt(match.get(CUSTOM_BEST_SHOOT));
      int timeMin = valueOfInt(match.get(TIME_MIN));
      int shootDelta = Math.abs(hostBestShoot - customBestShoot);
      return shootDelta >= 3 && shootDelta >= timeMin / 20 + 2;
    }
  };

}

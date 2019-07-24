package com.test.realtime;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.test.spider.SpiderUtils;

public class RealtimeMain {

  public static void main(String[] args) {
    collectRealTimeMatchIds();
  }

  private static List<Integer> collectRealTimeMatchIds() {
    // sData[1722870]=[[0.87,0.75,0.95,1.11,0.5,0.78,,,],[1.69,3.35,4.40,2.12,3.10,3.25,,,],[0.95,2.25,0.85,0.90,2,0.96,,,],[0.89,0.25,0.93,1.20,0.25,0.71,,,],[0.72,0.75,1.08,0.82,0.75,1.04,,,],[2.33,2.01,5.10,2.78,1.88,4.35,,,]];sData[1744542]=[[0.91,2.5,0.85,0.85,2.75,0.97,,,],[1.09,7.90,12.50,1.05,9.20,16.50,,,],[0.80,3.75,0.96,1.08,4,0.72,,,],[0.81,1,0.95,1.04,1.25,0.78,,,],[0.76,1.50,1.00,1.05,1.75,0.75,,,],[1.39,3.40,9.00,1.33,3.60,11.50,,,]];sData[1748890]=[[0.78,0,0.98,1.00,-0.5,0.76,0.97,-0.5,0.85],[2.38,3.15,2.63,4.00,3.30,1.76,1.92,3.10,3.60],[0.96,2.25,0.80,0.91,2.25,0.85,0.91,2.75,0.89],[0.68,-0.25,1.08,0.78,-0.25,0.98,0.45,-0.25,1.58],[0.69,0.75,1.07,1.08,1,0.68,1.36,1.5,0.53],[4.20,2.00,2.63,4.65,2.04,2.41,1.22,4.30,15.50]];sData[1755308]=[[0.90,-1.25,0.80,0.74,-0.75,0.96,0.64,-0.5,1.06],[5.60,4.45,1.34,3.45,3.70,1.70,21.00,7.70,1.03],[0.80,3,0.90,0.88,3,0.82,0.82,1.75,0.88],[0.89,-0.25,0.87,,,,1.72,0,0.28],[1.01,1.25,0.75,,,,2.77,0.5,0.06],[4.10,2.24,2.17,,,,13.50,1.05,7.80]];sData[1755331]=[[0.94,-0.25,0.88,0.96,-0.5,0.86,,,],[2.73,3.65,2.08,3.20,3.70,1.86,,,],[0.85,3,0.95,0.85,3,0.95,,,],[,,,,,,,,],[,,,,,,,,],[,,,,,,,,]];sData[1756501]=[[0.77,0.75,0.99,0.70,1,1.13,,,],[1.58,3.85,4.45,1.41,4.20,5.70,,,],[0.96,2.75,0.80,0.98,2.75,0.82,,,],[0.85,0.25,0.97,1.02,0.5,0.80,,,],[0.77,1.00,1.03,0.75,1,1.05,,,],[2.21,2.23,4.55,2.02,2.25,5.50,,,]];
    final String requestUrl = "http://score.nowscore.com/data/sbOddsData.js";


    List<Integer> matchIds = new ArrayList<>();
    try {
      HttpResponse response = HttpClients.custom().build().execute(new HttpGet(requestUrl));
      HttpEntity entity = response.getEntity();
      String html = EntityUtils.toString(entity, "UTF-8");
      // System.out.println(html);
      Pattern pattern = Pattern.compile("sData\\[\\d+\\]");
      Matcher matcher = pattern.matcher(html);
      while (matcher.find()) {
        String matchString = matcher.group();
        matchString = matchString.replace("sData[", "").replace("]", "");
        matchIds.add(SpiderUtils.valueOfInt(matchString));
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }

    System.out.println(matchIds);
    return matchIds;

  }


}

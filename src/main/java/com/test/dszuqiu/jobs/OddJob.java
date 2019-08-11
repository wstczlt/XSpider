package com.test.dszuqiu.jobs;

import static com.test.tools.Utils.setSkip;

import java.util.Map;

import org.apache.http.util.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.test.Config;
import com.test.dszuqiu.parser.RaceParser;
import com.test.http.HttpJob;
import com.test.pipeline.FilePipeline;

import okhttp3.Request;

public class OddJob extends HttpJob {

  private static final String REQUEST_URL =
      "http://api.dszuqiu.com/v3/race/baijia/summary?token=&race_id=%d";

  private final FilePipeline mFilePipeline;

  public OddJob(int matchID) {
    super(matchID);
    mFilePipeline = new FilePipeline("/Users/Jesse/Desktop/odd", "_odd.txt");
  }

  @Override
  public Request.Builder newRequestBuilder() {
    String requestUrl = String.format(REQUEST_URL, mMatchID);
    return new Request.Builder()
        .header("User-Agent", "Android 6.0.1/CLT-AL00/9")
        .url(requestUrl);
  }

  @Override
  public void onResponse(String text, Map<String, String> items) throws Exception {
    JSONObject json = JSON.parseObject(text);
    String error = RaceParser.isLegalOdd(json);
    if (!TextUtils.isEmpty(error)) {
      setSkip(items);
      Config.LOGGER.log(
          String.format("[%s], matchID=%d, Error=%s", getClass().getSimpleName(), mMatchID, error));
      return;
    }

    // 存储
    mFilePipeline.process(mMatchID + "", text);

    // 处理Json
    // new OddParser(text, items).doParse();


    Config.LOGGER.log(String.format("Found Match ID=%d",
        mMatchID));
  }
}

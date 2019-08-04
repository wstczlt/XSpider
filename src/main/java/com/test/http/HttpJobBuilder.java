package com.test.http;

import java.util.List;

public interface HttpJobBuilder {

  List<HttpJob> buildJobs(int matchID);
}

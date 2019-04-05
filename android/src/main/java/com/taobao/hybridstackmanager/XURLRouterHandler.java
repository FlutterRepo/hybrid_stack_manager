package com.taobao.hybridstackmanager;

import java.util.HashMap;

public interface XURLRouterHandler {
    Class openUrlWithQueryAndParams(String url, HashMap query, HashMap params);
}

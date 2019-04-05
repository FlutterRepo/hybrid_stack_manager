package com.taobao.hybridstackmanager;

/**
 * Created by kylewong on 19/03/2018.
 */

 interface FlutterActivityChecker {

     boolean isActive();

     void openUrl(String url);

     void setCurFlutterRouteName(String curFlutterRouteName);

     void popCurActivity();
}

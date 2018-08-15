package com.taobao.hybridstackmanager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kylewong on 19/03/2018.
 */

public interface FlutterActivityChecker {

    public boolean isActive();

    public void openUrl(String url);

    public void setCurFlutterRouteName(String curFlutterRouteName);

    public void popCurActivity();
}

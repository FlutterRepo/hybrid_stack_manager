package com.taobao.hybridstackmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XURLRouter {
    final static String kOpenUrlPrefix = "hrd";
    static XURLRouter sRouterInst;
    Context mAppContext;
    XURLRouterHandler mNativeRouterHandler;
    public static boolean sReusingMode = false;
    public static Map<String, FlutterActivityChecker> sActivityMap = new LinkedHashMap<>();
    public static Map<FlutterActivityChecker, String> sActivityToFlutterPageName = new HashMap<>();
    public static Set<String> sFlutterPageNameNeedingBlockOnBackPressed = new HashSet<>();
    public static List<Activity> sActivityList = new ArrayList<>();
    public static List<String> sPageUrlList = new ArrayList<>();
    public static Map<String, Integer> sFlutterStackSizeMap = new HashMap<>();
    public static Map<String, String> sNativesLastFlutterPageName = new HashMap<>();
    ///记录着访问的页面的hash和埋点title
    public static Map<String, String> sUrlToPageTitleMap = new LinkedHashMap<>();

    public static XURLRouter sharedInstance() {
        if (sRouterInst == null) {
            sRouterInst = new XURLRouter();
        }
        return sRouterInst;
    }

    public void setAppContext(Context context) {
        mAppContext = context;
    }

    public void setNativeRouterHandler(XURLRouterHandler handler) {
        mNativeRouterHandler = handler;
    }

    public boolean openUrlWithQueryAndParams(String url, HashMap query, HashMap params) {
        Uri tmpUri = Uri.parse(url);
        if (!kOpenUrlPrefix.equals(tmpUri.getScheme()))
            return false;
        if (query != null && query.containsKey("flutter") && (Boolean) query.get("flutter")) {
            Intent intent = new Intent(mAppContext, FlutterWrapperActivity.class);
            intent.setData(Uri.parse(url));
            intent.putExtra("params", params);
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mAppContext.startActivity(intent);
            return true;
        }
        if (mNativeRouterHandler != null) {
            Class activityCls = mNativeRouterHandler.openUrlWithQueryAndParams(url, query, params);
            Intent intent = new Intent(mAppContext, activityCls);
            intent.setData(Uri.parse(url));
            intent.setAction(Intent.ACTION_VIEW);
            mAppContext.startActivity(intent);
        }
        return false;
    }
}

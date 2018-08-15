package com.taobao.hybridstackmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.util.HashMap;

public class XURLRouter {
    static String kOpenUrlPrefix = "hrd";
    static Context mAppContext;
    static XURLRouterHandler mNativeRouterHandler;
    public static void setAppContext(Context context){
        mAppContext = context;
    }
    public static void setNativeRouterHandler(XURLRouterHandler handler){
        mNativeRouterHandler = handler;
    }
    public static boolean openUrlWithQueryAndParams(String url, HashMap query, HashMap params){
        Uri tmpUri = Uri.parse(url);
        if(!kOpenUrlPrefix.equals(tmpUri.getScheme()))
            return false;
        if(query!=null && query.containsKey("flutter") && (Boolean) query.get("flutter")){
            Intent intent = new Intent(mAppContext,FlutterWrapperActivity.class);
            intent.setData(Uri.parse(url));
            intent.setAction(Intent.ACTION_VIEW);
            mAppContext.startActivity(intent);
            return true;
        }
        if(mNativeRouterHandler!=null) {
            Class activityCls =  mNativeRouterHandler.openUrlWithQueryAndParams(url, query, params);
            Intent intent = new Intent(mAppContext,activityCls);
            intent.setData(Uri.parse(url));
            intent.setAction(Intent.ACTION_VIEW);
            mAppContext.startActivity(intent);
        }
        return false;
    }
}

package com.taobao.hybridstackmanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.util.HashMap;

public class XURLRouter {
    final static String kOpenUrlPrefix = "hrd";
    static XURLRouter sRouterInst;
    Context mAppContext;
    XURLRouterHandler mNativeRouterHandler;
    public static XURLRouter sharedInstance(){
        if(sRouterInst==null){
            sRouterInst = new XURLRouter();
        }
        return sRouterInst;
    }
    public void setAppContext(Context context){
        mAppContext = context;
    }
    public void setNativeRouterHandler(XURLRouterHandler handler){
        mNativeRouterHandler = handler;
    }
    public boolean openUrlWithQueryAndParams(String url, HashMap query, HashMap params){
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

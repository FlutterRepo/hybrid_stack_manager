package com.taobao.hybridstackmanager;

import android.app.Activity;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * hybridstackmanager
 */
public class HybridStackManager implements MethodCallHandler {
    /**
     * Plugin registration.
     */
    private static HybridStackManager hybridStackManager = null;
    public MethodChannel methodChannel;
    public FlutterActivityChecker curFlutterActivity;
    public HashMap mainEntryParams;
    public HashMap deviceInfoParams;
    private static Registrar sRegister;

    public static HybridStackManager sharedInstance() {
        if (hybridStackManager != null) {
            return hybridStackManager;
        }
        hybridStackManager = new HybridStackManager();
        return hybridStackManager;
    }

    public static void registerWith(Registrar registrar) {
        hybridStackManager = HybridStackManager.sharedInstance();
        sRegister = registrar;
        hybridStackManager.methodChannel = new MethodChannel(registrar.messenger(), "hybrid_stack_manager");
        hybridStackManager.methodChannel.setMethodCallHandler(hybridStackManager);
    }

    public static HashMap assembleChanArgs(String url, HashMap query, HashMap params) {
        HashMap arguments = new HashMap();
        Uri uri = Uri.parse(url);
        String tmpUrl = String.format("%s://%s", uri.getScheme(), uri.getHost());
        HashMap tmpQuery = new HashMap();
        if (query != null) {
            tmpQuery.putAll(query);
        }
        for (String key : uri.getQueryParameterNames()) {
            tmpQuery.put(key, uri.getQueryParameter(key));
        }

        HashMap tmpParams = new HashMap();
        if (params != null) {
            tmpParams.putAll(params);
        }
        if (tmpUrl != null) {
            arguments.put("url", tmpUrl);
        }
        if (tmpQuery != null) {
            arguments.put("query", tmpQuery);
        }
        if (tmpParams != null) {
            arguments.put("params", tmpParams);
        }
        return arguments;
    }

    public static String concatUrl(String url, HashMap query, HashMap params) {
        // assert(params==null||params.size()==0);
        Uri uri = Uri.parse(url);
        Uri.Builder builder = uri.buildUpon();
        if (query != null) {
            for (Object key : query.keySet()) {
                Object value = query.get(key);
                if (value != null) {
                    builder.appendQueryParameter(String.valueOf(key), value.toString());
                }
            }
            for (Object key : params.keySet()) {
                Object value = params.get(key);
                if (value != null) {
                    builder.appendQueryParameter(String.valueOf(key), value.toString());
                }
            }
        }
        return builder.build().toString();
    }

    public void openUrlFromFlutter(String url, HashMap query, HashMap params) {
        if (HybridStackManager.sharedInstance().methodChannel == null) {
            hybridStackManager.methodChannel = new MethodChannel(sRegister.messenger(), "hybrid_stack_manager");
            hybridStackManager.methodChannel.setMethodCallHandler(hybridStackManager);
            System.out.println("=v= method channel is null");
        }
        HybridStackManager.sharedInstance().methodChannel.invokeMethod("openURLFromFlutter",
                assembleChanArgs(url, query, params));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("openUrlFromNative")) {
            HashMap openUrlInfo = (HashMap) call.arguments;
            String url = (String) openUrlInfo.get("url");
            XURLRouter.sRecentPages[0] = XURLRouter.sRecentPages[1];
            XURLRouter.sRecentPages[1] = url;
            HashMap query = (HashMap) openUrlInfo.get("query");
            HashMap params = (HashMap) openUrlInfo.get("params");
            String concatUrl = concatUrl(url, query, params);
            if (curFlutterActivity != null && curFlutterActivity.isActive()) {
                curFlutterActivity.openUrl(concatUrl);
            }
            result.success("OK");
        } else if (call.method.equals("getMainEntryParams")) {
            if (mainEntryParams == null) {
                mainEntryParams = new HashMap();
            }
            result.success(mainEntryParams);
        } else if (call.method.equals("updateCurFlutterRoute")) {
            String curRouteName = (String) call.arguments;
            if (curFlutterActivity != null && curFlutterActivity.isActive()) {
                curFlutterActivity.setCurFlutterRouteName(curRouteName);
            }
            ///SINGLE TASK模式 注意：未确定是否会影响生命值
            boolean foundInStack = false;
            String key = curRouteName.split("_")[0];
            List<Map.Entry<String, FlutterActivityChecker>> entryList = new ArrayList<>(XURLRouter.sActivityMap.entrySet());
            for (int i = 0; i < entryList.size() - 1; i++) {
//                if (originalSize-- < 1) return;
                String savedActivityName = (String) entryList.get(i).getKey();
                if (TextUtils.isEmpty(savedActivityName)) continue;
                if (!foundInStack && savedActivityName.startsWith(key)) {
                    foundInStack = true;
                }
                if (foundInStack) {
                    Activity activity = (Activity) entryList.get(i).getValue();
                    if (activity != null) activity.finish();
                    XURLRouter.sActivityMap.remove(savedActivityName);
                }
            }
            if (foundInStack) {
                result.success("OK");
                return;
            }
            result.success("OK");
        } else if (call.method.equals("popCurPage")) {
            if (curFlutterActivity != null && curFlutterActivity.isActive()) {
                curFlutterActivity.popCurActivity();
            }
            result.success("OK");
        } else if (call.method.equals("popDesignatedPage")) {
            String pageName = (String) call.arguments;
            //注意，1. 这里最初sActivityList长度比Activity要多一个，好像可以优化 2. 如果非要这样，好像可以用LinkedHashMap访问上一个结点
            for (int i = 0; i < XURLRouter.sPageUrlList.size(); i++) {
                if (XURLRouter.sPageUrlList.get(i).equals(pageName) && i + 1 < XURLRouter.sActivityList.size()) {
                    for (int j = XURLRouter.sActivityList.size(); j >= 0; j--) {
                        Activity activity = XURLRouter.sActivityList.get(i + 1);
                        if (activity != null) {
                            activity.finish();
                        }
                    }
                }
            }
        } else if (call.method.equals("closeDesignatedPageInstance")) {
            String pageName = (String) call.arguments;
            Activity activity = (Activity) XURLRouter.sActivityMap.get(pageName);
            if (activity != null) {
                activity.finish();
            }
        } else if (call.method.equals("associatePageNameWithActivity")) {
            String pageName = (String) call.arguments;
            XURLRouter.sActivityMap.put(pageName, curFlutterActivity);
            XURLRouter.sActivityToFlutterPageName.put(curFlutterActivity, pageName);
            Log.d("giaogiao", pageName);
        } else if (call.method.equals("registerOnBackPress")) {
            String pageName = (String) call.arguments;
            XURLRouter.sFlutterPageNameNeedingBlockOnBackPressed.add(pageName);
        } else if (call.method.equals("reusingMode")) {
            XURLRouter.sReusingMode = (Boolean) call.arguments;
        } else {
            result.notImplemented();
        }
    }
}

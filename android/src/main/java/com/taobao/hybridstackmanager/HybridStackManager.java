package com.taobao.hybridstackmanager;
import android.net.Uri;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.os.Build;
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

    public static HybridStackManager sharedInstance() {
        if (hybridStackManager != null) { return hybridStackManager; }
        hybridStackManager = new HybridStackManager();
        return hybridStackManager;
    }

    public static void registerWith(Registrar registrar) {
        hybridStackManager = HybridStackManager.sharedInstance();
        hybridStackManager.methodChannel = new MethodChannel(registrar.messenger(), "hybrid_stack_manager");
        hybridStackManager.methodChannel.setMethodCallHandler(hybridStackManager);
    }

    public static HashMap assembleChanArgs(String url, HashMap query, HashMap params) {
        HashMap arguments = new HashMap();
        Uri uri = Uri.parse(url);
        String tmpUrl = String.format("%s://%s", uri.getScheme(), uri.getHost());
        HashMap tmpQuery = new HashMap();
        if (query != null) { tmpQuery.putAll(query); }
        for (String key : uri.getQueryParameterNames()) {
            tmpQuery.put(key, uri.getQueryParameter(key));
        }

        HashMap tmpParams = new HashMap();
        if (params != null) { tmpParams.putAll(params); }
        if (tmpUrl != null) { arguments.put("url", tmpUrl); }
        if (tmpQuery != null) { arguments.put("query", tmpQuery); }
        if (tmpParams != null) { arguments.put("params", tmpParams); }
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
        HybridStackManager.sharedInstance().methodChannel.invokeMethod("openURLFromFlutter",
            assembleChanArgs(url, query, params));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("openUrlFromNative")) {
            if (curFlutterActivity != null && curFlutterActivity.isActive()) {
                HashMap openUrlInfo = (HashMap)call.arguments;
                String url = (String)openUrlInfo.get("url");
                HashMap query = (HashMap)openUrlInfo.get("query");
                HashMap params = (HashMap)openUrlInfo.get("params");
                String concatUrl = concatUrl(url, query, params);
                curFlutterActivity.openUrl(concatUrl);
            }
            result.success("OK");
        } else if (call.method.equals("getMainEntryParams")) {
            if (mainEntryParams == null) { mainEntryParams = new HashMap(); }
            result.success(mainEntryParams);
            //      mainEntryParams = null;
        } else if (call.method.equals("updateCurFlutterRoute")) {
            String curRouteName = (String)call.arguments;
            if (curFlutterActivity != null && curFlutterActivity.isActive()) {
                curFlutterActivity.setCurFlutterRouteName(curRouteName);
            }
            result.success("OK");
        } else if (call.method.equals("popCurPage")) {
            if (curFlutterActivity != null && curFlutterActivity.isActive()) {
                curFlutterActivity.popCurActivity();
            }
            result.success("OK");
        }else {
            result.notImplemented();
        }
    }
}

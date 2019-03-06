import 'dart:async';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:hybrid_stack_manager/hybrid_stack_manager_plugin.dart';

typedef Future<dynamic> MethodHandler(MethodCall call);

class StackManagerApis {
  static StackManagerApis singleton = new StackManagerApis._internal();
  MethodChannel _channel;
  MethodHandler _handler;

  void setMethodCallHandler(MethodHandler hdler) {
    _handler = hdler;
    _channel.setMethodCallHandler(_handler);
  }

  StackManagerApis._internal() {
    _channel = new MethodChannel('hybrid_stack_manager');
  }

  openUrlFromNative({String url, Map query, Map params, bool animated}) {
    print("=v= openUrlFromNative, current stack size: ${Router.singleton.buildContextStack.length}");

    ///打开native页面时关掉reusing mode，否则native可能无法返回
    _channel.invokeMethod("reusingMode", false);
    _channel.invokeMethod("openUrlFromNative", {"url": url ?? "", "query": (query ?? {}), "params": (params ?? {}), "animated": animated ?? true});
  }

  ///在原有的Native页面上添加Flutter页面
  openFlutterPageDirectly(BuildContext context, Widget page) {
    ///更新最新的flutter页面的context
    Router.singleton.buildContextStack.addLast(context);
    if (Router.singleton.buildContextStack.length > 0) {
      ///如果flutter页面数量大于0个（注意，Native页面中的第一个Flutter页面的context不存入stack），开启复用模式，保证android拦截返回键
      _channel.invokeMethod("reusingMode", true);
    }
    _channel.invokeMethod("updateCurNativeFlutterStackSize", {"pageName": Utils.lastGeneratedPageName, "stackSize": Router.singleton.buildContextStack.length});
    print("=v= adding page, current stack size: ${Router.singleton.buildContextStack.length}");
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => page,
      ),
    ).then((result) {
      print("=v= result from popped page: $result");
    });
  }

  ///在原有的Native页面上直接返回上一个flutter页面，Android物理返回键也调用这个
  popFlutterPageDirectly() {
    Navigator.pop(Router.singleton.buildContextStack.removeLast(), "携带的参数");
    _channel.invokeMethod("updateCurNativeFlutterStackSize", {"pageName": Utils.lastGeneratedPageName, "stackSize": Router.singleton.buildContextStack.length});
    print("=v= poping page, current stack size: ${Router.singleton.buildContextStack.length}");

    ///如果flutter页面数量小于等于0个，让android接管返回键
    if (Router.singleton.buildContextStack.length <= 0) {
      _channel.invokeMethod("reusingMode", false);
    }
  }

  popDesignatedPage(String pageName) {
    _channel.invokeMethod("popDesignatedPage", pageName);
  }

  closeDesignatedPageInstance(String pageName) {
    _channel.invokeMethod("closeDesignatedPageInstance", pageName);
  }

  associatePageNameWithActivity(String pageName) {
    _channel.invokeMethod("associatePageNameWithActivity", pageName);
  }

  popCurPage({bool animated = true}) {
    _channel.invokeMethod("popCurPage", animated);
  }

  updateCurFlutterRoute(String curRouteName) {
    _channel.invokeMethod("updateCurFlutterRoute", curRouteName ?? "");
  }

  ///参数是当前页面的instance名称
  registerOnBackPress(String curRouteName, VoidCallback callback) {
    _channel.invokeMethod("registerOnBackPress", curRouteName ?? "");
    print("=v= giao1");
    Router.singleton.onBackPressedMap[curRouteName] = callback;
    print("=v= giao2");
  }

  Future<Map> getMainEntryParams() async {
    dynamic info = await _channel.invokeMethod("getMainEntryParams");
    return new Future.sync(() => info as Map);
  }
}

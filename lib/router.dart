import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'router_option.dart';
import 'hybrid_stack_manager.dart';
import 'utils.dart';

typedef Widget FlutterWidgetHandler({RouterOption routeOption, Key key});

class XMaterialPageRoute<T> extends MaterialPageRoute<T> {
  final WidgetBuilder builder;
  final bool animated;

  Duration get transitionDuration {
    if (animated == true) return const Duration(milliseconds: 300);
    return const Duration(milliseconds: 0);
  }

  XMaterialPageRoute({
    this.builder,
    this.animated,
    RouteSettings settings: const RouteSettings(),
  }) : super(builder: builder, settings: settings);
}

class Router extends Object {
  static final Router singleton = new Router._internal();
  List<XMaterialPageRoute> flutterRootPageNameLst = new List();
  Map onBackPressedMap = {};
  String currentPageUrl = null;
  FlutterWidgetHandler routerWidgetHandler;
  GlobalKey globalKeyForRouter;
  BuildContext latestBuildContext;
  Queue<BuildContext> buildContextStack = new Queue();

  static Router sharedInstance() {
    return singleton;
  }

  Map<String, VoidCallback> onResumeCallbacksMap = {};

  registerOnResumeCallback(String routeName, VoidCallback callback) {
    onResumeCallbacksMap[routeName] = callback;
  }

  Router._internal() {
    HybridStackManagerPlugin.hybridStackManagerPlugin.setMethodCallHandler((MethodCall methodCall) {
      String method = methodCall.method;
      if (method == "openURLFromFlutter") {
        Map args = methodCall.arguments;
        if (args != null) {
          bool animated = (args["animated"] == 1);
          Router.sharedInstance().pushPageWithOptionsFromFlutter(
              routeOption: new RouterOption(url: args["url"], query: args["query"], params: args["params"]), animated: animated ?? false);
        }
      } else if (method == "popToRoot") {
        Router.sharedInstance().popToRoot();
      } else if (method == "popToRouteNamed") {
        Router.sharedInstance().popToRouteNamed(methodCall.arguments);
        print("=V= methodCall.arguments ${methodCall.arguments}");
      } else if (method == "popRouteNamed") {
        Router.sharedInstance().popRouteNamed(methodCall.arguments);
      } else if (method == "pleaseHandleOnBackPressed") {
        print("=v= giao3333");
        VoidCallback onBackPressedCallback = onBackPressedMap[methodCall.arguments];
        print("=v= giao3${onBackPressedCallback.runtimeType}");
        if (onBackPressedCallback is VoidCallback) {
          print("=v= giao4");
          onBackPressedCallback();
        }
      } else if (method == "reusingModeOnBackPressed") {
        HybridStackManagerPlugin.hybridStackManagerPlugin.popFlutterPageDirectly();
      }
    });
  }

  popToRoot() {
    NavigatorState navState = Navigator.of(globalKeyForRouter.currentContext);
    List<Route<dynamic>> navHistory = navState.history;
    int histLen = navHistory.length;
    for (int i = histLen - 1; i >= 1; i--) {
      Route route = navHistory.elementAt(i);
      navState.removeRoute(route);
    }
  }

  popToRouteNamed(String routeName) {
    NavigatorState navState = Navigator.of(globalKeyForRouter.currentContext);
    List<Route<dynamic>> navHistory = navState.history;
    int histLen = navHistory.length;
    for (int i = histLen - 1; i >= 1; i--) {
      Route route = navHistory.elementAt(i);
      if (!(route is XMaterialPageRoute) || ((route as XMaterialPageRoute).settings.name != routeName)) {
        print("removing route(popToRouteNamed) --- : $routeName");
        navState.removeRoute(route);
      }
      if ((route is XMaterialPageRoute) && (route.settings.name == routeName)) break;
    }
    String newTopRouteName = (navHistory.elementAt(navHistory.length - 1) as XMaterialPageRoute).settings.name;
    print("now stack top page is $newTopRouteName.");
    VoidCallback callback = onResumeCallbacksMap[newTopRouteName];
    if (callback is VoidCallback) {
      print(newTopRouteName + " is callbacking now.");
      callback();
    }
  }

  popRouteNamed(String routeName) {
    NavigatorState navState = Navigator.of(globalKeyForRouter.currentContext);
    List<Route<dynamic>> navHistory = navState.history;
    int histLen = navHistory.length;
    for (int i = histLen - 1; i >= 1; i--) {
      Route route = navHistory.elementAt(i);
      if ((route is XMaterialPageRoute) && (route.settings.name == routeName)) {
        navState.removeRoute(route);
        break;
      }
    }
  }

  pushPageWithOptionsFromFlutter({RouterOption routeOption, bool animated}) {
    Widget page = Router.sharedInstance().pageFromOption(routeOption: routeOption);
    if (page != null) {
      XMaterialPageRoute pageRoute = new XMaterialPageRoute(
          settings: new RouteSettings(name: routeOption.userInfo),
          animated: animated,
          builder: (BuildContext context) {
            return page;
          });

      Navigator.of(globalKeyForRouter.currentContext).push(pageRoute);
      HybridStackManagerPlugin.hybridStackManagerPlugin.updateCurFlutterRoute(routeOption.userInfo);
    } else {
      HybridStackManagerPlugin.hybridStackManagerPlugin.openUrlFromNative(url: routeOption.url, query: routeOption.query, params: routeOption.params);
    }
    NavigatorState navState = Navigator.of(globalKeyForRouter.currentContext);
    List<Route<dynamic>> navHistory = navState.history;
    String name = routeOption.userInfo.split("_")[0];
    print("======(((" + navHistory.length.toString());
    if (name != "/") {
      bool hasSamePage = false;
      navHistory.forEach((f) {
        print(f.settings.name);
      });
      for (var i = 0; i < navHistory.length - 1; i++) {
        String currentName = navHistory[i].settings.name.split("_")[0];
        if (hasSamePage || name == currentName) {
          hasSamePage = true;
          Route route = navHistory.elementAt(i);
          navState.removeRoute(route);
          i--;
        }
      }
      print("======(((" + navHistory.length.toString());
    }
  }

  pushPageWithOptionsFromNative({RouterOption routeOption, bool animated}) {
    HybridStackManagerPlugin.hybridStackManagerPlugin
        .openUrlFromNative(url: routeOption.url, query: routeOption.query, params: routeOption.params, animated: animated);
  }

  pageFromOption({RouterOption routeOption, Key key}) {
    try {
      currentPageUrl = routeOption.url + "?" + convertUrl(routeOption.query);
    } catch (e) {}
    routeOption.userInfo = Utils.generateUniquePageName(routeOption.url);
    HybridStackManagerPlugin.hybridStackManagerPlugin.associatePageNameWithActivity(routeOption.userInfo);
    if (routerWidgetHandler != null) return routerWidgetHandler(routeOption: routeOption, key: key);
  }

  static String convertUrl(Map query) {
    String tmpUrl = "";
    if (query != null) {
      bool skipfirst = true;
      query.forEach((key, value) {
        if (skipfirst) {
          skipfirst = false;
        } else {
          tmpUrl = tmpUrl + "&";
        }
        tmpUrl = tmpUrl + (key + "=" + value.toString());
      });
    }
    return Uri.encodeFull(tmpUrl);
  }
}

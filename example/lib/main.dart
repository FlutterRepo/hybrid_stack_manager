import 'package:flutter/material.dart';
import 'package:hybrid_stack_manager/hybrid_stack_manager_plugin.dart';

import 'app_config.dart';
import 'my_app.dart';

void main() async {
  AppConfig.sharedInstance();
  StackManagerApis plugin = StackManagerApis.singleton;
  Map args = await plugin.getMainEntryParams();
  runApp(new MyApp());
  if (args != null && args["url"] != null) {
    RouterOption routeOption = new RouterOption(url: args["url"], query: args["query"], params: args["params"]);
    Router.sharedInstance().pushPageWithOptionsFromFlutter(routeOption: routeOption, animated: false);
  }
}

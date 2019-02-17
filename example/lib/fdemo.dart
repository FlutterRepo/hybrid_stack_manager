import 'package:flutter/material.dart';
import 'package:hybrid_stack_manager/hybrid_stack_manager_plugin.dart';

class FDemoWidget extends StatelessWidget {
  RouterOption routeOption;
  String pageName;

  FDemoWidget({String pageName, RouterOption option, Key key}) : super(key: key) {
    this.routeOption = option;
    this.pageName = pageName;
  }

  Widget build(BuildContext context) {
    Map map;
    if (routeOption != null && routeOption.userInfo != null) {
      map = Utils.parseUniquePageName(routeOption.userInfo);
    }
    return new Scaffold(
        appBar: new AppBar(
          leading: new GestureDetector(
              child: new Icon(Icons.arrow_back),
              onTap: () {
                HybridStackManagerPlugin.hybridStackManagerPlugin.popCurPage();
              }),
          title: new Text(map == null ? pageName : "Individual Native(${map["id"]})"),
        ),
        body: Container(
          width: MediaQuery.of(context).size.width,
          child: new Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              new InkWell(
                child: new Text(
                  "Click to open reused FlutterPage\n(在原有的Native页面装载flutter页面)",
                  textAlign: TextAlign.center,
                ),
                onTap: () {
                  HybridStackManagerPlugin.hybridStackManagerPlugin
                      .openFlutterPageDirectly(context, FDemoWidget(pageName: Utils.generateUniquePageName("Reused Native")));
                },
              ),
              new InkWell(
                child: new Text("Click to open new native FlutterPage\n(打开一个单独的Native页面装载flutter页面)", textAlign: TextAlign.center),
                onTap: () {
                  HybridStackManagerPlugin.hybridStackManagerPlugin.openUrlFromNative(url: "hrd://fdemo", query: {"flutter": true});
                },
              ),
              new InkWell(
                child: new Text("Click to open NativePage\n(打开Native页面)", textAlign: TextAlign.center),
                onTap: () {
                  HybridStackManagerPlugin.hybridStackManagerPlugin.openUrlFromNative(url: "hrd://ndemo");
                },
              )
            ],
          ),
        ),
        floatingActionButton: null // This trailing comma makes auto-formatting nicer for build methods.
        );
  }
}

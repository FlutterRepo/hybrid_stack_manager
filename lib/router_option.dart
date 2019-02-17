import 'dart:convert';

class RouterOption {
  String url;
  Map query;
  Map params;
  ///目前，userInfo中保存的是hrd://WordListPage_101这样的页面实例名称，由generateUniquePageName()生成
  String userInfo;

  RouterOption({this.url, this.query, this.params});

  String toJson() {
    return json.encode({"url": url, "query": query, "params": params, "userInfo": userInfo});
  }
}

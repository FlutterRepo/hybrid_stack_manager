import 'dart:async';
import 'package:flutter/services.dart';


typedef  Future<dynamic> MethodHandler(MethodCall call);

class HybridStackManagerPlugin {
  static HybridStackManagerPlugin hybridStackManagerPlugin = new HybridStackManagerPlugin._internal();
  MethodChannel _channel;
  MethodHandler _handler;
  void setMethodCallHandler(MethodHandler hdler){
    _handler = hdler;
    _channel.setMethodCallHandler(_handler);
  }
  HybridStackManagerPlugin._internal(){
    _channel = new MethodChannel('hybrid_stack_manager');
  }
  openUrlFromNative({String url,Map query,Map params,bool animated}){
    _channel.invokeMethod("openUrlFromNative",{"url":url??"","query":(query??{}),"params":(params??{}),"animated":animated??true});
  }
  popCurPage({bool animated = true}){
    _channel.invokeMethod("popCurPage",animated);
  }
  updateCurFlutterRoute(String curRouteName){
    _channel.invokeMethod("updateCurFlutterRoute",curRouteName??"");
  }
  Future<Map> getMainEntryParams()async{
    dynamic info = await _channel.invokeMethod("getMainEntryParams");
    return new Future.sync(()=>info as Map);
  }
}

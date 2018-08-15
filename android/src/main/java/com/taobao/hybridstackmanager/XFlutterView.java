// Copyright 2013 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
package com.taobao.hybridstackmanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.util.AttributeSet;

import java.lang.reflect.Field;
import java.util.List;

import io.flutter.plugin.common.ActivityLifecycleListener;
import io.flutter.plugin.common.JSONMethodCodec;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformPlugin;
import io.flutter.view.FlutterNativeView;
import io.flutter.view.FlutterView;

/**
 * An Android view containing a Flutter app.
 */
public class XFlutterView extends FlutterView
        {
            public XFlutterView(Context context, AttributeSet attrs, FlutterNativeView nativeView){
                super(context,attrs,nativeView);
            }
            public void registerReceiver() {
                try {
                    //Register Receiver
                    Field privateField0 = FlutterView.class.getDeclaredField("mDiscoveryReceiver");
                    privateField0.setAccessible(true);
                    BroadcastReceiver mDiscoveryReceiver = (BroadcastReceiver) privateField0.get(this);
                    if (mDiscoveryReceiver != null) {
                        if ((getContext().getApplicationInfo().flags & 2) != 0 && mDiscoveryReceiver != null) {
                            getContext().registerReceiver(mDiscoveryReceiver, new IntentFilter("io.flutter.view.DISCOVER"));
                        }
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            public void unregisterReceiver(){
                try {
                    //UnRegister Receiver
                    Field privateField0 = FlutterView.class.getDeclaredField("mDiscoveryReceiver");
                    privateField0.setAccessible(true);
                    BroadcastReceiver mDiscoveryReceiver = (BroadcastReceiver)privateField0.get(this);
                    if (mDiscoveryReceiver != null) {
                        this.getContext().unregisterReceiver(mDiscoveryReceiver);
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            public void resetActivity(Activity activity){
                try {
                    Field privateField1 = FlutterView.class.getDeclaredField("mNativeView");
                    privateField1.setAccessible(true);
                    FlutterNativeView mNativeView = (FlutterNativeView)privateField1.get(this);
                    mNativeView.attachViewAndActivity(this, activity);

                    Field privateField2 = FlutterView.class.getDeclaredField("mActivityLifecycleListeners");
                    privateField2.setAccessible(true);
                    List<ActivityLifecycleListener> mActivityLifecycleListener = (List<ActivityLifecycleListener>)privateField2.get(this);
                    mActivityLifecycleListener.clear();
                    PlatformPlugin platformPlugin = new PlatformPlugin(activity);
                    MethodChannel flutterPlatformChannel = new MethodChannel(this, "flutter/platform", JSONMethodCodec.INSTANCE);
                    flutterPlatformChannel.setMethodCallHandler(platformPlugin);
                    addActivityLifecycleListener(platformPlugin);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
}

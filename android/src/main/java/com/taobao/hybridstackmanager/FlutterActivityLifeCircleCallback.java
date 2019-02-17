package com.taobao.hybridstackmanager;

import android.content.Intent;

/**
 * Created by warner on 2018/12/20.
 */
public interface FlutterActivityLifeCircleCallback {

    void onActivityResult(int requestCode, int resultCode, Intent data);
}

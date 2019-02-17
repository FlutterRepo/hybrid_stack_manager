package com.taobao.hybridstackmanager;
import android.os.Build;

public class FlutterUtils {
    public static boolean isSupportFlutter(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            //Flutter supports android with apilevel 16 and above.
            return true;
        } else{
            return false;
        }
    }
}

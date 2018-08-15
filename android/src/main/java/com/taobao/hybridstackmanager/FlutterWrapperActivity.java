package com.taobao.hybridstackmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.taobao.hybridstackmanager.XFlutterActivityDelegate.ViewFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.view.FlutterNativeView;
import io.flutter.view.FlutterView;


public class FlutterWrapperActivity extends Activity implements PluginRegistry,ViewFactory,FlutterView.Provider, FlutterActivityChecker {
    private static XFlutterActivityDelegate delegate;
    private static XFlutterView flutterView;
    private static FlutterNativeView nativeView;
    private XFlutterActivityDelegate eventDelegate;
    private PluginRegistry pluginRegistry;
    private boolean isActive;
    private String curFlutterRouteName;
    private static int flutterWrapperInstCnt=0;
    //Flutter Activity Related Work
    private ImageView fakeSnapImgView;
    private Bitmap lastbitmap;
    private String pageName = "";
    private HashMap spm = null;

    public void setPageName(String pageName){
        this.pageName = pageName;
    }

    public void setSpm(HashMap spm){
        this.spm = spm;
    }

    /**
     * Returns the Flutter view used by this activity; will be null before
     * {@link #onCreate(Bundle)} is called.
     */
    @Override
    public XFlutterView getFlutterView() {
        return createFlutterView(this);
    }

    /**
     * Hook for subclasses to customize the creation of the
     * {@code FlutterView}.
     *
     * <p>The default implementation returns {@code null}, which will cause the
     * activity to use a newly instantiated full-screen view.</p>
     */
    @Override
    public XFlutterView createFlutterView(Context context) {
        if(flutterView!=null)
            return flutterView;
        flutterView = new XFlutterView(this,null,createFlutterNativeView());
        return flutterView;
    }

    /**
     * Hook for subclasses to customize the creation of the
     * {@code FlutterNativeView}.
     *
     * <p>The default implementation returns {@code null}, which will cause the
     * activity to use a newly instantiated native view object.</p>
     */
    @Override
    public FlutterNativeView createFlutterNativeView() {
        if(nativeView!=null)
            return nativeView;
        nativeView = new FlutterNativeView(this.getApplicationContext());
        return nativeView;
    }

    private boolean isFlutterViewAttachedOnMe(){
        FrameLayout rootView = (FrameLayout) findViewById(R.id.flutter_rootview);
        XFlutterView flutterView = getFlutterView();
        ViewGroup priorParent = (ViewGroup) flutterView.getParent();
        return rootView == priorParent;
    }

    @Override
    public final boolean hasPlugin(String key) {
        return pluginRegistry.hasPlugin(key);
    }

    @Override
    public final <T> T valuePublishedByPlugin(String pluginKey) {
        return pluginRegistry.valuePublishedByPlugin(pluginKey);
    }
    @Override
    public final Registrar registrarFor(String pluginKey) {
        return pluginRegistry.registrarFor(pluginKey);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean firstLaunch = (nativeView==null?true:false);

        super.onCreate(savedInstanceState);
        checkIfInitActivityDelegate();
        eventDelegate.onCreate(savedInstanceState);

        if(firstLaunch){
            eventDelegate.runFlutterBundle();
            Class<?> c = null;
            try {
                c = Class.forName("io.flutter.plugins.GeneratedPluginRegistrant");
                Method method = c.getMethod("registerWith",PluginRegistry.class);
                method.invoke(null,pluginRegistry);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                flutterView.registerReceiver();
            }catch (Exception e){
                Log.e( "FlutterWrapperActivity ","onCreate flutterView.registerReceiver error" );
            }
        }
        setContentView(R.layout.flutter_layout);
        checkIfAddFlutterView();
        fakeSnapImgView = (ImageView) findViewById(R.id.flutter_snap_imageview);
        fakeSnapImgView.setVisibility(View.GONE);
        //Process Intent Extra
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Uri uri = intent.getData();
        if(uri!=null){
            HybridStackManager.sharedInstance().openUrlFromFlutter(uri.toString(),null,null);
        }
        else if(bundle!=null){
            HybridStackManager.sharedInstance().openUrlFromFlutter(intent.getStringExtra("url"),(HashMap)intent.getSerializableExtra("query"),(HashMap)intent.getSerializableExtra("params"));
        }
        flutterWrapperInstCnt++;
    }


    @Override
    protected void onResume() {
        super.onResume();
        fakeSnapImgView.setVisibility(View.GONE);
        checkIfAddFlutterView();
        if(isFlutterViewAttachedOnMe())
            eventDelegate.onResume();
        HybridStackManager.sharedInstance().curFlutterActivity = this;
        isActive = true;
        if(lastbitmap!=null && curFlutterRouteName!=null && curFlutterRouteName.length()>0 ){
            HybridStackManager.sharedInstance().methodChannel.invokeMethod("popToRouteNamed",curFlutterRouteName);
        }
    }

    private void destorybitmap() {
        if (lastbitmap != null && !lastbitmap.isRecycled()) {
            lastbitmap.recycle();
            lastbitmap = null;
        }
        fakeSnapImgView.setImageBitmap(null);
    }

    @Override
    protected void onDestroy() {
//        eventDelegate.onDestroy();
        flutterWrapperInstCnt--;
        if(flutterWrapperInstCnt==0){
            HybridStackManager.sharedInstance().methodChannel.invokeMethod("popToRoot",null);
        }
        isActive = false;
        try {
            destorybitmap();
            flutterView.unregisterReceiver();
        }catch (Exception e){
            Log.e( "FlutterWrapperActivity ","onDestroy flutterView.unregisterReceiver error" );
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        popCurActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFlutterViewAttachedOnMe())
            eventDelegate.onPause();
        isActive = false;
    }

    @Override
    protected void onStart(){
        super.onStart();
        eventDelegate.onStart();
        HybridStackManager.sharedInstance().curFlutterActivity = this;
        isActive = true;
    }

    @Override
    protected void onStop() {
        FrameLayout rootView = (FrameLayout) findViewById(R.id.flutter_rootview);
        XFlutterView flutterView = getFlutterView();
        ViewGroup priorParent = (ViewGroup) flutterView.getParent();
        if(isFlutterViewAttachedOnMe())
            eventDelegate.onStop();
        super.onStop();
        if(super.isFinishing()){
            HybridStackManager.sharedInstance().methodChannel.invokeMethod("popRouteNamed",curFlutterRouteName);
            if (priorParent == rootView) {
                priorParent.removeView(flutterView);
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(isFlutterViewAttachedOnMe())
            eventDelegate.onPostResume();
    }

    // @Override - added in API level 23
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(isFlutterViewAttachedOnMe())
            eventDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!(isFlutterViewAttachedOnMe() && eventDelegate.onActivityResult(requestCode, resultCode, data))){
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if(isFlutterViewAttachedOnMe())
            eventDelegate.onNewIntent(intent);
    }

    @Override
    public void onUserLeaveHint() {
        if(isFlutterViewAttachedOnMe())
            eventDelegate.onUserLeaveHint();
    }

    @Override
    public void onTrimMemory(int level) {
        if(isFlutterViewAttachedOnMe())
            eventDelegate.onTrimMemory(level);
    }

    @Override
    public void onLowMemory() {
        if(isFlutterViewAttachedOnMe())
            eventDelegate.onLowMemory();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(isFlutterViewAttachedOnMe())
            eventDelegate.onConfigurationChanged(newConfig);
    }

    //ActivityDelegate Related
    void checkIfInitActivityDelegate(){
        if(nativeView==null){
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            Uri uri = intent.getData();
            HashMap arguments = new HashMap();
            if(uri!=null){
                arguments = HybridStackManager.assembleChanArgs(uri.toString(),null,null);
            }
            else if(bundle!=null){
                arguments = HybridStackManager.assembleChanArgs(intent.getStringExtra("url"),(HashMap)intent.getSerializableExtra("query"),(HashMap)intent.getSerializableExtra("params"));
           }
            HybridStackManager.sharedInstance().mainEntryParams = arguments;
        }
        if(delegate == null) {
            delegate = new XFlutterActivityDelegate(this, this);
        }
        else {
            delegate.resetActivity(this);
        }
        eventDelegate = delegate;
        pluginRegistry = delegate;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    public void openUrl(String url) {
        HybridStackManager.sharedInstance().curFlutterActivity = null;
        if(url.contains("flutter=true")){
            Intent intent = new Intent(FlutterWrapperActivity.this, FlutterWrapperActivity.class);
            intent.setAction(Intent.ACTION_RUN);
            intent.setData(Uri.parse(url));
            this.innerStartActivity(intent,true);
        }
        else{
            Uri tmpUri = Uri.parse(url);
            String tmpUrl = String.format("%s://%s",tmpUri.getScheme(),tmpUri.getHost());
            HashMap query = new HashMap();
            for(String key : tmpUri.getQueryParameterNames()){
                query.put(key,tmpUri.getQueryParameter(key));
            }
            XURLRouter.openUrlWithQueryAndParams(tmpUrl,query,null);
            saveFinishSnapshot(false);
        }
    }

    public void innerStartActivity(Intent intent,boolean showSnapshot){
        this.startActivity(intent);
        saveFinishSnapshot(showSnapshot);
    }


    @Override
    public void setCurFlutterRouteName(String curFlutterRouteName){
        this.curFlutterRouteName = curFlutterRouteName;
    }

    @Override
    public void popCurActivity() {
        finish();
        saveFinishSnapshot(true);
    }

    //Flutter View Related Logic
    void checkIfAddFlutterView() {
        final FrameLayout rootView = (FrameLayout) findViewById(R.id.flutter_rootview);
        final XFlutterView flutterView = getFlutterView();
        ViewGroup priorParent = (ViewGroup) flutterView.getParent();
        if (priorParent == rootView) {
            return;
        }
        final ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        final FlutterWrapperActivity activity = this;
        if (priorParent != null) {
            priorParent.removeView(flutterView);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after delay of 20ms
                    if (flutterView.getParent() == null && activity.isActive==true ) {
                        rootView.addView(flutterView, params);
                        flutterView.resetActivity(activity);
                    }
                }
            }, 20);
        }
        else{
            rootView.addView(flutterView, params);
            flutterView.resetActivity(activity);
        }
    }

    void saveFinishSnapshot(boolean showSnapshot){
        XFlutterView fv = getFlutterView();
        lastbitmap = fv.getBitmap();
        fakeSnapImgView.setImageBitmap(lastbitmap);
        if(showSnapshot)
            fakeSnapImgView.setVisibility(View.VISIBLE);
    }
}

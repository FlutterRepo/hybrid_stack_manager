package com.example.hybridstackmanagerexample;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.taobao.hybridstackmanager.*;

import java.util.HashMap;

public class MainActivity extends Activity implements XURLRouterHandler {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    XURLRouter.sharedInstance().setAppContext(getApplicationContext());
    setContentView(R.layout.placeholder);
    setTitle("Native Root Page");
    setupOperationBtns();
    setupNativeOpenUrlHandler();
  }

  void setupOperationBtns(){
    LinearLayout layout = findViewById(R.id.native_root);
    final Button btn=new Button(this);
    btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    btn.setText("Click to jump Flutter");
    btn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        HashMap<String,Object> m = new HashMap<String,Object>();
        m.put("flutter",true);
        XURLRouter.sharedInstance().openUrlWithQueryAndParams("hrd://fdemo",m,null);

      }
    });
    layout.addView(btn);
  }
  void setupNativeOpenUrlHandler(){
    XURLRouter.sharedInstance().setNativeRouterHandler(this);
  }
  public Class openUrlWithQueryAndParams(String url, HashMap query, HashMap params){
    Uri tmpUri = Uri.parse(url);
    if("ndemo".equals(tmpUri.getHost())){
      return XDemoActivity.class;
    }
    return null;
  }
}

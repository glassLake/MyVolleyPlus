package com.hss01248.myvolleyplus;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hss01248.myvolleyplus.volley.MyVolleyUtils;
import com.hss01248.myvolleyplus.wrapper.MyNetCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    @Bind(R.id.button)
    Button mButton;
    @Bind(R.id.button2)
    Button mButton2;
    @Bind(R.id.button3)
    Button mButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.button, R.id.button2, R.id.button3})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                MyVolleyUtils.getInstance(this).getString("https://www.baidu.com/", null, "dd", new MyNetCallback() {
                    @Override
                    public void onSuccess(Object response, String resonseStr) {
                        Log.e("baidu",response.toString());
                    }
                });
                break;
            case R.id.button2:
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", "145");
                MyVolleyUtils.getInstance(this).postJsonRequest("http://www.qxinli.com:9001/api/voice/detail/v1.json", map, "kk", new MyNetCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response, String resonseStr) {
                        Log.e("postJsonRequest","onSuccess");
                    }

                    @Override
                    public void onSuccess(JSONObject response, String responseStr, String data, int code, String msg) {
                        super.onSuccess(response, responseStr, data, code, msg);
                        Log.e("postJsonRequest","onSuccess long "+ "code:"+code + "--msg:"+ msg + "--data:"+data);
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                    }
                });
                break;
            case R.id.button3:
                File file = new File(Environment.getExternalStorageDirectory(),"qxinli.apk");
                if (!file.exists()){
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String path = file.getAbsolutePath();
                MyVolleyUtils.getInstance(this).download("http://www.qxinli.com/download/qxinli.apk", path, new MyNetCallback<String>() {
                    @Override
                    public void onSuccess(String response, String resonseStr) {
                        Log.e("download","onSuccess:"+ response);
                    }

                    @Override
                    public void onProgressChange(long fileSize, long downloadedSize) {
                        super.onProgressChange(fileSize, downloadedSize);
                        Log.e("download","onProgressChange:"+downloadedSize+ "--totalsize:"+ fileSize);
                    }

                    @Override
                    public void onError(String error) {
                        super.onError(error);
                        Log.e("download","error:"+error);
                    }
                });
                break;
        }
    }
}

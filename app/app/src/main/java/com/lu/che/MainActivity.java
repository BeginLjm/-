package com.lu.che;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends Activity implements View.OnClickListener {

    String url = "";
    private WebView webView;
    private AlertDialog alertDialog;
    private ArrayList<String> select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.q).setOnClickListener(this);
        findViewById(R.id.h).setOnClickListener(this);
        findViewById(R.id.z).setOnClickListener(this);
        findViewById(R.id.y).setOnClickListener(this);
        findViewById(R.id.s).setOnClickListener(this);
        webView = (WebView) findViewById(R.id.web);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            ArrayList<String> connectIp = getConnectIp();
            if (connectIp.size() == 0 || connectIp.size() == 1) {
                Toast.makeText(this, "链接异常", Toast.LENGTH_SHORT).show();
                //没有
            } else if (connectIp.size() == 2) {
                url = "http://" + connectIp.get(1) + ":5000";
            } else {
                //二选一
                this.select = connectIp;
                showSelectYearDialog();
            }
        } catch (Exception e) {
            Toast.makeText(this, "链接异常", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

        });
    }

    private void showSelectYearDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = View.inflate(MainActivity.this, R.layout.select_dialog, null);
        ListView lvSelect = (ListView) view.findViewById(R.id.lv_select);
        SelectAdapter selectAdapter = new SelectAdapter(this, select);
        lvSelect.setAdapter(selectAdapter);

        lvSelect.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                url = "http://" + select.get(position) + ":5000";
                Log.d("url", url);
                webView.loadUrl(url);
                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();
    }

    private ArrayList<String> getConnectIp() throws Exception {
        ArrayList<String> connectIpList = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader("/proc/net/arp"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] splitted = line.split(" +");
            if (splitted != null && splitted.length >= 4) {
                String ip = splitted[0];
                connectIpList.add(ip);
            }
        }
        return connectIpList;
    }

    private void get(final String url) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.q:
                get(url + "/q");
                break;
            case R.id.h:
                get(url + "/h");
                break;
            case R.id.z:
                get(url + "/z");
                break;
            case R.id.y:
                get(url + "/y");
                break;
            case R.id.s:
                get(url + "/s");
                break;
        }
    }
}

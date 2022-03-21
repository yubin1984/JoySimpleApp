package com.yubin.simpleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.yubin.simpleapp.net.HttpRequestUtils;
import com.yubin.simpleapp.net.LoadDialog;
import com.yubin.simpleapp.net.NetworkCallback;
import com.yubin.simpleapp.service.DownLoadService;
import com.yubin.simpleapp.utils.PackageUtils;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private TextView tv1;
    private EditText et1;
    private String url = "xxxx";
    private String versionUrl = "xxxx";
    private String apkUrl = "xxxx";
    private AlertDialog updateDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = findViewById(R.id.tv1);
        et1 = findViewById(R.id.et1);

        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null) {
            String name = uri.getQueryParameter("name");
            tv1.setText("姓名：" + name);
//            getRequest(name);

        }

        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tv1.setText(editable);
                getRequest(editable.toString());
            }
        });
        
        
//        getVersion();
    }

    // 获取版本号更新信息（打开app获取）
    private void getVersion() {
        LoadDialog.show(this);
        HttpRequestUtils.getRequestAsyn(versionUrl, new HashMap<>(), new NetworkCallback() {
            @Override
            public void completed(String response) {
                if (isFinishing() || isDestroyed()) return;
//                UpdataVersionBean bean = new Gson().fromJson(response, UpdataVersionBean.class);
//                if (bean == null || bean.getData() == null) return;
                int versionResponse = 10;

                if (PackageUtils.getVersionCode(MainActivity.this) < versionResponse) {
                    showUpdataDialog();
                }

            }

            @Override
            public void failed(int httpStatusCode, String error) {
                LoadDialog.dismiss();
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * 显示更新弹窗
     */
    private void showUpdataDialog() {
        if (updateDialog == null) {
            updateDialog = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("版本升级")
                    .setMessage("最新版本：12").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DownLoadService.startActionFoo(MainActivity.this, apkUrl, "");
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            updateDialog.dismiss();
                        }
                    }).create();
        }

        updateDialog.show();
    }


    private void getRequest(String name) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        HttpRequestUtils.getRequestAsyn(url, map,
                new NetworkCallback() {
                    @Override
                    public void completed(String response) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }

                    }

                    @Override
                    public void failed(int httpStatusCode, String error) {
                        if (isFinishing() || isDestroyed()) {
                            return;
                        }
                    }
                });
    }
}
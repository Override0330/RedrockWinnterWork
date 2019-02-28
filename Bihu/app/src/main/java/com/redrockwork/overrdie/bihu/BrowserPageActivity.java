package com.redrockwork.overrdie.bihu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class BrowserPageActivity extends AppCompatActivity {
    private String url = "https://github.com/Override0330";
    private Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_page);


        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        ImageView imageView = findViewById(R.id.tv_browser);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView copy = findViewById(R.id.iv_browser_copy);
        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager mClipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData mClipData;
                mClipData = ClipData.newPlainText("url",url);
                mClipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(context,"已复制网站链接",Toast.LENGTH_SHORT).show();
            }
        });

        final ProgressBar progressBar = findViewById(R.id.pb_browser);
        WebView webView = findViewById(R.id.wv_browser);
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress==100){
                    progressBar.setVisibility(View.GONE);
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
        webView.loadUrl(url);


    }
}

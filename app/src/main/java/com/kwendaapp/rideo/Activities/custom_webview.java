package com.kwendaapp.rideo.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kwendaapp.rideo.Helper.CustomDialog;
import com.kwendaapp.rideo.Helper.SharedHelper;
import com.kwendaapp.rideo.R;

public class custom_webview extends AppCompatActivity {
    private WebView webView;
    private CustomDialog customDialog;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_webview);
        Intent mainintent = getIntent();

        customDialog = new CustomDialog(this);
        customDialog.show();

        webView = findViewById(R.id.livechatwebview);
        ImageView back = findViewById(R.id.backArrow);
        TextView custompagename = findViewById(R.id.custompagename);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);

        custompagename.setText(mainintent.getStringExtra("page_name"));
        webView.loadData(SharedHelper.getKey(this, mainintent.getStringExtra("page")), "text/html", "UTF-8");
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                if (customDialog.isShowing())
                    customDialog.dismiss();
            }
        });

        back.setOnClickListener(v -> onBackPressed());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        webView.clearHistory();
        finish();
    }
}

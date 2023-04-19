package com.electro.sbg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 777);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    webView.loadUrl("javascript:(function() {" +
                            "var parent = document.getElementsByTagName('head').item(0);" +
                            "let scriptElements = parent.getElementsByTagName('script');" +
                            "let scripts = [" +
                            "'https://stronghold.openkrosskod.org/~electro/sbg-cui/sbg_custom_ui.js'," +
                            "'https://github.com/egorantonov/sbg-enhanced/releases/latest/download/index.js'," +
                            "'https://stronghold.openkrosskod.org/~electro/elscript/script.js'" +
                            "];" +
                            "let tf = false;" +
                            "for(let i = 0;i<scriptElements.length;i++) {" +
                            "if(scripts.includes(scriptElements[i].src)) {tf = true; break;}" +
                            "};" +
                            "if(tf) return;" +
                            "for(let i = 0;i<scripts.length;i++) {" +
                            "let script = document.createElement('script');" +
                            "script.type = 'text/javascript';" +
                            "script.setAttribute('src', scripts[i]);" +
                            "script.setAttribute('async', '');" +
                            "parent.appendChild(script);" +
                            "};" +
//                            "console.log(\"Cur. syte: \" + window.location.href)" +
                            "})()");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onPageFinished(view, url);
            }
            @SuppressLint("WebViewClientOnReceivedSslError")
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
                // super.onReceivedSslError(view, handler, error);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        if (savedInstanceState == null) webView.loadUrl("https://3d.sytes.net/");
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) webView.goBack();
        // else super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }
}
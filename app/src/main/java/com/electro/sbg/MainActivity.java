package com.electro.sbg;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.Manifest;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private WebView webView;

    void loadJS() {
        try {
            webView.loadUrl("javascript:(function() {" +
                    "function collectionContains(collection, url) {" +
                    "for (var i = 0; i < collection.length; i++) {" +
                    "if( collection[i].src == url ) {" +
                    "return true;" +
                    "}" +
                    "}" +
                    "return false;" +
                    "}" +
                    "" +
                    "var parent = document.getElementsByTagName('body').item(0);" +
                    "let scriptElements = parent.getElementsByTagName('script');" +
                    "let scripts = [" +
                    "'https://nicko-v.github.io/sbg-cui/index.min.js'," +
                    "'https://github.com/egorantonov/sbg-enhanced/releases/latest/download/index.js'," +
                    // "'https://stronghold.openkrosskod.org/~electro/elscript/script.js'" +
                    "];" +
                    "for(let i = 0;i<scripts.length;i++) {" +
                    "if(!collectionContains(scriptElements, scripts[i])) {" +
                    "let script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    "script.setAttribute('async', '');" +
                    "script.setAttribute('src', scripts[i]);" +
                    "parent.appendChild(script);" +
                    "}" +
                    "};" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
//            @Override
//            public void onPageFinished(WebView view, String url) {
//                loadJS();
//                super.onPageFinished(view, url);
//            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                loadJS();
//                return super.shouldOverrideUrlLoading(view, request);
//            }

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

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                // Загружает скрипты, когда видит напечатанную версию в консоли
                if(Pattern.matches("^(\\d+\\.)(\\d+\\.)(\\*|\\d+)$", consoleMessage.message())) loadJS();
                return super.onConsoleMessage(consoleMessage);
            }
        });

        if (savedInstanceState == null) webView.loadUrl("https://3d.sytes.net/");
    }

    @Override
    public void onBackPressed() {
        webView.loadUrl("javascript:document.dispatchEvent(new Event(\"backbutton\"))");
        // if(webView.canGoBack()) webView.goBack();
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
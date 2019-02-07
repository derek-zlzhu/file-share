package com.adesa.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {

    private WebView webView = null;
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.webView = (WebView) findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);

        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(true);

        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webSettings.setMixedContentMode(0);

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webView.setWebContentsDebuggingEnabled(true);

        WebViewClientImpl webViewClient = new WebViewClientImpl(this);
        webView.setWebViewClient(webViewClient);

        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage)
            {
                Log.d(TAG, "WebChromeClient consoleMessage: " + consoleMessage.message());

                // if "Uncaught SyntaxError: Unexpected end of input" error message is caught
                // we must load the webview using loadUrl instead of loadDataWithBaseURL
                // this is due to a bug with webview.loadDataWithBaseURL can't process JS with comments

                return true;
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request)
            {
//                Log.d(TAG, "onPermissionRequest");
//					request.grant(request.getResources());

//					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//						request.grant(request.getResources());
//					}

                MainActivity.this.runOnUiThread(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        Log.i(TAG, "|> onPermissionRequest run");
                        request.grant(request.getResources());
                    }
                });

            }
        });

        webView.loadUrl("https://s3.amazonaws.com/olm-web-dev/gens/scan/scan-into-input/index.html");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
package com.adesa.webview;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MainActivity extends Activity {

    private final int CAMERA_PERMISSION_REQUEST = 0;

    private boolean deniedCameraAccess = false;

    private WebView webView = null;
    public static final String TAG = MainActivity.class.getSimpleName();


//    @TargetApi(Build.VERSION_CODES.M)
//    private void grantCameraPermissionsThenStartScanning() {
//        if (this.checkSelfPermission(Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (!mDeniedCameraAccess) {
//                // It's pretty clear for why the camera is required. We don't need to give a
//                // detailed reason.
//                this.requestPermissions(new String[]{ Manifest.permission.CAMERA },
//                        CAMERA_PERMISSION_REQUEST);
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        if (requestCode == CAMERA_PERMISSION_REQUEST) {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                mDeniedCameraAccess = false;
//            } else {
//                mDeniedCameraAccess = true;
//            }
//            return;
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.webView = findViewById(R.id.webview);

        WebSettings webSettings = webView.getSettings();
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

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d(TAG, "WebChromeClient consoleMessage: " + consoleMessage.message());

                // if "Uncaught SyntaxError: Unexpected end of input" error message is caught
                // we must load the webview using loadUrl instead of loadDataWithBaseURL
                // this is due to a bug with webview.loadDataWithBaseURL can't process JS with comments

                return true;
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            grantCameraPermissionsThenStartScanning();
        } else {
            doSomeStuffWithPermissionGranted();
        }
    }

    private void doSomeStuffWithPermissionGranted() {
        webView.loadUrl("https://s3.amazonaws.com/olm-web-dev/gens/scan/scan-into-input/index.html");
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void grantCameraPermissionsThenStartScanning() {
        if (this.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (!deniedCameraAccess) {
                this.requestPermissions(new String[]{ Manifest.permission.CAMERA }, CAMERA_PERMISSION_REQUEST);
            }
        } else {
            doSomeStuffWithPermissionGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                deniedCameraAccess = false;
                doSomeStuffWithPermissionGranted();
            } else {
                deniedCameraAccess = true;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
package com.tweet.testapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * LoginWebActivity - twitter web login
 * 
 * @author VijayK
 * 
 */
public class LoginWebActivity extends Activity {

    /** Intent */
    private Intent mIntent;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.twitter_webview);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        this.mIntent = getIntent();
        String url = (String) this.mIntent.getExtras().get("URL");
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
                if (url.contains(getResources().getString(R.string.twitter_callback))) {
                    Uri uri = Uri.parse(url);
                    String oauthVerifier = uri.getQueryParameter(Constants.OAUTH_VERIFIER);
                    LoginWebActivity.this.mIntent.putExtra(Constants.OAUTH_VERIFIER, oauthVerifier);
                    setResult(RESULT_OK, LoginWebActivity.this.mIntent);
                    finish();
                    return true;
                }
                return false;
            }
        });
        webView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}

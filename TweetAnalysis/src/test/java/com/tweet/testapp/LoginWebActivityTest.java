/**
 * 
 */
package com.tweet.testapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowWebView;
import org.robolectric.util.ActivityController;

import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;

/**
 * Class to test LoginWebActivity
 * 
 * @author VijayK
 * 
 */
@RunWith(RobolectricTestRunner.class)
public class LoginWebActivityTest {

    /** HomeActivity instance */
    private LoginWebActivity mActivity;

    /** ActivityController instance */
    private ActivityController<LoginWebActivity> mActivityController;

    /** Fake URL - just to test things */
    private static final String FAKE_URL = "https://www.google.com";

    /**
     * setup
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final Context context = Robolectric.getShadowApplication().getApplicationContext();
        Intent intent = new Intent(context, LoginWebActivity.class);
        intent.putExtra("URL", FAKE_URL);
        this.mActivityController = Robolectric.buildActivity(LoginWebActivity.class).withIntent(intent).create();
        this.mActivity = this.mActivityController.get();
    }

    /**
     * Test 1 --> Method to test webview loading
     */
    @Test
    public void testWebViewLoading() {
        WebView webview = (WebView) this.mActivity.findViewById(R.id.webview);
        assertNotNull(webview);
        ShadowWebView shadowWebView = Robolectric.shadowOf(webview);
        assertEquals(shadowWebView.getLastLoadedUrl(), FAKE_URL);
        assertNotNull(shadowWebView.getWebViewClient());
    }
}

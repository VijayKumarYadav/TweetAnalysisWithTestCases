package com.tweet.testapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowHandler;
import org.robolectric.shadows.ShadowPreferenceManager;
import org.robolectric.shadows.ShadowToast;
import org.robolectric.shadows.ShadowWebView;
import org.robolectric.util.ActivityController;

import twitter4j.TwitterException;
import twitter4j.User;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.webkit.WebView;

/**
 * 
 * Class to test HomeActivity
 * 
 * @author VijayK
 * 
 */
@RunWith(RobolectricTestRunner.class)
public class HomeActivityTest {

    /** HomeActivity instance */
    private HomeActivity mActivity;

    /** ActivityController instance */
    private ActivityController<HomeActivity> mActivityController;

    /** ShadowActivity Instance */
    private ShadowActivity mShadowActivity;

    /** Invalid token */
    private static final String INVALID_TOKEN = "ABCJSKSOSNS28SN";

    /** Mock mUser instance */
    @Mock
    private User mUser;

    /**
     * setup
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mActivityController = Robolectric.buildActivity(HomeActivity.class).create();
        this.mActivity = this.mActivityController.get();
        this.mShadowActivity = Robolectric.shadowOf(this.mActivity);
    }

    /**
     * Test 1 --> Test for proper UI Initialization
     */
    @Test
    public void testUI() {
        assertNotNull(this.mActivity.findViewById(R.id.user_photo));
        assertNotNull(this.mActivity.findViewById(R.id.user_name));
        assertNotNull(this.mActivity.findViewById(R.id.follow_follower_pie));
        assertNotNull(this.mActivity.findViewById(R.id.follow_follower_graph));
    }

    /**
     * Test 2 --> Since mActivity is just created and mUser should not be login hence no access token should be present
     */
    @Test
    public void testToken() {
        SharedPreferences settings = ShadowPreferenceManager.getDefaultSharedPreferences(this.mActivity);
        assertNull(settings.getString(Constants.ACCESS_TOKEN, null));
        assertNull(settings.getString(Constants.ACCESS_TOKEN_SECRET, null));
    }

    /**
     * Test 3 --> Since mUser has press authenticate button and hence he/she is on this screen so mActivity should show
     * WebView for login
     * 
     * @throws TwitterException - TwitterException
     */
    @Test
    public void testWebViewShownWhenAcitivityResumed() throws TwitterException {
        this.mActivity.onResume();
        Intent startedIntent = this.mShadowActivity.getNextStartedActivity();
        assertEquals(startedIntent.getComponent().getClassName(), LoginWebActivity.class.getName());
    }

    /**
     * Test 4 --> Test onActivityResult, Since invalid token, we should expect twitter exception
     */
    @Test
    public void testOnActivityResult() {
        this.mActivity.onResume();
        this.mShadowActivity.receiveResult(this.mShadowActivity.getNextStartedActivity(), Activity.RESULT_OK,
                                           new Intent().putExtra(Constants.OAUTH_VERIFIER, INVALID_TOKEN));
        ShadowHandler.idleMainLooper();
        assertEquals(this.mActivity.getString(R.string.error_occured), ShadowToast.getTextOfLatestToast());

    }

    /**
     * Test 5 --> test logout, after log out, token should be clear from preference
     */
    @Test
    public void testLogout() {
        // save token
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this.mActivity);
        settings = PreferenceManager.getDefaultSharedPreferences(this.mActivity);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(Constants.ACCESS_TOKEN, INVALID_TOKEN);
        editor.commit();
        // do logout
        this.mActivity.logoutFromTwitter();
        // test preference
        assertNull(settings.getString(Constants.ACCESS_TOKEN, null));
    }

    /**
     * Test 6 --> Test chart rendering
     */
    @Test
    public void testChartRendering() {
        when(this.mUser.getFavouritesCount()).thenReturn(10);
        when(this.mUser.getFriendsCount()).thenReturn(10);
        when(this.mUser.getFollowersCount()).thenReturn(10);
        when(this.mUser.getStatusesCount()).thenReturn(10);
        this.mActivity.updateChart(this.mUser);
        ShadowWebView shadowWebView = Robolectric.shadowOf((WebView) this.mActivity
                .findViewById(R.id.follow_follower_pie));
        assertNotNull(shadowWebView.getLastLoadedUrl());
    }
}

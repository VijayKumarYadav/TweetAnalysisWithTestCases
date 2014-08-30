/**
 * 
 */
package com.tweet.testapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
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
import org.robolectric.shadows.ShadowToast;
import org.robolectric.util.ActivityController;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;

import com.tweet.testapp.MainActivity.NetworkManager;

/**
 * Class to test MainActivity
 * 
 * @author VijayK
 * 
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {

    /** login Button */
    private Button mBtnLogin;

    /** MainActivity instance */
    private MainActivity mActivity;

    /** ActivityController instance */
    private ActivityController<MainActivity> mActivityController;

    /** ShadowActivity Instance */
    private ShadowActivity mShadowActivity;

    /** NetworkManager */
    @Mock
    private NetworkManager mNetworkManager;

    /**
     * setup
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        this.mActivityController = Robolectric.buildActivity(MainActivity.class).create().start().resume();
        this.mActivity = this.mActivityController.get();
        this.mShadowActivity = Robolectric.shadowOf(this.mActivity);
        this.mBtnLogin = (Button) this.mActivity.findViewById(R.id.btnLogin);
        this.mActivity.setNetworkManager(this.mNetworkManager);
    }

    /**
     * Test 1 --> test for proper UI initialization
     */
    @Test
    public void testUI() {
        // should not null
        assertNotNull(this.mBtnLogin);
        // test click listener
        assertNotNull(Robolectric.shadowOf(this.mBtnLogin).getOnClickListener());
    }

    /**
     * Test 2 --> Test click flow when network is avaialble
     */
    @Test
    public void testClickFlowWhenNetworkAvail() {
        when(this.mNetworkManager.haveNetworkConnection(any(Context.class))).thenReturn(true);
        this.mBtnLogin.performClick();
        Intent startedIntent = this.mShadowActivity.getNextStartedActivity();
        assertEquals(startedIntent.getComponent().getClassName(), HomeActivity.class.getName());
    }

    /**
     * Test 2 --> Test click flow when network is not avaialble
     */
    @Test
    public void testClickFlowWhenNetworkNotAvail() {
        when(this.mNetworkManager.haveNetworkConnection(any(Context.class))).thenReturn(false);
        this.mBtnLogin.performClick();
        ShadowHandler.idleMainLooper();
        assertEquals(this.mActivity.getString(R.string.no_network), ShadowToast.getTextOfLatestToast());
    }

}

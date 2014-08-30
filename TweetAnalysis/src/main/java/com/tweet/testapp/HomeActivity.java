package com.tweet.testapp;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.BarChart;
import com.googlecode.charts4j.BarChartPlot;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.PieChart;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Slice;

/**
 * HomeActivity - Responsible for Authentication and home screen
 * 
 * @author VijayK
 * 
 */
public class HomeActivity extends Activity {

    /** Request code */
    private int TWITTER_AUTH;

    /** Twitter Instance */
    private Twitter mTwitter;

    /** RequestToken */
    private RequestToken mRequestToken;

    /** mAccessToken */
    private String mAccessToken;

    /** mAccessTokenSecret */
    private String mAccessTokenSecret;

    /** WebView */
    private WebView mWebviewPie;

    /** ImageView */
    private ImageView mUserImageView;

    /** TextView */
    private TextView mUserNameView;

    /** Animation */
    private Animation mAnimFadein;

    /** WebView for graph */
    private WebView mWebviewGraph;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set custom title
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.home);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        // logout initially
        logoutFromTwitter();
        // initialize UI elements
        initUI();
    }

    /**
     * Method to init UIs
     */
    private void initUI() {
        // All UI elements
        this.mUserImageView = (ImageView) findViewById(R.id.user_photo);
        this.mUserNameView = (TextView) findViewById(R.id.user_name);
        this.mWebviewPie = (WebView) findViewById(R.id.follow_follower_pie);
        this.mWebviewPie.setOnTouchListener(this.touchListener);
        this.mWebviewGraph = (WebView) findViewById(R.id.follow_follower_graph);
        this.mWebviewGraph.setOnTouchListener(this.touchListener);
        // load the animation
        this.mAnimFadein = AnimationUtils.loadAnimation(this, R.anim.rotate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        this.mAccessToken = settings.getString(Constants.ACCESS_TOKEN, null);
        this.mAccessTokenSecret = settings.getString(Constants.ACCESS_TOKEN_SECRET, null);
        if ((this.mAccessToken == null) || (this.mAccessTokenSecret == null)) {
            this.mTwitter = new TwitterFactory().getInstance();
            this.mRequestToken = null;
            this.mTwitter.setOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
            String callbackURL = getResources().getString(R.string.twitter_callback);
            try {
                this.mRequestToken = this.mTwitter.getOAuthRequestToken(callbackURL);
                Intent i = new Intent(this, LoginWebActivity.class);
                i.putExtra("URL", this.mRequestToken.getAuthenticationURL() + Constants.FORCE_LOGIN);
                startActivityForResult(i, this.TWITTER_AUTH);
            } catch (TwitterException e) {
                Toast.makeText(this, getString(R.string.error_occured), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == this.TWITTER_AUTH) {
            if (resultCode == Activity.RESULT_OK) {
                String oauthVerifier = (String) data.getExtras().get(Constants.OAUTH_VERIFIER);
                AccessToken mAccessTokenInstance = null;
                try {
                    // Pair up our request with the response
                    mAccessTokenInstance = this.mTwitter.getOAuthAccessToken(oauthVerifier);
                    String theToken = mAccessTokenInstance.getToken();
                    String theTokenSecret = mAccessTokenInstance.getTokenSecret();
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    settings = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Constants.ACCESS_TOKEN, theToken);
                    editor.putString(Constants.ACCESS_TOKEN_SECRET, theTokenSecret);
                    editor.putString(Constants.OAUTH_VERIFIER, oauthVerifier);
                    editor.commit();
                    updateUI(mAccessTokenInstance);
                } catch (TwitterException e) {
                    Toast.makeText(this, getString(R.string.error_occured), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /**
     * Function to logout from twitter It will just clear the application shared
     * preferences
     * */
    public void logoutFromTwitter() {
        // Clear the shared preferences
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.edit().clear().commit();
    }

    /**
     * Method to update UI for user name and photo
     * 
     * @param mAccessTokenInstance - Access Token
     */
    private void updateUI(final AccessToken mAccessTokenInstance) {
        long userID = mAccessTokenInstance.getUserId();
        User user;
        try {
            user = this.mTwitter.showUser(userID);
            // Displaying
            this.mUserNameView.setText(user.getName());
            new DownloadImageTask(this.mUserImageView).execute(user.getOriginalProfileImageURL());
            updateChart(user);
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to update charts
     * 
     * @param user - User object
     */
    public void updateChart(final User user) {
        int followerCount = user.getFollowersCount();
        int friendCount = user.getFriendsCount();
        int favouriteCount = user.getFavouritesCount();
        int statusCount = user.getStatusesCount();

        /** set chart height width according to screen density */
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        final float scale = metrics.density;
        int width = (int) (metrics.widthPixels / scale);
        int height = (int) (metrics.heightPixels / (3 * scale));

        /** set slices */
        Slice friend = Slice.newSlice(followerCount, Color.newColor("CACACA"), "Follower");
        Slice follower = Slice.newSlice(friendCount, Color.newColor("DF7417"), "Friend");
        Slice favourite = Slice.newSlice(favouriteCount, Color.newColor("951800"), "Favourite");
        Slice status = Slice.newSlice(statusCount, Color.newColor("01A1DB"), "Tweets");

        /** create pie chart */
        PieChart chart = GCharts.newPieChart(friend, follower, favourite, status);
        chart.setTitle("Friend Follower Tweets Analysis", Color.newColor("01A1DB"), 21);
        chart.setSize((width), (int) (height * 0.95f));
        chart.setThreeD(true);
        this.mWebviewPie.loadUrl(chart.toURLString());

        // bar chart
        BarChartPlot team1 = Plots.newBarChartPlot(Data
                .newData(friendCount, followerCount, favouriteCount, statusCount), Color.newColor("01A1DB"));

        // Instantiating chart.
        BarChart graphChart = GCharts.newBarChart(team1);

        // Adding axis info to chart.
        graphChart.addXAxisLabels(AxisLabelsFactory.newAxisLabels("Friend", "Follower", "Favourite", "Tweets"));
        graphChart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, 100));
        graphChart.setSize(width, height);
        graphChart.setBarWidth(width / 5 - 10);
        graphChart.setSpaceWithinGroupsOfBars(20);
        graphChart.setDataStacked(true);
        graphChart.setGrid(100, 10, 3, 2);
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.LAVENDER, 100);
        fill.addColorAndOffset(Color.WHITE, 0);
        graphChart.setAreaFill(fill);
        this.mWebviewGraph.loadUrl(graphChart.toURLString());
    }

    /**
     * OnTouchListener
     */
    private final OnTouchListener touchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            HomeActivity.this.mWebviewPie.startAnimation(HomeActivity.this.mAnimFadein);
            HomeActivity.this.mWebviewGraph.startAnimation(HomeActivity.this.mAnimFadein);
            return false;
        }
    };
}

package com.tweet.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

/**
 * Main Activity - Launcher
 * Show Authentication button and call Home Screen
 * 
 * @author VijayK
 * 
 */
public class MainActivity extends Activity {

    /** login Button */
    private Button btnLogin;

    /** Animation instance */
    private Animation animFadein;

    /** NetworkManager */
    private NetworkManager networkManager;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.login);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
        this.btnLogin = (Button) findViewById(R.id.btnLogin);
        this.networkManager = new NetworkManager();
        // load the animation
        this.animFadein = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        this.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (getNetworkManager().haveNetworkConnection(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, getString(R.string.connecting), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, HomeActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.no_network), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.btnLogin.startAnimation(this.animFadein);
    }

    /**
     * getNetworkManager
     * 
     * @return networkManager
     */
    public NetworkManager getNetworkManager() {
        return this.networkManager;
    }

    /**
     * setNetworkManager
     * 
     * @param networkManager - NetworkManager
     */
    public void setNetworkManager(final NetworkManager networkManager) {
        this.networkManager = networkManager;
    }

    /**
     * Class to check networks
     * 
     * @author vy250012
     * 
     */
    public static class NetworkManager {

        /** Wifi connected check */
        private boolean haveConnectedWifi;

        /** Mobile network connected check */
        private boolean haveConnectedMobile;

        /**
         * Check network
         * 
         * @param context - context
         * 
         * @return status - net status
         */
        public boolean haveNetworkConnection(final Context context) {
            this.haveConnectedWifi = false;
            this.haveConnectedMobile = false;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if ("WIFI".equalsIgnoreCase(ni.getTypeName())) {
                    if (ni.isConnected()) {
                        this.haveConnectedWifi = true;
                    }
                }
                if ("MOBILE".equalsIgnoreCase(ni.getTypeName())) {
                    if (ni.isConnected()) {
                        this.haveConnectedMobile = true;
                    }
                }
            }
            return this.haveConnectedWifi || this.haveConnectedMobile;
        }
    }

}

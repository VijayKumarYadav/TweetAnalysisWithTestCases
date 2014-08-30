/**
 * 
 */
package com.tweet.testapp;

import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

/**
 * Use to download profile image
 * 
 * @author VijayK
 * 
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    /** Image view */
    ImageView mImageView;

    /**
     * DownloadImageTask Constructor
     * 
     * @param mImageView - ImageView
     */
    public DownloadImageTask(final ImageView mImageView) {
        this.mImageView = mImageView;
    }

    @Override
    protected Bitmap doInBackground(final String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {

            InputStream in = new URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(final Bitmap result) {
        this.mImageView.setImageBitmap(result);
    }
}

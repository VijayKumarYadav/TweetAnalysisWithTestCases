package com.tweet.testapp;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @author vy250012
 * 
 */
@RunWith(RobolectricTestRunner.class)
public class DownloadImageTaskTest {

    /**
     * mDownloadImageTask Instance
     */
    private DownloadImageTask mDownloadImageTask;

    /**
     * setup
     */
    @Before
    public void setup() {
        Robolectric.getBackgroundScheduler().pause();
        Robolectric.getUiThreadScheduler().pause();
    }

    /**
     * Test Flow
     * 
     * @throws InterruptedException - can throw InterruptedException
     * @throws ExecutionException - can throw ExecutionException
     * @throws TimeoutException - can throw TimeoutException
     */
    @Test
    public void testFlow() throws InterruptedException, ExecutionException, TimeoutException {
        final Context context = Robolectric.getShadowApplication().getApplicationContext();
        this.mDownloadImageTask = new DownloadImageTask(new ImageView(context));
        this.mDownloadImageTask.execute("http://www.google.com");
        Robolectric.runBackgroundTasks();
        assertTrue(this.mDownloadImageTask.get(100, TimeUnit.MILLISECONDS) instanceof Bitmap);
    }
}

package com.men1n2.couca;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;


public class changeLanguageTutorial extends Activity {
    // Timer to return to the main screen if no action is done
    Timer timer;

    // Video container
    VideoView enterTextVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language_tutorial);

        enterTextVideo = (VideoView) findViewById(R.id.videoView4);
        enterTextVideo.setVideoPath("android.resource://" + getPackageName() + "/" + R.drawable.entertextvid);
        enterTextVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        // Create Timer
        timer = new Timer();
        // Add timer task
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                changeActivity();
            }
        };
        // Timer cycle time
        timer.schedule(timerTask, 3000);
    }

    private void changeActivity() {
        Intent intent = new Intent(this, enterText.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_change_language_tutorial, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Play animation automatically after frames loading
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        enterTextVideo.start();
    }
}

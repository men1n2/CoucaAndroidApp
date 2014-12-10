package com.men1n2.couca;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.men1n2.couca.R;

import java.util.Timer;
import java.util.TimerTask;

public class showPersonName extends Activity {
    // Object containing the animation
    VideoView showTextVideo;

    // For name animation
    TextView personNameTextView;
    Animation personNameTextViewAnimation;
    ImageView foregroundImage;

    // Person name passed from previous activity
    String personName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_person_name);

        // Dim System Bar
        // This example uses decor view, but you can use any visible view.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        // Disable action on Status Bar
        // ------------------------
        WindowManager manager = ((WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        CustomViewGroup view = new CustomViewGroup(this);

        manager.addView(view, localLayoutParams);
        // ------------------------

        // Create Timer
        Timer timer = new Timer();
        // Add timer task and cycle time
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                restartApp();
            }
        };
        timer.schedule(timerTask, 30000);

        showTextVideo = (VideoView) findViewById(R.id.videoView3);
        showTextVideo.setVideoPath("android.resource://" + getPackageName() + "/" + R.drawable.shownamevid);
        showTextVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        // Get the message from the intent
        personName = getIntent().getExtras().getString("personName");
        personNameTextView = (TextView) findViewById(R.id.personNameField);
        personNameTextView.setText(personName);
        Typeface fontType = Typeface.createFromAsset(getAssets(), "fonts/CoucaAppFont.ttf");
        personNameTextView.setTypeface(fontType);

        // Animation for the textView of the person's name
        personNameTextViewAnimation = AnimationUtils.loadAnimation(this, R.anim.shownameanimation);
        personNameTextView.startAnimation(personNameTextViewAnimation);
        foregroundImage = (ImageView) findViewById(R.id.fgImageView);

        // Create Timer
        Timer timer2 = new Timer();
        // Add timer task and cycle time
        TimerTask timerTask2 = new TimerTask() {
            @Override
            public void run() {
                // This to run the code on the main UI thread and avoid exception
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        foregroundImage.setVisibility(View.VISIBLE);
                    }
                });
            }
        };
        timer2.schedule(timerTask2, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_person_name, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Prevent exit on Back pressed
    @Override
    public void onBackPressed() {
        // We doing this too stop user from exiting app, normally.
        // super.onBackPressed();
        // Dim System Bar
        // This example uses decor view, but you can use any visible view.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    // To prevent action on Multitask button click
    @Override
    protected void onPause() {
        super.onPause();

        // Dim System Bar
        // This example uses decor view, but you can use any visible view.
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }

    // Play animation automatically after frames loading
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // introAnimation.start();
        showTextVideo.start();
        autoScaleTextViewTextToHeight(personNameTextView, personName);
        personNameTextView.setPadding(0, 0, 0, 100);
    }

    /**
     * To scale TextView to the text

    public static void autoScaleTextViewTextToHeight(final TextView tv, String s) {
        float currentWidth = tv.getPaint().measureText(s);
        int scalingFactor = 0;
        final int characters = s.length();
        //scale based on # of characters in the string
        if (characters < 5) {
            scalingFactor = 100;
        } else if (characters >= 5 && characters < 7) {
            scalingFactor = 200;
        } else if (characters >= 7 && characters < 15) {
            scalingFactor = 300;
        }

        //System.out.println(((int)Math.ceil(currentWidth)/tv.getWidth()+scalingFactor));
        //the +scalingFactor is important... increase this if nec. later
        while ((((int) Math.ceil(currentWidth) / tv.getWidth() + scalingFactor) * tv.getTextSize()) < tv.getHeight()) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, tv.getTextSize() + 0.25f);
            currentWidth = tv.getPaint().measureText(s);
            //System.out.println(((int)Math.ceil(currentWidth)/tv.getWidth()+scalingFactor));
        }
        tv.setText(s);
    }
     */
    public static void autoScaleTextViewTextToHeight(final TextView tv, String s) {
        final int characters = s.length();
        //scale based on # of characters in the string
        if (characters < 4) {
            tv.setTextSize(450f);
        } else if (characters >= 4 && characters < 7) {
            tv.setTextSize(400f);
        } else if (characters >= 7 && characters < 10) {
            tv.setTextSize(280f);
        } else if (characters >= 10 && characters < 13) {
            tv.setTextSize(230f);
        } else if (characters >= 13 && characters < 20) {
            tv.setTextSize(180f);
        }
    }

    /**
     * Restart the application
     */
    public void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}

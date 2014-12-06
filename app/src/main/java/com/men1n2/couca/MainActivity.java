package com.men1n2.couca;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends Activity {
    // Object containing the animation
    // AnimationDrawable introAnimation;
    FasterAnimationsContainer mFasterAnimationsContainer;
    private static final int[] IMAGE_RESOURCES = { R.drawable.introanimframe_00000,
            R.drawable.introanimframe_00001, R.drawable.introanimframe_00002, R.drawable.introanimframe_00003,
            R.drawable.introanimframe_00004, R.drawable.introanimframe_00005, R.drawable.introanimframe_00006,
            R.drawable.introanimframe_00007, R.drawable.introanimframe_00008, R.drawable.introanimframe_00009, R.drawable.introanimframe_00010,
            R.drawable.introanimframe_00011, R.drawable.introanimframe_00012, R.drawable.introanimframe_00013,
            R.drawable.introanimframe_00014, R.drawable.introanimframe_00015, R.drawable.introanimframe_00016,
            R.drawable.introanimframe_00017, R.drawable.introanimframe_00018, R.drawable.introanimframe_00019, R.drawable.introanimframe_00020,
            R.drawable.introanimframe_00021, R.drawable.introanimframe_00022, R.drawable.introanimframe_00023,
            R.drawable.introanimframe_00024, R.drawable.introanimframe_00025, R.drawable.introanimframe_00026,
            R.drawable.introanimframe_00027, R.drawable.introanimframe_00028, R.drawable.introanimframe_00029, R.drawable.introanimframe_00030,
            R.drawable.introanimframe_00031, R.drawable.introanimframe_00032, R.drawable.introanimframe_00033,
            R.drawable.introanimframe_00034, R.drawable.introanimframe_00035, R.drawable.introanimframe_00036,
            R.drawable.introanimframe_00037, R.drawable.introanimframe_00038, R.drawable.introanimframe_00039, R.drawable.introanimframe_00040,
            R.drawable.introanimframe_00041, R.drawable.introanimframe_00042, R.drawable.introanimframe_00043,
            R.drawable.introanimframe_00044, R.drawable.introanimframe_00045, R.drawable.introanimframe_00046,
            R.drawable.introanimframe_00047, R.drawable.introanimframe_00048, R.drawable.introanimframe_00049, R.drawable.introanimframe_00050,
            R.drawable.introanimframe_00051, R.drawable.introanimframe_00052, R.drawable.introanimframe_00053,
            R.drawable.introanimframe_00054, R.drawable.introanimframe_00055, R.drawable.introanimframe_00056,
            R.drawable.introanimframe_00057, R.drawable.introanimframe_00058, R.drawable.introanimframe_00059, R.drawable.introanimframe_00060,
            R.drawable.introanimframe_00061, R.drawable.introanimframe_00062, R.drawable.introanimframe_00063,
            R.drawable.introanimframe_00064, R.drawable.introanimframe_00065, R.drawable.introanimframe_00066,
            R.drawable.introanimframe_00067, R.drawable.introanimframe_00068, R.drawable.introanimframe_00069, R.drawable.introanimframe_00070,
            R.drawable.introanimframe_00071, R.drawable.introanimframe_00072, R.drawable.introanimframe_00073,
            R.drawable.introanimframe_00074, R.drawable.introanimframe_00075, R.drawable.introanimframe_00076,
            R.drawable.introanimframe_00077, R.drawable.introanimframe_00078, R.drawable.introanimframe_00079, R.drawable.introanimframe_00080,
            R.drawable.introanimframe_00081, R.drawable.introanimframe_00082, R.drawable.introanimframe_00083,
            R.drawable.introanimframe_00084, R.drawable.introanimframe_00085, R.drawable.introanimframe_00086,
            R.drawable.introanimframe_00087 };

    private static final int ANIMATION_INTERVAL = 40;// X in ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

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
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

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

        Typeface fontType = Typeface.createFromAsset(getAssets(), "fonts/CoucaAppFont.ttf");
        Button button = (Button) findViewById(R.id.button);
        button.setTypeface(fontType);

        ImageView introImage = (ImageView) findViewById(R.id.imageView);
        // introImage.setBackgroundResource(R.drawable.introanimation);
        // introAnimation = (AnimationDrawable) introImage.getBackground();
        mFasterAnimationsContainer = FasterAnimationsContainer.getInstance(introImage);
        mFasterAnimationsContainer.addAllFrames(IMAGE_RESOURCES, ANIMATION_INTERVAL);
        mFasterAnimationsContainer.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    // Play animation automatically after frames loading
    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        // introAnimation.start();
    }

    // Go to next activity on screen touch
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(this, enterText.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onTouchEvent(event);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mFasterAnimationsContainer.stop();
    }

    /** Called when the user touches the button */
    public void tarbijaButtonClicked(View view) {
        // Do something in response to button click
        Intent intent = new Intent(this, enterText.class);
        startActivity(intent);
        finish();
    }

}




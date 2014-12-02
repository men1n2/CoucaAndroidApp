package com.men1n2.couca;

import android.app.Activity;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.view.WindowManager;
import android.widget.TextView;

import com.men1n2.couca.R;

import java.util.Timer;
import java.util.TimerTask;

public class showNameActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // Create Timer
        Timer timer = new Timer();
        // Add timer task and cycle time
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("bof");
                restartApp();
            }
        };
        timer.schedule(timerTask, 3000);

        setContentView(R.layout.activity_show_name);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        // Get the message from the intent
        String personName = getIntent().getExtras().getString("personName");
        TextView personNameTextView = (TextView) findViewById(R.id.personNameField);
        personNameTextView.setText(personName);
        // autoScaleTextViewTextToHeight(textField, personName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_name, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_show_name, container, false);
            return rootView;
        }

        public void setText(String text){
            TextView textView = (TextView) getView().findViewById(R.id.personNameField);
            textView.setText(text);
        }
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

    /** To scale TextView to the text */
    public static void autoScaleTextViewTextToHeight(final TextView tv, String s)
    {
        float currentWidth=tv.getPaint().measureText(s);
        int scalingFactor = 0;
        final int characters = s.length();
        //scale based on # of characters in the string
        if(characters<10)
        {
            scalingFactor = 2;
        }
        /*else if(characters>=5 && characters<10)
        {
            scalingFactor = 2;
        }*/
        else if(characters>=10 && characters<15)
        {
            scalingFactor = 3;
        }
        else if(characters>=15 && characters<20)
        {
            scalingFactor = 3;
        }
        else if(characters>=20 && characters<25)
        {
            scalingFactor = 3;
        }
        else if(characters>=25 && characters<30)
        {
            scalingFactor = 3;
        }
        else if(characters>=30 && characters<35)
        {
            scalingFactor = 3;
        }
        else if(characters>=35 && characters<40)
        {
            scalingFactor = 3;
        }
        else if(characters>=40 && characters<45)
        {
            scalingFactor = 3;
        }
        else if(characters>=45 && characters<50)
        {
            scalingFactor = 3;
        }
        else if(characters>=50 && characters<55)
        {
            scalingFactor = 3;
        }
        else if(characters>=55 && characters<60)
        {
            scalingFactor = 3;
        }
        else if(characters>=60 && characters<65)
        {
            scalingFactor = 3;
        }
        else if(characters>=65 && characters<70)
        {
            scalingFactor = 3;
        }
        else if(characters>=70 && characters<75)
        {
            scalingFactor = 3;
        }
        else if(characters>=75)
        {
            scalingFactor = 5;
        }

        //System.out.println(((int)Math.ceil(currentWidth)/tv.getWidth()+scalingFactor));
        //the +scalingFactor is important... increase this if nec. later
        while((((int)Math.ceil(currentWidth)/tv.getWidth()+scalingFactor)*tv.getTextSize())<tv.getHeight())
        {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, tv.getTextSize()+0.25f);
            currentWidth=tv.getPaint().measureText(s);
            //System.out.println(((int)Math.ceil(currentWidth)/tv.getWidth()+scalingFactor));
        }

        tv.setText(s);
    }

    /** Restart the application */
    public void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

package com.men1n2.couca;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class enterText extends Activity {
    // Timer to return to the main screen if no action is done
    Timer timer;

    // Button of confirmation
    Button confirmButton;

    // Object containing the animation
    VideoView enterTextVideo;

    // Text Field
    EditText editField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_text);

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

        enterTextVideo = (VideoView) findViewById(R.id.videoView2);
        enterTextVideo.setVideoPath("android.resource://" + getPackageName() + "/" + R.drawable.entertextvid);
        enterTextVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });

        // Create Timer
        timer = new Timer();
        // Add timer task and cycle time
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                restartApp();
            }
        };
        timer.schedule(timerTask, 45000);

        // Show keyboard automatically on focus
        editField = (EditText) findViewById(R.id.personNameEditText);
        if (editField != null) {
            editField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }

        Typeface fontType = Typeface.createFromAsset(getAssets(), "fonts/CoucaAppFont.ttf");
        editField.setTypeface(fontType);
        editField.requestFocus();
        confirmButton = (Button) findViewById(R.id.confirmButton);
        confirmButton.setTypeface(fontType);

        // To execute code when DONE on keyboard clicked
        editField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    onConfirmClick(getWindow().getDecorView());
                }
                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enter_text, menu);
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
        enterTextVideo.start();
    }

    /**
     * Called when the user touches the button
     */
    public void onConfirmClick(View view) {
        // Do something in response to button click
        confirmButton.setBackgroundResource(R.drawable.confirmbuttonpressed);
        EditText inputField = (EditText) findViewById(R.id.personNameEditText);
        // Test if the word is bad or no
        if (testBadWords(inputField.getText().toString())) {
            // Show Alert if the word is bad word
            inputField.setText("");
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("أزبي !");
            builder1.setMessage("نقص بلا كلام منيك أساحبي :(");
            builder1.setCancelable(true);
            builder1.setNeutralButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
            confirmButton.setBackgroundResource(R.drawable.confirmbutton);
        } else {
            InputMethodManager imm = (InputMethodManager)getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editField.getWindowToken(), 0);
            timer.cancel();
            Intent intent = new Intent(this, showPersonName.class);
            intent.putExtra("personName", editField.getText().toString());
            startActivity(intent);
        }
    }

    /**
     * To test if the word is a bad word or no
     */
    public static boolean testBadWords(String word) {
        String[] badWordsArray = {"3asba", "aasba", "asba", "zeb ", "zab ", "zabb", "zebb", "zabour", "zabbour", "ta7an", "tahan",
                "ta77an", "9a7ba", "ka7ba", "kahba", "9ahba", "miboun", "mibouna", "3atay", "3attay", "aattay", "taffar", "tafar", "mag7out",
                "mak7out", "mnayek ", "nayek", "nik", "nike", "niq", "nique", "bachoula", "katla", "catla", "za3ka", "zaaka", "sorm", "terma",
                "nikommek", "nik omek", "nik ommek", "nik o5tek", "nik okhtek", "niko5etk", "nik bouk ", "zabbourommek", "zabbouromek",
                "zabourommek", "zabouromek", "zok", "zock", "zokk", "zokkommek", "zokommek", "zokomek", "zokkomek", "rabbek", "rabek", "rabb",
                "rabomek", "rabbommek", "rabomek", "rabbomek", "rabommek", "dirrabbek", "dirabek", "dirrabek", "dirabbek", "dinrabek",
                "dinrabbek", "pute", "bitch", "fuck", "pd", "flobb ", "flob", "chlambout", "torch9an", "nouna", "batrouna", "batroun", "zebi",
                "zebbi", "nam", "nami", "namm", "bachla", "zmonka", "terma",
                "عصبة", "زب", "زبور", "زبر", "طحان", "طحين", "قحبة", "قحب", "ميبون", "ميبونة", "عطاي", "طفار", "مكحوط", "منيك", "نيك", "نياك",
                "بشلة", "بشولة", "كتلة", "زعكة", "زعك", "صرم", "ترمة", "نيك أمك", "نيكأمك", "نيك أختك", "نيكأختك", "نيك بوك", "زبور أمك", "زك",
                "زك أمك", "زكأمك", "ربك", "رب", "رب أمك", "ديربك", "دربك", "دينربك", "دنربك", "فلب", "شلمبوت", "شلمبوط", "طرشقان", "نونة", "بترون",
                "بترونة", "بطرونة", "بطرون", "زبي", "نم", "نمي", "زمنكة"};
        for (String s : badWordsArray) {
            int i = s.indexOf(word);
            if (i >= 0) {
                // found a match to "word" at offset i
                return true;
            }
        }
        return false;
    }

    /**
     * Restart the application
     */
    public void restartApp() {
        timer.cancel();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

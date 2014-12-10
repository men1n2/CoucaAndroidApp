package com.men1n2.couca;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
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
    AutoCompleteTextView editField;

    // List of accepted names
    List<String> listOfNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Define custom transition between the previous and this activity
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

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

        Context context = this;
        Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale("en", "US");
        res.updateConfiguration(conf, dm);

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
        // Add timer task
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                restartApp();
            }
        };
        // Timer cycle time
        timer.schedule(timerTask, 45000);


        // Show keyboard automatically on focus
        editField = (AutoCompleteTextView) findViewById(R.id.personNameEditText);
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

        // Populate the text field with names to autocomplete with
        readNameListFromTextFile();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, listOfNames);
        editField.setThreshold(1);
        editField.setAdapter(adapter);
        editField.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                timer.cancel();
                // Create Timer
                timer = new Timer();
                // Add timer task
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        restartApp();
                    }
                };
                // Timer cycle time
                timer.schedule(timerTask, 30000);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

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
            builder1.setTitle("Hey !");
            builder1.setMessage("لازمك تختار تربيجة مالليستة !");
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
            InputMethodManager imm = (InputMethodManager) getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editField.getWindowToken(), 0);
            saveNameOnFile(editField.getText().toString());
            timer.cancel();
            Intent intent = new Intent(this, showPersonName.class);
            intent.putExtra("personName", editField.getText().toString());
            startActivity(intent);
        }
    }

    /**
     * Save the name on file on external storage
     */
    private boolean saveNameOnFile(String personName) {
        /* Checks if external storage is available for read and write */
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Write on file
            // String pathToExternalStorage = Environment.getExternalStorageDirectory().toString();
            File folder = new File(File.separator + "storage" + File.separator + "sdcard1" + File.separator + "Tarbijet");
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    return false;
                }
            }
            // New line seperator
            final String nlSeparator = System.getProperty("line.separator");
            File myFile = new File(folder, "Tarbijet.txt");
            if (myFile.exists()) {
                try {
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut, "utf8");
                    myOutWriter.append(personName);
                    myOutWriter.append(nlSeparator);
                    myOutWriter.flush();
                    myOutWriter.close();
                    fOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile, true);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut, "utf8");
                    myOutWriter.append(personName);
                    myOutWriter.append(nlSeparator);
                    myOutWriter.flush();
                    myOutWriter.close();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        } else return false;
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
                "zebbi", "nam", "nami", "namm", "bachla", "zmonka", "terma", "tartour", "bajbouj", "wral", "marzouki", "3asbet",
                "عصبة", "زب", "زبور", "زبر", "طحان", "طحين", "قحبة", "قحب", "ميبون", "ميبونة", "عطاي", "طفار", "مكحوط", "منيك", "نيك", "نياك",
                "بشلة", "بشولة", "كتلة", "زعكة", "زعك", "صرم", "ترمة", "نيك أمك", "نيكأمك", "نيك أختك", "نيكأختك", "نيك بوك", "زبور أمك", "زك",
                "زك أمك", "زكأمك", "ربك", "رب", "رب أمك", "ديربك", "دربك", "دينربك", "دنربك", "فلب", "شلمبوت", "شلمبوط", "طرشقان", "نونة", "بترون",
                "بترونة", "بطرونة", "بطرون", "زبي", "نم", "نمي", "زمنكة", "المرزوقي", "الورل", "البجبوج", "الطرطور", "العصبات"};
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
     * To populate the names' array from text file on the SD card
     */
    private void readNameListFromTextFile() {
        String fpath = File.separator + "storage" + File.separator + "sdcard1" + File.separator
                + "Tarbijet" + File.separator + "listOfTarbijet.csv";
        File file = null;
        try {
            file = new File(fpath);

            Scanner scanner = null;
            try {
                scanner = new Scanner(new FileInputStream(file));

                listOfNames = new ArrayList<String>();

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    listOfNames.add(line);
                }
                scanner.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

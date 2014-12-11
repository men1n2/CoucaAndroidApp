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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
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

        readNameListFromTextFile();

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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, listOfNames) {
            public ArrayList<String> suggestions = new ArrayList<String>();
            
            @Override
            public Filter getFilter() {
                return nameFilter;
            }

            Filter nameFilter = new Filter() {
                @Override
                public String convertResultToString(Object resultValue) {
                    String str = (String) resultValue;
                    return str;
                }
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    System.out.println("#### Filterin999...");
                    suggestions.clear();
                    readNameListFromTextFile();
                    if(charSequence != null) {
                        int index = 0;
                        String testString = "";
                        for (String s : listOfNames) {
                            testString = deleteUmlauts(s);
                            if (testString.length() >= charSequence.length()){
                                /*if (charSequence.toString().toLowerCase().contains(testString.substring(0, charSequence.length()-1).toLowerCase())) {
                                    System.out.println("#### Tghueeee");
                                    System.out.println("#### To test : " + listOfNames.get(index));
                                    System.out.println("### Its substr = " + testString.substring(0, charSequence.length()));
                                    System.out.println("#### Suggest : " + charSequence);
                                    suggestions.add(listOfNames.get(index));
                                }*/
                                int counter = 0;
                                for(int j = 0; j < charSequence.length(); j++) {
                                    System.out.println("### Comparing : " + charSequence.toString().charAt(j) + " || " + testString.charAt(j));
                                    if(charSequence.toString().toLowerCase().charAt(j) == testString.toLowerCase().charAt(j)) counter++;
                                }
                                if(counter == charSequence.length()) suggestions.add(listOfNames.get(index));
                            }
                            index++;
                        }
                        FilterResults filterResults = new FilterResults();
                        filterResults.values = suggestions;
                        filterResults.count = suggestions.size();
                        System.out.println("#### N of suggs = " + suggestions.size());
                        return filterResults;
                    } else {
                        return new FilterResults();
                    }
                }
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    List<String> filteredList = (List<String>) results.values;
                    if(results != null && results.count > 0) {
                        clear();
                        for (String c : filteredList) {
                            add(c);
                        }
                        notifyDataSetChanged();
                    }
                }
            };
        };
        editField.setThreshold(1);
        editField.setAdapter(adapter);
        editField.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // This to run the code on the main UI thread and avoid exception
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                        timer.schedule(timerTask, 20000);
                    }
                });
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
        if (!testPermittedWords(inputField.getText().toString())) {
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
     * To test if the word entered is on the list or no
     */
    private boolean testPermittedWords(String word) {
        for (String s : listOfNames) {
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
    public void readNameListFromTextFile() {
        // Path to test on emulator/device without SD card
        /*String fpath = File.separator + "mnt" + File.separator + "sdcard" + File.separator
                    + "Tarbijet" + File.separator + "listOfTarbijet.csv";*/
        // Path in a device with SD card
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

    /**
     * Normalize Method
     * \p{Mn} in regex (called Diphthongs)
     *
     * @return String
     */
    public String deleteUmlauts(String input) {

        //Remove honorific sign
        input = input.replaceAll("\u0610", "");//ARABIC SIGN SALLALLAHOU ALAYHE WA SALLAM
        input = input.replaceAll("\u0611", "");//ARABIC SIGN ALAYHE ASSALLAM
        input = input.replaceAll("\u0612", "");//ARABIC SIGN RAHMATULLAH ALAYHE
        input = input.replaceAll("\u0613", "");//ARABIC SIGN RADI ALLAHOU ANHU
        input = input.replaceAll("\u0614", "");//ARABIC SIGN TAKHALLUS

        //Remove koranic anotation
        input = input.replaceAll("\u0615", "");//ARABIC SMALL HIGH TAH
        input = input.replaceAll("\u0616", "");//ARABIC SMALL HIGH LIGATURE ALEF WITH LAM WITH YEH
        input = input.replaceAll("\u0617", "");//ARABIC SMALL HIGH ZAIN
        input = input.replaceAll("\u0618", "");//ARABIC SMALL FATHA
        input = input.replaceAll("\u0619", "");//ARABIC SMALL DAMMA
        input = input.replaceAll("\u061A", "");//ARABIC SMALL KASRA
        input = input.replaceAll("\u06D6", "");//ARABIC SMALL HIGH LIGATURE SAD WITH LAM WITH ALEF MAKSURA
        input = input.replaceAll("\u06D7", "");//ARABIC SMALL HIGH LIGATURE QAF WITH LAM WITH ALEF MAKSURA
        input = input.replaceAll("\u06D8", "");//ARABIC SMALL HIGH MEEM INITIAL FORM
        input = input.replaceAll("\u06D9", "");//ARABIC SMALL HIGH LAM ALEF
        input = input.replaceAll("\u06DA", "");//ARABIC SMALL HIGH JEEM
        input = input.replaceAll("\u06DB", "");//ARABIC SMALL HIGH THREE DOTS
        input = input.replaceAll("\u06DC", "");//ARABIC SMALL HIGH SEEN
        input = input.replaceAll("\u06DD", "");//ARABIC END OF AYAH
        input = input.replaceAll("\u06DE", "");//ARABIC START OF RUB EL HIZB
        input = input.replaceAll("\u06DF", "");//ARABIC SMALL HIGH ROUNDED ZERO
        input = input.replaceAll("\u06E0", "");//ARABIC SMALL HIGH UPRIGHT RECTANGULAR ZERO
        input = input.replaceAll("\u06E1", "");//ARABIC SMALL HIGH DOTLESS HEAD OF KHAH
        input = input.replaceAll("\u06E2", "");//ARABIC SMALL HIGH MEEM ISOLATED FORM
        input = input.replaceAll("\u06E3", "");//ARABIC SMALL LOW SEEN
        input = input.replaceAll("\u06E4", "");//ARABIC SMALL HIGH MADDA
        input = input.replaceAll("\u06E5", "");//ARABIC SMALL WAW
        input = input.replaceAll("\u06E6", "");//ARABIC SMALL YEH
        input = input.replaceAll("\u06E7", "");//ARABIC SMALL HIGH YEH
        input = input.replaceAll("\u06E8", "");//ARABIC SMALL HIGH NOON
        input = input.replaceAll("\u06E9", "");//ARABIC PLACE OF SAJDAH
        input = input.replaceAll("\u06EA", "");//ARABIC EMPTY CENTRE LOW STOP
        input = input.replaceAll("\u06EB", "");//ARABIC EMPTY CENTRE HIGH STOP
        input = input.replaceAll("\u06EC", "");//ARABIC ROUNDED HIGH STOP WITH FILLED CENTRE
        input = input.replaceAll("\u06ED", "");//ARABIC SMALL LOW MEEM

        //Remove tatweel
        input = input.replaceAll("\u0640", "");

        //Remove tashkeel
        input = input.replaceAll("\u064B", "");//ARABIC FATHATAN
        input = input.replaceAll("\u064C", "");//ARABIC DAMMATAN
        input = input.replaceAll("\u064D", "");//ARABIC KASRATAN
        input = input.replaceAll("\u064E", "");//ARABIC FATHA
        input = input.replaceAll("\u064F", "");//ARABIC DAMMA
        input = input.replaceAll("\u0650", "");//ARABIC KASRA
        input = input.replaceAll("\u0651", "");//ARABIC SHADDA
        input = input.replaceAll("\u0652", "");//ARABIC SUKUN
        input = input.replaceAll("\u0653", "");//ARABIC MADDAH ABOVE
        input = input.replaceAll("\u0654", "");//ARABIC HAMZA ABOVE
        input = input.replaceAll("\u0655", "");//ARABIC HAMZA BELOW
        input = input.replaceAll("\u0656", "");//ARABIC SUBSCRIPT ALEF
        input = input.replaceAll("\u0657", "");//ARABIC INVERTED DAMMA
        input = input.replaceAll("\u0658", "");//ARABIC MARK NOON GHUNNA
        input = input.replaceAll("\u0659", "");//ARABIC ZWARAKAY
        input = input.replaceAll("\u065A", "");//ARABIC VOWEL SIGN SMALL V ABOVE
        input = input.replaceAll("\u065B", "");//ARABIC VOWEL SIGN INVERTED SMALL V ABOVE
        input = input.replaceAll("\u065C", "");//ARABIC VOWEL SIGN DOT BELOW
        input = input.replaceAll("\u065D", "");//ARABIC REVERSED DAMMA
        input = input.replaceAll("\u065E", "");//ARABIC FATHA WITH TWO DOTS
        input = input.replaceAll("\u065F", "");//ARABIC WAVY HAMZA BELOW
        input = input.replaceAll("\u0670", "");//ARABIC LETTER SUPERSCRIPT ALEF

        return input;
    }
}


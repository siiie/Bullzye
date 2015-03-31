package com.mobilerealtimesystems.bullzye;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//import com.software.shell.fab.ActionButton;

public class MainActivity extends ActionBarActivity {
    private static final long delay = 2000L; // Delay between two back button clicks
    private String TAG = "bullzye"; // Logging TAG
    private String bestGuessKey = "com.bullzye.app.bestGuess";
    private Button btn;
    private Button reset;
    private EditText input;
    private LinearLayout outer;
    private LinearLayout rating;
    private ScrollView scroll;
    private Game gm;
    private RatingBar hitsRate;
    private RatingBar almostRate;
    private TextView lastGuess;
    private TextView bestGuess;
    private TextView numGuesses;
    //private ActionButton fab;
    private SharedPreferences prefs;

    private boolean mRecentlyBackPressed = false;
    private Runnable mExitRunnable = new Runnable() {

        @Override
        public void run() {
            mRecentlyBackPressed=false;
        }
    };
    private Handler mExitHandler = new Handler();

    // FAB listener
    private View.OnClickListener lstFabClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           // fab.hide();
            anyDialog("Bullzye","Exit the app?\n(The number was: " + gm.getSysNum().getNum() + ")","OK","Cancel","Exit");
        }
    };

    // EditText listener
    private TextWatcher tWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {


        }

        @Override
        public void afterTextChanged(Editable s) {
            String inputContent = input.getText().toString();
            if(inputContent.length()<4){
                btn.setEnabled(false);
            }else{
                btn.setEnabled(true);
            }
        }
    };
    // Button listener
    private View.OnClickListener lstBtn= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.buttonSubmit:
                    if(gm.setGameNum(input.getText().toString(), getApplicationContext())){
                        updateHits(gm);
                        updateAlmost(gm);
                        input.setText(""); // Empty input after clicking
                        uiUpdate();
                    }else{
                        almostRate.setRating(0);
                        hitsRate.setRating(0);
                        uiUpdate();
                    }

                    break;
                case R.id.buttonReset:
                    newGame();
                    Toast.makeText(getApplicationContext(),"Reset",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }


        }
    };
    private void uiUpdate() {
        if(gm.getGameNum().getNum() != null) {
            lastGuess.setText(getString(R.string.TextViewLastGuess) + gm.getGameNum().getNum());
            numGuesses.setText(getString(R.string.TextViewTotalGuesses) + gm.getGuesses());
        }
    }
    private void updateAlmost(Game gm) {
        int tmpAlmost = gm.getAlmost();
        Log.d(TAG,"Almost: " + tmpAlmost);
        almostRate.setRating(tmpAlmost);
    }
    private void updateHits(Game gm) {
        int tmpHits = gm.getHits();
        Log.d(TAG,"Hits: " + tmpHits);
        hitsRate.setRating(tmpHits);
        if(tmpHits==4){
            Toast.makeText(getApplicationContext(),"Bullzye!!",Toast.LENGTH_SHORT).show();
            setGuessesRecord();
            anyDialog("Bullzye","You Won.\nExit or Start new game?","Exit","New Game", "4Hits");
        }
    }

    private void setGuessesRecord() {
        int bestGuessTmp = prefs.getInt(bestGuessKey, -1);
        if(bestGuessTmp > gm.getGuesses()){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(bestGuessKey, gm.getGuesses());
            editor.apply();
            bestGuess.setText("Best guess: "+gm.getGuesses());
        }else{
            if (bestGuessTmp != -1){
                bestGuess.setText("Best guess: "+bestGuessTmp);
            }
        }

    }

    // Layout listener
    private View.OnClickListener lstLayout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            hideKeyboard();
        }
    };
    // EditText listener
    private View.OnClickListener lstEdit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            scroll.smoothScrollTo(0,rating.getBottom()); // Input above keyboard
        }
    };
    // On keyboard Done key pressed listener
    private View.OnKeyListener lstEditKey = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_ENTER){
                hideKeyboard();
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // widgets - findViewById
        scroll = (ScrollView) findViewById(R.id.scrollview);
        rating = (LinearLayout) findViewById(R.id.ratinglayout);
        outer = (LinearLayout) findViewById(R.id.outterLayout);
        outer.setOnClickListener(lstLayout);
        btn = (Button) findViewById(R.id.buttonSubmit);
        btn.setOnClickListener(lstBtn);
        reset = (Button) findViewById(R.id.buttonReset);
        reset.setOnClickListener(lstBtn);
        input = (EditText) findViewById(R.id.guessEditText);
        input.addTextChangedListener(tWatch);
        input.setOnClickListener(lstEdit);
        input.setOnKeyListener(lstEditKey);
        lastGuess = (TextView) findViewById(R.id.textViewLastGuess);
        bestGuess = (TextView) findViewById(R.id.textViewBestGuesses);
        numGuesses = (TextView) findViewById(R.id.textViewNumGuesses);
        hitsRate = (RatingBar) findViewById(R.id.ratingBarHits);
        almostRate = (RatingBar) findViewById(R.id.ratingBarAlmost);
        //fab = (ActionButton) findViewById(R.id.action_button);
        //fab.setType(ActionButton.Type.MINI);
        //fab.setOnClickListener(lstFabClick);

        prefs = this.getSharedPreferences(
                "com.bullzye.app", Context.MODE_PRIVATE);

        init();
    }

    private void init() {
        gm = new Game();
        gm.setSysNum();

        int bestGuessTmp = prefs.getInt(bestGuessKey, -1);
        if (bestGuessTmp != -1){
            bestGuess.setText("Best guess: "+bestGuessTmp);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void anyDialog(String title, String message, String positive, String negative, final String lst) {

        hideKeyboard();

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (lst.equals("Exit"))
                            //fab.show();
                        if (lst.equals("4Hits"))
                            return;
                    }
                })// Exit / OK
                .setPositiveButton(positive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (lst.equals("Exit")) {
                            newGame();
                            exitGame(); // Exit app code -> go to the 'Home Screen'
                        }
                        if (lst.equals("4Hits")) {
                            newGame();
                            exitGame(); // Exit app code -> go to the 'Home Screen'
                        }

                    }
                })// Cancel / New game
                .setNegativeButton(negative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (lst.equals("Exit")) {
                            //fab.show();
                            newGame();
                        }
                        if (lst.equals("4Hits"))
                            newGame();
                    }
                })
                .show();
    }

    private void newGame() {
        gm.setSysNum(); // New random number
        gm.setAlmost(0);
        gm.setGuesses(0);
        lastGuess.setText("");
        numGuesses.setText("");
        hitsRate.setRating(0);
        almostRate.setRating(0);
    }

    private void exitGame(){

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (mRecentlyBackPressed) {
            mExitHandler.removeCallbacks(mExitRunnable);
            mExitHandler = null;
            super.onBackPressed();
        }
        else
        {
            mRecentlyBackPressed = true;
            Toast.makeText(this, "press again to exit", Toast.LENGTH_SHORT).show();
            mExitHandler.postDelayed(mExitRunnable, delay);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // Hide keyboard method
    public void hideKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


}

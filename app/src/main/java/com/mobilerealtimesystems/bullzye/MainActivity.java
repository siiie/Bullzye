package com.mobilerealtimesystems.bullzye;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MainActivity extends ActionBarActivity {
    private static final long delay = 2000L; // Delay between two back button clicks
    private String TAG = "bullzye"; // Logging TAG
    private Button btn;
    private EditText input;
    private LinearLayout outer;
    private LinearLayout rating;
    private ScrollView scroll;
    private Game gm;
    private RatingBar hitsRate;
    private RatingBar almostRate;
    private TextView lastGuess;
    private TextView lastRandom;
    private TextView numGuesses;

    private boolean mRecentlyBackPressed = false;
    private Runnable mExitRunnable = new Runnable() {

        @Override
        public void run() {
            mRecentlyBackPressed=false;
        }
    };
    private Handler mExitHandler = new Handler();

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
            gm.setSysNum();
            //Log.d(TAG,"Input number onClick listener: "+ input.getText().toString());
            gm.setGameNum(input.getText().toString(), getApplicationContext());
            updateHits(gm);
            updateAlmost(gm);
            input.setText(""); // Empty input after clicking
            uiUpdate();

        }

        private void uiUpdate() {
            if(gm.getGameNum().getNum() != null) {
                lastGuess.setText(getString(R.string.TextViewLastGuess) + gm.getGameNum().getNum());
                numGuesses.setText(getString(R.string.TextViewTotalGuesses) + gm.getGuesses());
                lastRandom.setText(getString(R.string.TExtViewLastGenerated) + gm.getSysNum().getNum());
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
            }
        }
    };
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
        input = (EditText) findViewById(R.id.guessEditText);
        input.addTextChangedListener(tWatch);
        input.setOnClickListener(lstEdit);
        input.setOnKeyListener(lstEditKey);
        lastGuess = (TextView) findViewById(R.id.textViewLastGuess);
        lastRandom = (TextView) findViewById(R.id.textViewLastRandom);
        numGuesses = (TextView) findViewById(R.id.textViewNumGuesses);
        hitsRate = (RatingBar) findViewById(R.id.ratingBarHits);
        almostRate = (RatingBar) findViewById(R.id.ratingBarAlmost);

        init();
    }

    private void init() {
        gm = new Game();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_exit) {
            //Log.d(TAG,"Exit button clicked..");
            exitDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void exitDialog() {
        hideKeyboard();
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Bullzye");
        alertDialogBuilder.setMessage("Exit the app?");
        alertDialogBuilder.setPositiveButton("OK", positiveClick);
        alertDialogBuilder.setNegativeButton("Cancel", negativeClick);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    // Dialog listener - OK clicked
    private DialogInterface.OnClickListener positiveClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Exit app code -> go to the 'Home Screen'
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }
    };
    // Dialog listener - Cancel clicked
    private DialogInterface.OnClickListener negativeClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Do nothing
        }
    };

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

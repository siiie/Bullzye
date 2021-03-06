package com.mobilerealtimesystems.bullzye;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
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
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.beardedhen.androidbootstrap.BootstrapButton;

import com.github.mrengineer13.snackbar.SnackBar;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.software.shell.fab.ActionButton;

public class MainActivity extends ActionBarActivity {
    private static final long delay = 2000L; // Delay between two back button clicks
    private String TAG = "bullzye"; // Logging TAG
    private String bestGuessKey = "com.bullzye.app.bestGuess"; // Shared pres. key
    private EditText input;
    private RelativeLayout rating;
    private ScrollView scroll;
    private Game gm;
    private RatingBar hitsRate;
    private RatingBar almostRate;
    private TextView lastGuess;
    private TextView bestGuess;
    private TextView numGuesses;
    private ActionButton fab;
    private SharedPreferences prefs;
    private BootstrapButton submit;
    private DialogPlus dialogP;
    private DialogPlus dialogR;
    Vibrator vib;

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
            fab.hide();
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
                submit.setEnabled(false);
            }else{
                submit.setEnabled(true);
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
                    snackbar();
                    hideKeyboard();
                    //Toast.makeText(getApplicationContext(),"Reset",Toast.LENGTH_SHORT).show();
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

        Log.d(TAG,"Guess: " + bestGuessTmp);

        if(bestGuessTmp > gm.getGuesses() || bestGuessTmp==-1){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(bestGuessKey, gm.getGuesses());
            editor.apply();
            Log.d(TAG,"best Guess: " + +gm.getGuesses());
            bestGuess.setText("Best: "+gm.getGuesses());
        }else{
            if (bestGuessTmp != -1){
                bestGuess.setText("Best: "+bestGuessTmp);
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
        rating = (RelativeLayout) findViewById(R.id.ratingLayout);
        findViewById(R.id.outterLayout).setOnClickListener(lstLayout);
        submit = (BootstrapButton) findViewById(R.id.buttonSubmit);
        submit.setOnClickListener(lstBtn);
        findViewById(R.id.buttonReset).setOnClickListener(lstBtn);
        input = (EditText) findViewById(R.id.guessEditText);
        input.addTextChangedListener(tWatch);
        input.setOnClickListener(lstEdit);
        input.setOnKeyListener(lstEditKey);
        lastGuess = (TextView) findViewById(R.id.textViewLastGuess);
        bestGuess = (TextView) findViewById(R.id.textViewBestGuesses);
        numGuesses = (TextView) findViewById(R.id.textViewNumGuesses);
        hitsRate = (RatingBar) findViewById(R.id.ratingBarHits);
        almostRate = (RatingBar) findViewById(R.id.ratingBarAlmost);
        fab = (ActionButton) findViewById(R.id.action_button);
        fab.setOnClickListener(lstFabClick);
        aboutDialogInit();
        findViewById(R.id.aboutButtonCSS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                dialogP.show();
            }
        });
        rulesDialogInit();
        findViewById(R.id.rulesButtonCSS).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                dialogR.show();
            }
        });

        // Private shared pres
        prefs = this.getSharedPreferences("com.bullzye.app", Context.MODE_PRIVATE);
        vib = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        init();

    }

    private void aboutDialogInit() {
        dialogP = new DialogPlus.Builder(this)
                .setContentHolder(new ViewHolder(R.layout.about_dialog))
                .setInAnimation(R.anim.slide_in_bottom)
                .setOutAnimation(R.anim.slide_out_bottom)
                .setCancelable(true)
                .setGravity(DialogPlus.Gravity.BOTTOM)
                .setBackgroundColorResourceId(R.color.black)
                .create();
    }
    private void rulesDialogInit() {
        dialogR = new DialogPlus.Builder(this)
                .setContentHolder(new ViewHolder(R.layout.rules_card))
                .setInAnimation(R.anim.slide_in_bottom)
                .setOutAnimation(R.anim.slide_out_bottom)
                .setCancelable(true)
                .setGravity(DialogPlus.Gravity.BOTTOM)
                .setBackgroundColorResourceId(R.color.black)
                .create();
    }

    private void init() {
        gm = new Game();
        newGame();
        int bestGuessTmp = prefs.getInt(bestGuessKey, -1); // Shared pres.
        if (bestGuessTmp != -1){
            bestGuess.setText("Best: "+bestGuessTmp);
        }
    }
    private void newGame() {
        gm.setSysNum(); // New random number
        gm.setAlmost(0);
        gm.setGuesses(0);
        lastGuess.setText("Last: ");
        numGuesses.setText("Total: ");
        hitsRate.setRating(0);
        almostRate.setRating(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_exit || super.onOptionsItemSelected(item);

    }

    private void anyDialog(String title, String message, String positive, String negative, final String lst) {

        hideKeyboard();

        new MaterialDialog.Builder(this)
                .title(title)
                .content(message)
                .positiveText(positive)
                .negativeText(negative)
                .theme(Theme.DARK)
                .disableDefaultFonts()
                .contentGravity(GravityEnum.CENTER)
                .buttonsGravity(GravityEnum.CENTER)
                .cancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (lst.equals("Exit"))
                            fab.show();
                        if (lst.equals("4Hits")) {
                        }
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override // Exit / OK
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        if (lst.equals("Exit")) {
                            newGame();
                            exitGame(); // Exit app code -> go to the 'Home Screen'
                        }
                        if (lst.equals("4Hits")) {
                            newGame();
                            exitGame(); // Exit app code -> go to the 'Home Screen'
                        }
                    }

                    @Override // Cancel / New game
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        if (lst.equals("Exit")) {
                            fab.show();
                            newGame();
                        }
                        if (lst.equals("4Hits"))
                            newGame();
                    }
                })
                .show();
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
            vib.vibrate(50);
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

    private void snackbar(){
        new SnackBar.Builder(this)
                .withMessage("Game reset")
                .withActionMessage("Dismiss")
                .withStyle(SnackBar.Style.INFO)
                .withDuration((short) 3000)
                .show();
    }

    public static void main(String args[]){
        System.out.println("Hello");

    }
}



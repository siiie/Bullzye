package com.mobilerealtimesystems.bullzye;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Game {

    private String TAG = "bullzye"; /* Logging TAG */
    private Number gameNum;
    private Number sysNum;
    private int hits;
    private int almost;
    private int totalHits;
    private int totalAlmost;
    private int guesses;
    private int bestGuess;

    public int getGuesses() {
        return guesses;
    }

    public void setGuesses(int guesses) {
        this.guesses = guesses;
    }

    public int getTotalHits() {
        return totalHits;
    }

    public int getTotalAlmost() {
        return totalAlmost;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public int getAlmost() {
        return almost;
    }

    public void setAlmost(int almost) {
        this.almost = almost;
    }

    // Empty c'tor
    public Game() {
        this.gameNum = new Number();
        this.sysNum = new Number();
        this.hits = 0;
        this.almost = 0;
        this.totalHits = 0;
        this.totalAlmost = 0;
        this.guesses = 0;
        bestGuess = 0;
    }

    public Number getGameNum() {
        return gameNum;
    }
    public Number getSysNum() {
        return sysNum;
    }
    // Input number setter
    public boolean setGameNum(String gameNumber, Context context) {
        if (isValid(gameNumber,context)) {
            this.gameNum.setNum(gameNumber);
            this.almost = this.gameNum.howManyAlmosts(this.sysNum);
            //this.totalAlmost += this.almost;
            this.hits = this.gameNum.howManyHits(this.sysNum);
            //this.totalHits += this.hits;
            this.guesses++;
            return true;
        }else{
            this.gameNum.setNum(null);
            Log.d(TAG, context.getString(R.string.Game_GameNum_Setter_InputNotValid));
            return false;
        }
    }
    // Random number setter
    public void setSysNum(){
        this.sysNum.createRandNum();
        Log.d(TAG,"Random num: " + sysNum.getNum());
    }
    // Input validation
    public boolean isValid(String num, Context context){
        if(num.trim().length()==0){
            // No input number
            Toast.makeText(context, context.getString(R.string.NoInputError), Toast.LENGTH_LONG).show();
            return false;
        }else if(!Number.IsUnique(num)){
            // Input number dosn't contain 4 diff. digits
            Toast.makeText(context, context.getString(R.string.No4DiffNumbersError), Toast.LENGTH_LONG).show();
            return false;
        }if(!Number.IsNumbersSizeEqual(num,this.sysNum)){
            // Numbers are not equal
            Toast.makeText(context, context.getString(R.string.WrongInputSizeError), Toast.LENGTH_LONG).show();
            return false;
        }
    return true;
}

}

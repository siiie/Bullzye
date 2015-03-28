package com.mobilerealtimesystems.bullzye;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;

public class Number {
    private String Num;

    // Empty c'tor
    public Number() {
        this.Num = null;
    }
    // c'tor
    public Number(String num) {
        this.Num = num;
    }
    // Num setter
    public void setNum(String num) {
        Num = num;
    }
    // Num getter
    public String getNum() {
        return Num;
    }

    // Amount of Hits between two numbers
    public int howManyHits(Number other){
        String check = this.getNum();
        String otherCheck = other.getNum();
        int tmpHits=0;
        for (int i=0;i<check.length();i++){
            if (otherCheck.charAt(i)==check.charAt(i)){
                tmpHits++;
            }
        }
        return tmpHits;
    }
    // Amount of Almost between two numbers
    public int howManyAlmosts(Number other){
        String check = this.getNum();
        String otherCheck = other.getNum();
        int tmpAlmost=0;
        for(int i=0;i<check.length();i++){
            if((otherCheck.indexOf(check.charAt(i)) != -1)
                    && (otherCheck.indexOf(check.charAt(i)) != i)){
                tmpAlmost++;
            }

        }
        return tmpAlmost;
    }
    // Random number generator
    public void createRandNum(){
        StringBuilder stringBuilder = new StringBuilder();
        Set<Integer> set = new HashSet<Integer>(4);
        Random rand = new Random();
        while (set.size() < 4) {
            set.add(rand.nextInt(9));
        }

        for (Integer imt : set) {
            stringBuilder.append(imt);
        }
        this.setNum(stringBuilder.toString());
    }
    // Size comparison between two numbers
    public static Boolean IsNumbersSizeEqual(String gameNum, Number sysNum){
        if(gameNum.length() == sysNum.getNum().length()){
            return true;
        }else{
            return false;
        }
    }
    // Number contain different digits checker
    public static Boolean IsUnique(String num){
        for (int i = 0; i < num.length(); i++) {
            String tmpChar = String.valueOf(num.charAt(i));
            if ((num.split(Pattern.quote(tmpChar),-1).length-1)>1){
                return false;
            }
        }
        return true;
    }

}

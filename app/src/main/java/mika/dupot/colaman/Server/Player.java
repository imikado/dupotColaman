package mika.dupot.colaman.Server;

import android.util.Log;

import java.util.Hashtable;

import mika.dupot.colaman.Domain.GamePlay;

public class Player {

    private int x;
    private int y;
    private String user;
    private boolean canAddBomb=true;
    private boolean isAlive=true;

    public  Player(String user_, int x_, int y_) {
        x = x_;
        y = y_;
        user = user_;
    }

    public void setCoord(int x_,int y_){
        x=x_;
        y=y_;
    }

    public void kill(){

        isAlive=false;
    }
    public void relive(){
        isAlive=true;
    }

    public boolean canAddBomb(){

        Log.i("Player","canAddBomb ?");
        if(canAddBomb){
            Log.i("Player","yes");
        }else{
            Log.i("Player","no");
        }
        return canAddBomb;
    }

    public void allowAddBomb(){
        Log.i("Player","allowAddBomb");
        canAddBomb=true;
    }

    public void denyAddBomb(){
        Log.i("Player","denyAddBomb");
        canAddBomb=false;
    }


    public int getX(){
        return x;
    }
    public String getXString(){
        return Integer.toString(x);
    }
    public int getY(){
        return y;
    }
    public String getYString(){
        return Integer.toString(y);
    }
    public String getUser(){
        return user;
    }

    public Hashtable<String,String> getAnswerDrawUser(){

        Hashtable<String,String> oAnswer=new Hashtable<String,String>();
        oAnswer.put(GamePlay.FIELD_STATUS,GamePlay.STATUS_OK);
        oAnswer.put(GamePlay.FIELD_ACTION,GamePlay.ACTION_DRAWUSER);
        oAnswer.put(GamePlay.FIELD_X, getXString());
        oAnswer.put(GamePlay.FIELD_Y, getYString());
        oAnswer.put(GamePlay.FIELD_USER, getUser());

        return oAnswer;
    }

    public Hashtable<String,String> getAnswerDrawAddBomb(){

        Hashtable<String,String> oAnswer=new Hashtable<String,String>();
        oAnswer.put(GamePlay.FIELD_STATUS,GamePlay.STATUS_OK);
        oAnswer.put(GamePlay.FIELD_ACTION,GamePlay.ACTION_ADDBOMB);
        oAnswer.put(GamePlay.FIELD_X, getXString());
        oAnswer.put(GamePlay.FIELD_Y, getYString());
        oAnswer.put(GamePlay.FIELD_USER, getUser());

        return oAnswer;
    }

    public Hashtable<String,String> getAnswerKilled(){

        Hashtable<String,String> oAnswer=new Hashtable<String,String>();
        oAnswer.put(GamePlay.FIELD_STATUS,GamePlay.STATUS_OK);
        oAnswer.put(GamePlay.FIELD_ACTION,GamePlay.ACTION_GAMEOVER);
        oAnswer.put(GamePlay.FIELD_X, getXString());
        oAnswer.put(GamePlay.FIELD_Y, getYString());
        oAnswer.put(GamePlay.FIELD_USER, getUser());

        return oAnswer;
    }

    public boolean isAlive() {
        return isAlive;
    }
}

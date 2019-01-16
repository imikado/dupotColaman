package mika.dupot.colaman.Server;

import android.util.Log;

import java.util.Hashtable;

import mika.dupot.colaman.Domain.GamePlay;

public class Flame {

    private int x;
    private int y;
    private String user;
    private int iCycle=0;
    private int iLastCycle=2;

    public  Flame(String user_, int x_, int y_) {
        x = x_;
        y = y_;
        user = user_;
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

    public void nextCycle() {
        iCycle++;
        Log.i("Flame server","cycle:"+Integer.toString(iCycle));

    }

    public boolean willDisapear() {
        if (iCycle > iLastCycle) {
            return true;
        }
        return false;
    }

    public Hashtable<String,String> getAnswerAddFlame(){
        Hashtable<String,String> oMessage=new Hashtable<String,String>();
        oMessage.put(GamePlay.FIELD_ACTION,GamePlay.ACTION_ADDFLAME);
        oMessage.put(GamePlay.FIELD_X,getXString());
        oMessage.put(GamePlay.FIELD_Y,getYString());
        oMessage.put(GamePlay.FIELD_USER,getUser());



        return oMessage;
    }

    public Hashtable<String,String> getAnswerRemoveFlame() {
        Hashtable<String,String> oMessage=new Hashtable<String,String>();
        oMessage.put(GamePlay.FIELD_ACTION,GamePlay.ACTION_REMOVEFLAME);
        oMessage.put(GamePlay.FIELD_X,getXString());
        oMessage.put(GamePlay.FIELD_Y,getYString());


        return oMessage;
    }

    public Hashtable<String,String> getAnswerRemoveWallBreakable() {
        Hashtable<String,String> oMessage=new Hashtable<String,String>();
        oMessage.put(GamePlay.FIELD_ACTION,GamePlay.ACTION_REMOVEWALLBREAKABLE);
        oMessage.put(GamePlay.FIELD_X,getXString());
        oMessage.put(GamePlay.FIELD_Y,getYString());


        return oMessage;
    }
}

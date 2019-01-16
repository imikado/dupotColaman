package mika.dupot.colaman.Server;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Hashtable;

import mika.dupot.colaman.Domain.GamePlay;

public class Bomb {

    private int x;
    private int y;


    private String user;
    private int iCycle=0;
    private int iLastCycle=3;

    public  Bomb(String user_, int x_, int y_) {
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

    public String getUser() {
        return user;
    }

    public void nextCycle() {
        iCycle++;

    }

    public boolean willExplose() {
        if (iCycle > iLastCycle) {
            return true;
        }
        return false;
    }

    public Hashtable<String,String> getAnswerExploseBomb(){
        Hashtable<String,String> oMessage=new Hashtable<String,String>();
        oMessage.put(GamePlay.FIELD_ACTION,GamePlay.ACTION_EXPLOSEBOMB);
        oMessage.put(GamePlay.FIELD_X,getXString());
        oMessage.put(GamePlay.FIELD_Y,getYString());

        return oMessage;
    }



    public ArrayList<Point> getListFlames(){

        boolean continueUp=true;
        boolean continueDown=true;
        boolean continueLeft=true;
        boolean continueRight=true;

        ArrayList<Point> tListFlames=new ArrayList<Point>();

        //origin
        Point pointOrigin=new Point((getX()),(getY()));
        tListFlames.add(pointOrigin);

        for(Integer i=1;i< 3;i++) {
            //up
            Point pointUp=new Point((getX()),(getY()-i));
            if(isWallBreakable(pointUp) && continueUp) {
                tListFlames.add(pointUp);
                continueUp = false;
            }else if(isFree(pointUp) && continueUp){
                tListFlames.add(pointUp);
            }else{
                continueUp=false;
            }


            //down
            Point pointDown=new Point((getX()),(getY()+i));
            if(isWallBreakable(pointDown) && continueDown) {
                tListFlames.add(pointDown);
                continueDown = false;
            }else if(isFree(pointDown) && continueDown){
                tListFlames.add(pointDown);
            }else{
                continueDown=false;
            }


            //left
            Point pointLeft=new Point((getX()-i),(getY()));
            if(isWallBreakable(pointLeft) && continueLeft) {
                tListFlames.add(pointLeft);
                continueLeft = false;
            }else if(isFree(pointLeft) && continueLeft){
                tListFlames.add(pointLeft);
            }else{
                continueLeft=false;
            }


            //right
            Point pointRight=new Point((getX()+i),(getY()));
            if(isWallBreakable(pointRight) && continueRight) {
                tListFlames.add(pointRight);
                continueRight = false;
            }else if(isFree(pointRight) && continueRight){
                tListFlames.add(pointRight);
            }else{
                continueRight=false;
            }


        }

        return tListFlames;
    }


    private boolean isFree(Point pointFlame){
        return GamePlay.getInstance().isFree(pointFlame.x,pointFlame.y);
    }

    private boolean isWallBreakable(Point pointFlame){
        return GamePlay.getInstance().isWallBreakable(pointFlame.x,pointFlame.y);
    }



}

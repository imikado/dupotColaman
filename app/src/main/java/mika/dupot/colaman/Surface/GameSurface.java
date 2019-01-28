package mika.dupot.colaman.Surface;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import mika.dupot.colaman.Domain.GamePlay;
import mika.dupot.colaman.GameActivity;
import mika.dupot.colaman.R;
import mika.dupot.colaman.Socket.MyWebSocketClient;
import mika.dupot.colaman.threads.GameThread;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback , Runnable{

    /**
     * Holds the surface frame
     */
    private SurfaceHolder holder;

    /**
     * Draw thread
     */
    private Thread drawThread;

    /**
     * True when the surface is ready to draw
     */
    private boolean surfaceReady = false;


    /**
     * Drawing thread flag
     */

    private boolean drawingActive = false;

    /**
     * Time per frame for 60 FPS
     */
    private static final int MAX_FRAME_TIME = (int) (1000.0 / 30.0);

    private static final String LOGTAG = "surface";

    //-----

    public static GameSurface oInstance;

    private GameThread gameThread;

    private static Player[] tPlayer;
    private static ArrayList<Bomb> tBomb;
    private static HashMap<String, Flame> tFlame;

    private Wall oWall;
    private Wall oWallBreakable;

    private Case oCase;

    //private static ArrayList<Point> tDrawWall = new ArrayList<Point>();
    private static HashMap<String,Point> tDrawWall = new HashMap<String,Point>();
    private static HashMap<String,Point> tDraWallBreakable = new HashMap<String,Point>();
    private static HashMap<String, Point> tDrawUser = new HashMap<String, Point>();

    public static boolean isAlive=true;
    public static boolean isGameEnded=false;

    private int tMapOff[][];

    private int previousX;
    private int previousY;

    private static String currentUser;

    private Canvas oCanvas;

    public static float scaledDensity;
    private Surface sf;

    public GameActivity oGameActivity;

    private Score oScoreRed;
    private Score oScoreBlue;
    private Score oScoreGreen;
    private Score oScoreYellow;

    public static String sScoreRed="0";
    public static String sScoreBlue="0";
    public static String sScoreGreen="0";
    public static String sScoreYellow="0";


    public GameSurface(Context context) {
        super(context);

        scaledDensity = getResources().getDisplayMetrics().scaledDensity;

        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true);


    }

    private static Player getPlayerCurrent() throws Exception {
        for (int i = 0; i < tPlayer.length; i++) {
            if (tPlayer[i].getColor().equals(currentUser)) {
                return tPlayer[i];
            }
        }
        throw new Exception("GameSurface: getPlayerCurrent() No currentUser selected");
    }

    public static void startGame() {
        isGameEnded=false;
        isAlive=true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        try {

            if (isGameEnded == true) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    MyWebSocketClient.oInstance.send(GamePlay.getInstance().sendRestartGame());
                }

                return true;
            }

            if (isAlive == false) {
                Log.i("GameSurface", "is Game over, send touch");

                return false;
            }


            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                this.previousX = (int) event.getX();
                this.previousY = (int) event.getY();


                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {


                Player oPlayerCurrent;
                try {
                    oPlayerCurrent = getPlayerCurrent();
                } catch (Exception e) {
                    Log.e("GameSurface Error", e.getMessage().toString());
                    return false;
                }

                int currentX = (int) event.getX();
                int currentY = (int) event.getY();

                int deltaHoriz = Math.abs((previousX - currentX));
                int deltaVert = Math.abs((previousY - currentY));

                if (deltaHoriz < 50 && deltaVert < 50) {
                    //this.addBomb();

                    Log.i("GameSurface","try to send :");
                    String messageToSend=GamePlay.getInstance().sendAskAddBomb(currentUser, oPlayerCurrent.getX(), oPlayerCurrent.getY());
                    Log.i("GameSurface","message to send:"+messageToSend);

                    MyWebSocketClient.oInstance.send(messageToSend);

                    Log.i("GameSurface","message sent");


                    return true;
                }


                if (deltaHoriz > deltaVert) {
                    //horiz

                    if (currentX > previousX) {//right

                        MyWebSocketClient.oInstance.send(GamePlay.getInstance().sendAskMove(currentUser, oPlayerCurrent.getX() + 1, oPlayerCurrent.getY() + 0));

                        //oPlayerCurrent.updateCoord(1, 0);

                        Log.i("MOVE", "update coord horiz right");
                    } else {//left
                        //oPlayerCurrent.updateCoord(-1, 0);

                        MyWebSocketClient.oInstance.send(GamePlay.getInstance().sendAskMove(currentUser, oPlayerCurrent.getX() - 1, oPlayerCurrent.getY() + 0));


                        Log.i("MOVE", "update coord horiz left");
                    }

                } else {
                    //vert

                    if (currentY > previousY) {//bottom
                        //oPlayerCurrent.updateCoord(0, 1);

                        MyWebSocketClient.oInstance.send(GamePlay.getInstance().sendAskMove(currentUser, oPlayerCurrent.getX() + 0, oPlayerCurrent.getY() + 1));


                        Log.i("MOVE", "update coord horiz bottom");
                    } else {//left
                        //oPlayerCurrent.updateCoord(0, -1);

                        MyWebSocketClient.oInstance.send(GamePlay.getInstance().sendAskMove(currentUser, oPlayerCurrent.getX() + 0, oPlayerCurrent.getY() - 1));


                        Log.i("MOVE", "update coord horiz topt");
                    }

                }


                return true;
            }


            return false;

        }catch(Exception e){

            Log.e("GameSurface","onTouchEvent: "+e.getMessage());

            return false;
        }
    }

    public void initMap(Canvas oCanvas_) {

        for(int y=0;y<=16;y++){
            for(int x=0;x<=10;x++){

                oCase.drawCoord(oCanvas_, x, y);
                String sCoordKey=x+"_"+y;

                if(tDrawWall.containsKey(sCoordKey)){
                    oWall.drawCoord(oCanvas_,tDrawWall.get(sCoordKey).x,tDrawWall.get(sCoordKey).y);
                }
                if(tDraWallBreakable.containsKey(sCoordKey)){
                    oWallBreakable.drawCoord(oCanvas_,tDraWallBreakable.get(sCoordKey).x,tDraWallBreakable.get(sCoordKey).y);
                }


            }
        }



        return;


    }

    public void resetGame(){
        isAlive=true;
        isGameEnded=false;

        tDraWallBreakable.clear();
        tDrawUser.clear();
        tDrawWall.clear();

        tFlame.clear();
        tBomb.clear();

        for (int i = 0; i < tPlayer.length; i++) {
            tPlayer[i].relive();
        }

    }

    public static void executeMessageFromClient(Hashtable<String, String> oMessage_) {

        String actionMessage = oMessage_.get(GamePlay.FIELD_ACTION);

        Log.i("GameSurface", "executeMessageFromClient, action:" + actionMessage);

        ArrayList<String> tObjectToDraw = new ArrayList<String>();
        tObjectToDraw.add(GamePlay.ACTION_DRAWWALL);
        tObjectToDraw.add(GamePlay.ACTION_DRAWWALLBREAKABLE);
        tObjectToDraw.add(GamePlay.ACTION_DRAWUSER);
        tObjectToDraw.add(GamePlay.ACTION_ADDBOMB);
        tObjectToDraw.add(GamePlay.ACTION_ADDFLAME);


        if (tObjectToDraw.contains(actionMessage)) {

            Point oPoint = new Point(Integer.parseInt(oMessage_.get(GamePlay.FIELD_X)), Integer.parseInt(oMessage_.get(GamePlay.FIELD_Y)));

            if (actionMessage.equals(GamePlay.ACTION_DRAWWALL)) {

                tDrawWall.put(Integer.toString(oPoint.x)+"_"+Integer.toString(oPoint.y),oPoint);
            } else if (actionMessage.equals(GamePlay.ACTION_DRAWWALLBREAKABLE)) {

                tDraWallBreakable.put(Integer.toString(oPoint.x)+"_"+Integer.toString(oPoint.y),oPoint);
            } else if (actionMessage.equals(GamePlay.ACTION_DRAWUSER)) {

                String user = oMessage_.get(GamePlay.FIELD_USER);

                tDrawUser.put(user, oPoint);

                Log.i("GameSurface", "drawUser x:" + oPoint.x);
            } else if (actionMessage.equals(GamePlay.ACTION_ADDBOMB)) {
                //stackDrawBomb.push(oPoint);

                drawBomb(oPoint.x, oPoint.y);

            } else if (actionMessage.equals(GamePlay.ACTION_ADDFLAME)) {
                Log.i("GameSurface", "add drawFlame");
                //stackDrawFlame.push(oPoint);
                drawFlame(oPoint.x, oPoint.y);

            }


        } else if (actionMessage.equals(GamePlay.ACTION_SETUSER)) {
            currentUser = oMessage_.get(GamePlay.FIELD_USER);
        } else if (actionMessage.equals(GamePlay.ACTION_EXPLOSEBOMB)) {

            Log.i("GameSurface", "exploseBomb");
            int xBombToDelete = Integer.parseInt(oMessage_.get(GamePlay.FIELD_X));
            int yBombToDelete = Integer.parseInt(oMessage_.get(GamePlay.FIELD_Y));


            removeBomb(xBombToDelete, yBombToDelete);
            //stackBombToRemove.push(new Point(xBombToDelete, yBombToDelete));


        } else if (actionMessage.equals(GamePlay.ACTION_REMOVEFLAME)) {

            Log.i("GameSurface", "remove flame");
            int xFlameToDelete = Integer.parseInt(oMessage_.get(GamePlay.FIELD_X));
            int yFlameToDelete = Integer.parseInt(oMessage_.get(GamePlay.FIELD_Y));

            removeFlame(xFlameToDelete, yFlameToDelete);
            //stackFlameToRemove.push(new Point(xFlameToDelete, yFlameToDelete));

        } else if (actionMessage.equals(GamePlay.ACTION_REMOVEWALLBREAKABLE)) {


            int xWallToDelete = Integer.parseInt(oMessage_.get(GamePlay.FIELD_X));
            int yWallToDelete = Integer.parseInt(oMessage_.get(GamePlay.FIELD_Y));


            removeWallBreakable(xWallToDelete, yWallToDelete);
            //stackWallBreakableToRemove.push(new Point(xWallToDelete, yWallToDelete));


        }else if(actionMessage.equals(GamePlay.ACTION_GAMEOVER)) {

            Log.i("GameSurface", "GAMEOVER");
            Player oCurrentPlayer = null;
            try {
                oCurrentPlayer = getPlayerCurrent();
                oCurrentPlayer.kill();
            } catch (Exception e) {

            }
            isAlive = false;



            GameSurface .oInstance.oGameActivity.goGameOver();

        }else if(actionMessage.equals(GamePlay.ACTION_RESTARTGAME)) {

            Log.i("GameSurface","ACTION_RESTARTGAME");

            //resetGame();

        }else if(actionMessage.equals(GamePlay.ACTION_GAMEENDED)){



            Log.i("GameSurface", "GAME ENDED ?");
            if(oMessage_.get(GamePlay.FIELD_USER).equals(currentUser)){
                Log.i("GameSurface", "not "+oMessage_.get(GamePlay.FIELD_USER)+" = currentUser "+currentUser);
                isGameEnded=true;

                GameSurface .oInstance.oGameActivity.goGameOverServer();
            }

        }else if(actionMessage.equals(GamePlay.ACTION_SETSCORE)){
            if(oMessage_.get(GamePlay.FIELD_USER).equals("red")){
                sScoreRed=oMessage_.get(GamePlay.FIELD_SCORE);
            }else  if(oMessage_.get(GamePlay.FIELD_USER).equals("blue")){
                sScoreBlue=oMessage_.get(GamePlay.FIELD_SCORE);
            }else  if(oMessage_.get(GamePlay.FIELD_USER).equals("green")){
                sScoreGreen=oMessage_.get(GamePlay.FIELD_SCORE);
            }else  if(oMessage_.get(GamePlay.FIELD_USER).equals("yellow")){
                sScoreYellow=oMessage_.get(GamePlay.FIELD_SCORE);
            }
        }



    }


    public void render(Canvas canvas) {
        try {
            super.draw(canvas);


            initMap(canvas);


            for (int i = 0; i < tDrawUser.size(); i++) {

                Player oPlayer = tPlayer[i];

                if(oPlayer.isAlive()) {
                    Point oPoint = tDrawUser.get(oPlayer.getColor());
                    oPlayer.setCoord(oPoint.x, oPoint.y);

                    oPlayer.draw(canvas);
                    oPlayer.update();
                }
            }


            //Log.i("GameSurface","start draw bomb and flame ");

            for (int i = 0; i < tBomb.size(); i++) {
                //Log.i("GameSurface", "loop tBomb");
                tBomb.get(i).draw(canvas);
                tBomb.get(i).update();
            }


            for (Flame oFlame : tFlame.values()) {

                //for (int i = 0; i < tFlame.size(); i++) {
                oFlame.draw(canvas);
                oFlame.update();
            }


            if(false==isAlive){
                //Log.i("GameSurface","draw text GAME OVER");

                drawText(canvas,"GAME OVER");
            }

            if(true==isGameEnded){
                Log.i("GameSurface","draw text Game ended");
                drawSubText(canvas,"La partie est finie, RETOUR touchez l'ecran RETOUR pour relancer");


            }


            //score
            oScoreRed.drawCoord(canvas,0,17,sScoreRed);

            oScoreBlue.drawCoord(canvas,2,17,sScoreBlue);

            oScoreGreen.drawCoord(canvas,4,17,sScoreGreen);

            oScoreYellow.drawCoord(canvas,6,17,sScoreYellow);

        /*

        if(this.oBomb!=null){
            this.oBomb.draw(canvas);
            this.oBomb.update();
        }

        */

        } catch (Exception e) {
            Log.e("GameSurface", "Error:" + e.getMessage());
        }
    }

    public void drawText(Canvas canvas,String text_){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.BLUE);

        //paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10*scaledDensity);
        //paint.setColor(Color.BLACK);

        paint.setTextAlign(Paint.Align.CENTER);

        paint.setTextSize(60*scaledDensity);

        canvas.drawText(text_, getWidth()/2, getHeight()/2, paint);



        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        paint.setColor(Color.WHITE);

        paint.setTextAlign(Paint.Align.CENTER);

        paint.setTextSize(60*scaledDensity);

        canvas.drawText(text_, getWidth()/2, getHeight()/2, paint);


    }

    public void drawSubText(Canvas canvas,String text_){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.BLUE);

        //paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8*scaledDensity);
        //paint.setColor(Color.BLACK);

        paint.setTextAlign(Paint.Align.CENTER);

        paint.setTextSize(30*scaledDensity);

        String[] listLine = text_.split("RETOUR");

        for(int i=0;i< listLine.length;i++){
            canvas.drawText(listLine[i], getWidth()/2, getHeight()/2+(i*30*scaledDensity)+(50*scaledDensity), paint);

        }


        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);

        //paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(0);
        //paint.setColor(Color.BLACK);

        paint.setTextAlign(Paint.Align.CENTER);

        paint.setTextSize(30*scaledDensity);

        for(int i=0;i< listLine.length;i++){
            canvas.drawText(listLine[i], getWidth()/2, getHeight()/2+(i*30*scaledDensity)+(50*scaledDensity), paint);

        }



    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        this.holder = holder;
        //this.setBackgroundColor(0Xffffffff);

        Bitmap persoRedBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.persored);
        Bitmap persoBlueBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.persoblue);
        Bitmap persoGreenBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.persogreen);
        Bitmap persoYellowBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.persoyellow);

        Bitmap scoreRedBitmap= BitmapFactory.decodeResource(this.getResources(),R.drawable.scorered);
        Bitmap scoreBlueBitmap= BitmapFactory.decodeResource(this.getResources(),R.drawable.scoreblue);
        Bitmap scoreGreenBitmap= BitmapFactory.decodeResource(this.getResources(),R.drawable.scoregreen);
        Bitmap scoreYellowBitmap= BitmapFactory.decodeResource(this.getResources(),R.drawable.scoreyellow);

        oScoreRed=new Score(scoreRedBitmap,getWidth(),scaledDensity);
        oScoreBlue=new Score(scoreBlueBitmap,getWidth(),scaledDensity);
        oScoreGreen=new Score(scoreGreenBitmap,getWidth(),scaledDensity);
        oScoreYellow=new Score(scoreYellowBitmap,getWidth(),scaledDensity);

        tBomb = new ArrayList<Bomb>();
        tFlame = new HashMap<String, Flame>();

        tPlayer = new Player[4];

        tPlayer[0] = new Player(this, persoRedBitmap, "red", getWidth(), scaledDensity, 1, 1);
        tPlayer[1] = new Player(this, persoBlueBitmap, "blue", getWidth(), scaledDensity, 11, 1);
        tPlayer[2] = new Player(this, persoGreenBitmap, "green", getWidth(), scaledDensity, 11, 15);
        tPlayer[3] = new Player(this, persoYellowBitmap, "yellow", getWidth(), scaledDensity, 1, 15);

        Bitmap wallbitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.wall);

        Log.i("GameSurface", "widthPixels:" + getResources().getDisplayMetrics().widthPixels);
        Log.i("GameSurface", "width:" + getWidth());
        Log.i("GameSurface", "scale" + getResources().getDisplayMetrics().scaledDensity);


        this.oWall = new Wall(this, wallbitmap, getWidth(), scaledDensity);
        this.oWall.setImage(wallbitmap);

        Bitmap wallBitmapBreakable = BitmapFactory.decodeResource(this.getResources(), R.drawable.wallbreakable);

        this.oWallBreakable = new Wall(this, wallBitmapBreakable, getWidth(), scaledDensity);
        this.oWallBreakable.setImage(wallBitmapBreakable);

        Bitmap caseBitmapBreakable = BitmapFactory.decodeResource(this.getResources(), R.drawable.back);

        this.oCase = new Case(this, caseBitmapBreakable, getWidth(), scaledDensity);


        if (drawThread != null){
            Log.d(LOGTAG, "draw thread still active..");
            drawingActive = false;
            try{
                drawThread.join();
            } catch (InterruptedException e){}
        }

        surfaceReady = true;
        startDrawThread();
    }

    public void startDrawThread(){
        if (surfaceReady && drawThread == null){
            drawThread = new Thread(this, "Draw thread");
            drawingActive = true;
            drawThread.start();
        }
    }

    @Override
    public void run() {
        Log.d(LOGTAG, "Draw thread started");
        long frameStartTime;
        long frameTime;

        /*
         * In order to work reliable on Nexus 7, we place ~500ms delay at the start of drawing thread
         * (AOSP - Issue 58385)
         */
        if (Build.BRAND.equalsIgnoreCase("google") && Build.MANUFACTURER.equalsIgnoreCase("asus") && Build.MODEL.equalsIgnoreCase("Nexus 7")) {
            Log.w(LOGTAG, "Sleep 500ms (Device: Asus Nexus 7)");
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }

        while (drawingActive) {
            if (holder == null) {
                return;
            }

            frameStartTime = System.nanoTime();
            Canvas canvas = holder.lockCanvas();
            if (canvas != null) {
                try {
                    synchronized (holder) {
                        //tick();
                        render(canvas);
                    }
                } finally {

                    holder.unlockCanvasAndPost(canvas);
                }
            }

            // calculate the time required to draw the frame in ms
            frameTime = (System.nanoTime() - frameStartTime) / 1000000;

            if (frameTime < MAX_FRAME_TIME){
                try {
                    Thread.sleep(MAX_FRAME_TIME - frameTime);
                } catch (InterruptedException e) {
                    // ignore
                }
            }

        }
        Log.d(LOGTAG, "Draw thread finished");
    }



    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public static void removeBomb(int x_, int y_) {
        for (int iBomb = 0; iBomb < tBomb.size(); iBomb++) {

            Bomb oBomb = tBomb.get(iBomb);

            if (oBomb.getX() == x_ && oBomb.getY() == y_) {
                tBomb.remove(iBomb);
                break;
            }
        }
    }

    public static void removeFlame(int x_, int y_) {

        tFlame.remove(Integer.toString(x_)+"_"+Integer.toString(y_));

        /*
        for (int iFlame = 0; iFlame < tFlame.size(); iFlame++) {

            Flame oFlame = tFlame.get(iFlame);

            Log.i("GameSurface", "flameToRemove ? ");
            if (oFlame.getX() == x_ && oFlame.getY() == y_) {
                tFlame.remove(iFlame);
                break;
            }
        }*/
    }

    public static String getKeyStringFromCoord(int x_,int y_){
        return Integer.toString(x_)+"_"+Integer.toString(y_);
    }

    public static void removeWallBreakable(int x_, int y_) {

        tDraWallBreakable.remove( getKeyStringFromCoord(x_,y_) );

        /*
        for (int iDraWallBreakable = 0; iDraWallBreakable < tDraWallBreakable.size(); iDraWallBreakable++) {

            Point oDraWallBreakablePoint = tDraWallBreakable.get(iDraWallBreakable);

            if (oDraWallBreakablePoint.x == x_ && oDraWallBreakablePoint.y == y_) {
                tDraWallBreakable.remove(iDraWallBreakable);
            }
        }
    */
    }

    public static void drawBomb(int x_, int y_) {
        Bitmap bombBitmap = BitmapFactory.decodeResource(oInstance.getResources(), R.drawable.bomb);

        Bomb oBomb = new Bomb(oInstance, bombBitmap, "red", oInstance.getWidth(), scaledDensity, x_, y_);

        tBomb.add(oBomb);
    }

    public static void drawFlame(int x_, int y_) {

        Bitmap flameBitmap = BitmapFactory.decodeResource(oInstance.getResources(), R.drawable.flame);

        Flame oFlame = new Flame(oInstance, flameBitmap, "red", oInstance.getWidth(), scaledDensity, x_, y_);

        tFlame.put(oFlame.getXString() + '_' + oFlame.getYString(), oFlame);
    }


    /**
     * Stops the drawing thread
     */
    public void stopDrawThread(){
        if (drawThread == null){
            Log.d(LOGTAG, "DrawThread is null");
            return;
        }
        drawingActive = false;
        while (true){
            try{
                Log.d(LOGTAG, "Request last frame");
                drawThread.join(5000);
                break;
            } catch (Exception e) {
                Log.e(LOGTAG, "Could not join with draw thread");
            }
        }
        drawThread = null;
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface is not used anymore - stop the drawing thread
        stopDrawThread();
        // and release the surface
        holder.getSurface().release();

        this.holder = null;
        surfaceReady = false;
        Log.d(LOGTAG, "Destroyed");
    }



}

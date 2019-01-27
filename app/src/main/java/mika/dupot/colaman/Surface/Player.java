package mika.dupot.colaman.Surface;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import mika.dupot.colaman.Domain.GamePlay;

public class Player extends GameObject {

    private Bitmap[][] tSprites;

    private int caseWidth;
    private int spriteWidth;

    private int colCount = 4;
    private int colUsing = 0;

    private int rowUsing=0;

    private long lastDrawNanoTime;

    private String color;

    private float targetRealX;
    private float targetRealY;
    private int targetX;
    private int targetY;
    private float targetStep;

    private boolean isAlive=true;

    public static final int SPRITE_STABLE=0;
    public static final int SPRITE_RIGHT=1;
    public static final int SPRITE_LEFT=2;
    public static final int SPRITE_DOWN=3;
    public static final int SPRITE_UP=4;



    public boolean isAlive() {
        return isAlive;
    }

    public void relive(){ isAlive=true;}

    public void kill(){
        isAlive=false;
    }



    private GameSurface gameSurface;

    public Player(GameSurface gameSurface, Bitmap image, String color_, int widthGame,float scaledDensity, int x, int y) {
        super(image, 5, 4, x, y);

        this.color = color_;

        Log.i("Player","width game:"+widthGame);

        caseWidth = (widthGame / GamePlay.max);

        spriteWidth= (int) (85*scaledDensity);

        targetStep=spriteWidth/10;


        Log.i("Player","spriteWidth:"+spriteWidth);


        this.gameSurface = gameSurface;

        tSprites = new Bitmap[5][4];
        for(int row=0;row < 5;row ++) {
            for (int col = 0; col < 4; col++) {

                tSprites[row][col] = this.createSubImageAt(row, col);
            }
        }
    }

    public void updateRow(int row_){
        rowUsing=row_;
    }

    public String getColor(){
        return color;
    }

    public void setCoord(int x_, int y_) {
        if(x_!=this.x || y_!=this.y) {
            this.updateCoord(x_,y_);

        }else {
            this.x = x_;
            this.y = y_;

            this.realX = x * this.caseWidth;
            this.realY = y * this.caseWidth - (this.caseWidth / 2);
        }
    }

    public void updateCoord(int x_, int y_) {

        this.targetX=x_;
        this.targetY=y_;

        Log.i("PlayerUpdateCoord","x:"+Integer.toString(x_));
        Log.i("PlayerUpdateCoord","y:"+Integer.toString(y_));

        this.targetRealX = x_* this.caseWidth;
        this.targetRealY = y_*this.caseWidth - (this.caseWidth/2);


    }


    public Bitmap getCurrentMoveBitmap() {

        return tSprites[this.rowUsing][this.colUsing];
    }


    public void update() {
        this.colUsing++;
        if (colUsing >= this.colCount) {
            this.colUsing = 0;
        }
        // Current time in nanoseconds
        long now = System.nanoTime();

        // Never once did draw.
        if (lastDrawNanoTime == -1) {
            lastDrawNanoTime = now;
        }
        // Change nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        int deltaTime = (int) ((now - lastDrawNanoTime) / 1000000);

        // Distance moves

    }

    public void draw(Canvas canvas) {
        Bitmap bitmap = this.getCurrentMoveBitmap();

        //int x2 = x * this.caseWidth;
        //int y2 = y * this.caseWidth - (this.caseWidth/2);

        if(this.targetX != this.x){

            if(this.targetRealX > this.realX){
                updateRow(this.SPRITE_RIGHT);

                this.realX +=this.targetStep;
                if(this.realX >= this.targetRealX){
                    this.realX=this.targetRealX;

                    this.x=this.targetX;
                }
            }else{
                updateRow(this.SPRITE_LEFT);

                this.realX -=this.targetStep;
                if(this.realX <= this.targetRealX){
                    this.realX=this.targetRealX;

                    this.x=this.targetX;
                }
            }

        }else if(this.targetY != this.y){

            Log.i("player","tartgetRealY"+Float.toString(targetRealY));
            Log.i("player","realY"+Float.toString(realY));

            if(this.targetRealY > this.realY){

                Log.i("player"," DOWN ");

                updateRow(this.SPRITE_DOWN);

                this.realY +=this.targetStep;
                if(this.realY >= this.targetRealY){
                    this.realY=this.targetRealY;

                    this.y=this.targetY;
                }
            }else{
                Log.i("player"," UP ");

                updateRow(this.SPRITE_UP);

                this.realY -=this.targetStep;
                if(this.realY <= this.targetRealY){
                    this.realY=this.targetRealY;

                    this.y=this.targetY;
                }
            }
        }else{
            updateRow(SPRITE_STABLE);
        }


        canvas.drawBitmap(bitmap, new Rect(0, 0, spriteWidth, spriteWidth), new Rect( (int)realX, (int)realY, (int)((float)caseWidth + realX), (int)((float)this.caseWidth + realY )), null);


        // Last draw time.
        this.lastDrawNanoTime = System.nanoTime();
    }


}
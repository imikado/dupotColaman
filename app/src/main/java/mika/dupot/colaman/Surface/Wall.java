package mika.dupot.colaman.Surface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import mika.dupot.colaman.Domain.GamePlay;

public class Wall extends GameObject {
    private GameSurface gameSurface;

    private int caseWidth;
    private int spriteWidth;

    private int widthGame;

    //protected int x;
    //protected int y;

    public Wall(GameSurface gameSurface, Bitmap image, int widthGame,float scaledDensity) {
        super(image, 4, 3,0,0);

        this.gameSurface= gameSurface;

        caseWidth=widthGame/GamePlay.max;

        spriteWidth=(caseWidth*3);
        spriteWidth= (int) (80*scaledDensity);

        //this.x=x;
        //this.y=y;
    }

    public void drawCoord(Canvas canvas, int x,int y){
        Bitmap bitmap = image;

        int x2=x*this.caseWidth;
        int y2=y*this.caseWidth;


        canvas.drawBitmap(bitmap,new Rect(0,0,spriteWidth,spriteWidth),new Rect(0+x2,0+y2,this.caseWidth+x2,this.caseWidth+y2),null);

    }


}

package mika.dupot.colaman.Surface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import mika.dupot.colaman.Domain.GamePlay;

public class Wall extends GameObject {

    private GameSurface gameSurface;

    private int caseWidth;
    private int caseHeight;

    private int spriteWidth;
    private int spriteHeight;

    private int widthGame;

    private Bitmap bitmap;

    //protected int x;
    //protected int y;

    public Wall(GameSurface gameSurface, Bitmap image, int widthGame,float scaledDensity) {
        super(image, 4, 3,0,0);

        this.gameSurface= gameSurface;

        caseWidth=widthGame/GamePlay.max;
        caseHeight=(int)(caseWidth*1.20);

        spriteWidth= (int) (80*scaledDensity);

        spriteHeight=(int)(100*scaledDensity);

        //this.x=x;
        //this.y=y;
    }

    public void setImage(Bitmap oImage_){
        bitmap=oImage_;
    }

    public void drawCoord(Canvas canvas, int x,int y){
        //Bitmap bitmap = image;

        float x2=(x*this.caseWidth);
        float y2=(y*this.caseWidth)-((caseWidth/10)*2);

        canvas.drawBitmap(bitmap,new Rect(0,0,spriteWidth,spriteHeight),new Rect( (int)x2,(int)y2, (int)((float)this.caseWidth+x2),(int)((float)(this.caseHeight+y2))),null);

    }


}

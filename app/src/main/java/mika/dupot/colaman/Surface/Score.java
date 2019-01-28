package mika.dupot.colaman.Surface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import mika.dupot.colaman.Domain.GamePlay;

public class Score {

    private final int caseWidth;
    private final int caseHeight;
    private Bitmap bitmap;
    private int spriteWidth;
    private int spriteHeight;


    public Score( Bitmap oImage_,int widthGame,float scaledDensity) {

        caseWidth=widthGame/ GamePlay.max;
        caseHeight=(int)(caseWidth*0.8);

        bitmap=oImage_;

        spriteWidth= (int) (66*scaledDensity);

        spriteHeight=(int)(56*scaledDensity);

        //this.x=x;
        //this.y=y;
    }


    public void drawCoord(Canvas canvas, int x, int y,String number){

        int x2=x*caseWidth;
        int y2=y*caseWidth;


        Paint mGridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mGridPaint.setStyle(Paint.Style.FILL);
        mGridPaint.setColor(Color.WHITE);
        mGridPaint.setTextSize((int)(caseWidth*0.8));


        canvas.drawBitmap(bitmap,new Rect(0,0,spriteWidth,spriteHeight),new Rect( x2,y2, (int)(x2+caseWidth),(int)(y2+caseHeight)),null);

        canvas.drawText(number,(float)x2+caseWidth,(float)y2+(caseWidth/4)+(caseWidth/2),mGridPaint);
    }
}

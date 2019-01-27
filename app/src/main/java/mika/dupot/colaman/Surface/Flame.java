package mika.dupot.colaman.Surface;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import mika.dupot.colaman.Domain.GamePlay;

public class Flame extends GameObject {

    private Bitmap[] tSprites;

    private int caseWidth;
    private int spriteWidth;

    private int colCount = 7;
    private int colUsing = 0;

    private long lastDrawNanoTime;

    private String color;

    private GameSurface gameSurface;

    public Flame(GameSurface gameSurface, Bitmap image, String color_, int widthGame,float scaledDensity, int x, int y) {
        super(image, 1, 7, x, y);

        this.color = color_;

        caseWidth = widthGame / GamePlay.max;
        spriteWidth= (int) (80*scaledDensity);

        this.gameSurface = gameSurface;

        tSprites = new Bitmap[7];
        for (int i = 0; i < 7; i++) {

            tSprites[i] = this.createSubImageAt(0, i);
        }
    }




    public Bitmap getCurrentMoveBitmap() {

        return tSprites[this.colUsing];
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

        int x2 = x * this.caseWidth;
        int y2 = y * this.caseWidth - (this.caseWidth/2);

        canvas.drawBitmap(bitmap, new Rect(0, 0, spriteWidth, spriteWidth), new Rect(0 + x2, 0 + y2, this.caseWidth + x2, this.caseWidth + y2), null);


        // Last draw time.
        this.lastDrawNanoTime = System.nanoTime();
    }
}

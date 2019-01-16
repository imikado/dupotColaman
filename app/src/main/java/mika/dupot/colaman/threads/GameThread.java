package mika.dupot.colaman.threads;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

import mika.dupot.colaman.Surface.GameSurface;

public class GameThread extends Thread {

    /**
     * Time per frame for 60 FPS
     */
    private static final int MAX_FRAME_TIME = (int) (1000.0 / 60.0);

    private boolean running;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;

    public GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder)  {
        this.gameSurface= gameSurface;
        this.surfaceHolder= surfaceHolder;
    }

    @Override
    public void run()  {
        long startTime = System.nanoTime();

        while(running)  {
            Canvas canvas= null;
            try {
                // Get Canvas from Holder and lock it.
                canvas = this.surfaceHolder.lockCanvas();

                /*
                 * In order to work reliable on Nexus 7, we place ~500ms delay at the start of drawing thread
                 * (AOSP - Issue 58385)
                 */
                if (android.os.Build.BRAND.equalsIgnoreCase("google") && android.os.Build.MANUFACTURER.equalsIgnoreCase("asus") && android.os.Build.MODEL.equalsIgnoreCase("Nexus 7"))
                {
                    //Log.w("GameThread", "Sleep 500ms (Device: Asus Nexus 7)");
                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException ignored)
                    {
                    }
                }

                // Synchronized
                //synchronized (canvas)  {
                    //this.gameSurface.update();
                    this.gameSurface.draw(canvas);
                //}
            }catch(Exception e)  {
                // Do nothing.
            } finally {
                if(canvas!= null)  {
                    // Unlock Canvas.
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            long now = System.nanoTime() ;
            // Interval to redraw game
            // (Change nanoseconds to milliseconds)
            long waitTime = (now - startTime)/1000000;
            if(waitTime < MAX_FRAME_TIME)  {
                waitTime= MAX_FRAME_TIME-waitTime; // Millisecond.
            }
            //System.out.print(" Wait Time="+ waitTime);

            try {
                // Sleep.
                Thread.sleep(waitTime);
            } catch(InterruptedException e)  {

            }
            startTime = System.nanoTime();
            //System.out.print(".");
        }
    }

    public void setRunning(boolean running)  {
        this.running= running;
    }
}
package mika.dupot.colaman;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;

import mika.dupot.colaman.Socket.MyWebSocketServer;
import mika.dupot.colaman.Surface.GameSurface;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameSurface.oInstance=new GameSurface(this );

        setContentView(GameSurface.oInstance);

    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            if(MyWebSocketServer.oInstance!=null) {
                MyWebSocketServer.oInstance.stop();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

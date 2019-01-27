package mika.dupot.colaman;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import java.io.IOException;

import mika.dupot.colaman.Socket.MyWebSocketServer;
import mika.dupot.colaman.Surface.GameSurface;

public class GameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GameSurface.isAlive=true;
        GameSurface.isGameEnded=false;

        GameSurface.oInstance = new GameSurface(this);
        GameSurface.oInstance.oGameActivity = this;



        setContentView(GameSurface.oInstance);

    }

    public void goGameOver(){

        Intent oIntent = new Intent(this, GameOverActivity.class);
        startActivity(oIntent);

        finish();
    }

    public void goGameOverServer(){
        Intent oIntent = new Intent(this, GameOverServerActivity.class);
        startActivity(oIntent);

        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();


    }
}

package mika.dupot.colaman;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import mika.dupot.colaman.Domain.GamePlay;
import mika.dupot.colaman.Socket.MyWebSocketClient;

public class GameOverServerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game_over_server);

        Button oInput=findViewById(R.id.relaunchGame);
        oInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relaunchGame();
            }
        });
    }

    public void relaunchGame() {
        Intent oIntent = new Intent(this, GameActivity.class);
        MyWebSocketClient.oInstance.send(GamePlay.getInstance().sendRestartGame());
        startActivity(oIntent);



    }
}

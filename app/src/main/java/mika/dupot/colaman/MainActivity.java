package mika.dupot.colaman;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import mika.dupot.colaman.Socket.MyWebSocketServer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        String versionCode = BuildConfig.VERSION_NAME;

        TextView oText = findViewById(R.id.textView);

        oText.setText(  versionCode);

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

    public void gotoServer(View v) {
        Intent oIntent = new Intent(this, ServerActivity.class);
        startActivity(oIntent);


    }

    public void gotoClient(View v) {

        Intent oIntent = new Intent(this, ClientActivity.class);
        startActivity(oIntent);


    }
}

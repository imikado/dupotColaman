package mika.dupot.colaman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SplashscreenActivity extends AppCompatActivity {
    private static int splashScreenTimeOut=3000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent oIntent =new Intent(SplashscreenActivity.this,TitleActivity.class);
                startActivity(oIntent);
                finish();
            }
        },splashScreenTimeOut);
    }
}

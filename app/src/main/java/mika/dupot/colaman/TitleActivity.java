package mika.dupot.colaman;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class TitleActivity extends AppCompatActivity {
    private static int screenTimeOut=3000;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.title);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent oIntent =new Intent(TitleActivity.this,MainActivity.class);
                startActivity(oIntent);
                finish();
            }
        },screenTimeOut);
    }
}

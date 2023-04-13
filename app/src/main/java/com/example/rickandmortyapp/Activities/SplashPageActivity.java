package com.example.rickandmortyapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.example.rickandmortyapp.R;
public class SplashPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_page);

        TextView splash_text_view = findViewById(R.id.splash_text_view);

        if(getFirstTurnFromLocalDataSource()){
            splash_text_view.setText(getResources().getString(R.string.welcome));
            saveFirstTurnToLocalDataSource();
        }else{
            splash_text_view.setText(getResources().getString(R.string.hello));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashPageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );
    }

    //uygulamanın ilk çalıştığı zamanı kontrol etmek için
    private boolean getFirstTurnFromLocalDataSource() {

        String result;
        String CONST_DATA = "FIRST_TURN";
        SharedPreferences preferences = this.getSharedPreferences(CONST_DATA, getApplicationContext().MODE_PRIVATE);
        result = preferences.getString(CONST_DATA, "");

        return result.equals("") || result.equals("firstTurn");
    }

    //uygulamanın ilk çalıştığını kaydetmek için
    private void saveFirstTurnToLocalDataSource() {

        String CONST_DATA = "FIRST_TURN";
        SharedPreferences preferences = this.getSharedPreferences(CONST_DATA, getApplicationContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CONST_DATA, "noFirstTurn");
        editor.apply();
    }
}
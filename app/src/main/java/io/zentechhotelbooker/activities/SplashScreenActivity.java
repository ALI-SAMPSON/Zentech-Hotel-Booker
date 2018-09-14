package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import io.zentechhotelbooker.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN_DISPLAY_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){
            actionBar.hide();
        }
        //call to the splash screen method
        splashScreen();
    }

    //method to be called when the application is first started
    public void splashScreen(){

        Thread timer = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(SPLASH_SCREEN_DISPLAY_TIME);
                    //Creates and start the intent of the next activity
                    Intent intent = new Intent(SplashScreenActivity.this,SliderViewActivity.class);
                    startActivity(intent); //starts the instance of the Intent Class
                    finish(); //this prevents the app from going back to the splash screen
                    super.run();
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }
}

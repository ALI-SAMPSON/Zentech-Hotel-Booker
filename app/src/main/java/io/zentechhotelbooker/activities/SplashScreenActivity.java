package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;

import io.zentechhotelbooker.R;
import maes.tech.intentanim.CustomIntent;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_SCREEN_DISPLAY_TIME = 3000;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // firebase instance
        mAuth = FirebaseAuth.getInstance();

        // method call
        runAnimation();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            // starts the HomeActivity
            finish();
            startActivity(new Intent(SplashScreenActivity.this,HomeActivity.class));
            // Adds a fadein-fadeout animations to the activity
            CustomIntent.customType(SplashScreenActivity.this,"fadein-to-fadeout");
        }
        else{
            // open splash screen first
            splashScreen();
        }
    }

    //class to the handle the splash screen activity
    public void splashScreen() {

        Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(SPLASH_SCREEN_DISPLAY_TIME);
                    // this prevents the app from going back to the splash screen
                    finish();
                    // Creates and start the intent of the next activity
                    startActivity(new Intent(SplashScreenActivity.this, UserLoginActivity.class));
                    // Adds a fadein-fadeout animations to the activity
                    CustomIntent.customType(SplashScreenActivity.this,"fadein-to-fadeout");

                    super.run();
                } catch (InterruptedException e) {
                    // display a toast to user
                    Toast.makeText(SplashScreenActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        };
        //starts the timer
        timer.start();
    }

    // method to set animation on textViews
    private  void runAnimation(){

        // setting animation for the App Title on the splashScreen
        TextView hotel_title = findViewById(R.id.hotel_title);
        YoYo.with(Techniques.BounceIn).playOn(hotel_title);

        TextView theme  = findViewById(R.id.theme);
        YoYo.with(Techniques.BounceInUp).playOn(theme);

        // setting animation for the App watermark on the splashScreen
        TextView trademark = findViewById(R.id.trade_mark);
        YoYo.with(Techniques.BounceInLeft).playOn(trademark);

    }

}

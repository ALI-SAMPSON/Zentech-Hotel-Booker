package io.zentechhotelbooker.activities;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

public class UserLoginActivity extends AppCompatActivity {

    // global or class variables
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    //Email and password EditText
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView welcome_msg;
    private AppCompatButton btn_login;

    private NestedScrollView nestedScrollView;

    private Animation shake;

    private Animation scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // removes status bar and to make background fit Screen
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        //get reference to the EditText fields defined in the xml file
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        btn_login = findViewById(R.id.appCompatButtonLogin);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        progressBar = findViewById(R.id.progressBar);

        // setting animation on the progressBar
        /*ObjectAnimator animator = ObjectAnimator.ofInt(progressBar,"progress",0,100);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();*/

        mAuth = FirebaseAuth.getInstance();

        // method call
        animTextView();

        shake = AnimationUtils.loadAnimation(this,R.anim.anim_shake);

        scale = AnimationUtils.loadAnimation(this,R.anim.anim_scale_imageview);

    }

    @Override
    protected void onStart(){
        super.onStart();
        // checks if user is currently logged in
        if(mAuth.getCurrentUser() != null){
            UserLoginActivity.this.finish();
            startActivity(new Intent(UserLoginActivity.this,HomeActivity.class));
            // Adds a fadein-fadeout animations to the activity
            CustomIntent.customType(UserLoginActivity.this,"fadein-to-fadeout");
        }

}

    // method to add animation to TextView
    private void animTextView(){

        // getting the id of the view
        TextView welcome_msg = findViewById(R.id.welcome);

        // add animation to the TextView using YoYo Library
        YoYo.with(Techniques.FlipInX).playOn(welcome_msg);


    }

    //method to be called when the login Button is clicked or tapped
    public void onLoginButtonClick(View view){

        //get text from the EditText fields
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //checks if text entered are valid and textfields are not empty
        if(email.isEmpty()){
            // adds animation to the editText
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(R.string.error_empty_email));
            // editTextEmail.requestFocus();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            // adds animation to the editText
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(R.string.email_invalid));
            // editTextEmail.requestFocus();
            return;
        }
        else if(password.isEmpty()){
            // adds animation to the editText
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(R.string.error_empty_password));
            editTextPassword.requestFocus();
            return;
        }
        else if(password.length() < 6){
            // adds animation to the editText
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(R.string.error_password_length));
            editTextPassword.requestFocus();
            return;
        }
        else if(email.isEmpty() && password.isEmpty()){
            Toast.makeText(UserLoginActivity.this,"Email and Password are required fields",Toast.LENGTH_LONG).show();
        }
        else{
            // call to the LoginUser method
            loginUser();
        }

    }

    //method for logging user into the system
    public void loginUser(){

        // add animation to the button
        //btn_login.startAnimation(scale);

        //get text from the EditText fields
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Method to check if email is Verified
                            checkIfEmailIsVerified();

                        }
                        else{
                            // display a successful login message
                            Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    // Method to check if user's email is verified
    private void checkIfEmailIsVerified(){

        FirebaseUser user = mAuth.getCurrentUser();

        // variable to handle isEmailVerified
        boolean isEmailVerified = user.isEmailVerified();

        // user is verified, so you can finish this activity or send user to activity which you want.
        if(isEmailVerified){

            // display a successful login message
            Toast.makeText(UserLoginActivity.this,getString(R.string.login_successful),Toast.LENGTH_SHORT).show();

            clearBothTextFields();

            startActivity(new Intent(UserLoginActivity.this,HomeActivity.class));
            // Adds a fadein-fadeout animations to the activity
            CustomIntent.customType(UserLoginActivity.this, "fadein-to-fadeout");

            // finishes this activity and starts a new one
            finish();

        }
        else{
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            Toast.makeText(this,getString(R.string.text_email_not_verified), Toast.LENGTH_LONG).show();
            mAuth.signOut();
            finish();
            // restart the activity
            startActivity(new Intent(UserLoginActivity.this,UserLoginActivity.class));
            // Adds a fadein-fadeout animations to the activity
            CustomIntent.customType(UserLoginActivity.this, "fadein-to-fadeout");

        }
    }

    //method called when the link to the SignUp Activity is clicked or tapped
    public void onSignUpLinkClick(View View){
        // finishes the current activity and open the Sign Up Activity
        finish();
        //starts the Sign Up Activity
        startActivity(new Intent(UserLoginActivity.this,UserSignUpActivity.class));
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(UserLoginActivity.this, "fadein-to-fadeout");

    }

    //method called when the link to the SignUp Activity is clicked or tapped
    public void onAdminLoginButtonLinkClick(View view){
        // finishes the current activity and open the AdminLoginActivity Activity
        finish();
        //starts the AdminLoginActivity
        startActivity(new Intent(UserLoginActivity.this,AdminLoginActivity.class));
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(UserLoginActivity.this, "fadein-to-fadeout");
    }

    // clears the Username and Password EditText
    public void clearBothTextFields(){
        editTextEmail.setText(null);
        editTextPassword.setText(null);
    }

    public void clearPasswordTextField(){
        editTextPassword.setText(null);
    }

    // method to reset user password
    public void onButtonResetPassword(View view) {
        // finishes the current activity and open the resetPassword Activity
        finish();
        startActivity(new Intent(UserLoginActivity.this, ResetUserPasswordActivity.class));
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(UserLoginActivity.this, "fadein-to-fadeout");
    }

    @Override
    public void finish() {
        super.finish();
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(UserLoginActivity.this, "fadein-to-fadeout");
    }
}

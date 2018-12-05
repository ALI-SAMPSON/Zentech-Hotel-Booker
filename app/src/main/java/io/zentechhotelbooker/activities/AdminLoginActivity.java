package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Admin;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;

    private NestedScrollView nestedScrollView;

    //an instance of the ProgressBar Class
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    private Admin admin;

    private FirebaseDatabase admindB;

    private DatabaseReference adminRef;

    private Animation shake;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // removes status bar and to make background fit Screen
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        //get reference to the EditText fields defined in the xml file
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        progressBar = findViewById(R.id.progressBar);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        mAuth = FirebaseAuth.getInstance();

        admin = new Admin();

        admindB = FirebaseDatabase.getInstance();

        adminRef = admindB.getReference("Admin");

        shake = AnimationUtils.loadAnimation(this,R.anim.anim_shake);

    }


    //login button method for admin
    public void onSignInButtonClick(View view){

        //get text from the EditText fields
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //checks if text entered are valid and textfields are not empty
        if(email.isEmpty()){
            // adds animation to the editText
            editTextEmail.clearAnimation();
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(R.string.error_empty_email));
            //editTextEmail.requestFocus();
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            // adds animation to the editText
            editTextEmail.clearAnimation();
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(R.string.email_invalid));
            //editTextEmail.requestFocus();
        }
        else if(password.isEmpty()){
            // adds animation to the editText
            editTextPassword.clearAnimation();
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(R.string.error_empty_password));
            editTextPassword.requestFocus();
        }
        else if(password.length() < 6){
            // adds animation to the editText
            editTextPassword.clearAnimation();
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(R.string.error_password_length));
            editTextPassword.requestFocus();
        }
        else{
            // method call to the LoginAdmin method
            loginAdmin();
        }

    }

    //method for logging Admin into the system
    public void loginAdmin(){

        progressBar.setVisibility(View.VISIBLE);

        //getting input from the editText
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        // method to sign admin into the system
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser currentAdmin = mAuth.getCurrentUser();

                            // clear TextBothfields
                            clearBothTextFields();

                            // displays a success message
                            Toast.makeText(AdminLoginActivity.this,getString(R.string.login_successful),Toast.LENGTH_LONG).show();
                            //Snackbar.make(nestedScrollView,getString(R.string.login_successful),Snackbar.LENGTH_LONG).show();

                            startActivity(new Intent(AdminLoginActivity.this,AdminDashBoardActivity.class));

                            // Adds a fadein-fadeout animations to the activity
                            CustomIntent.customType(AdminLoginActivity.this, "fadein-to-fadeout");

                            // finishes this activity and start the AdminDashBoard Activity
                            finish();
                        }
                        else{
                            // clear PasswordTextfields
                            clearPasswordTextField();
                            // displays an error message
                            Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();

                        }
                        // dismiss progressBar
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // sign user out if this method is called
        mAuth.signOut();
    }

    // clears all text from the Username and Password EditText
    public void clearBothTextFields(){
        editTextEmail.setText(null);
        editTextPassword.setText(null);
    }

    // clears all text from the Password EditText
    public void clearPasswordTextField(){
        editTextPassword.setText(null);
    }

    //link to the user login interface
    public void onLoginButtonLinkClick(View view){

        startActivity(new Intent(AdminLoginActivity.this,UserLoginActivity.class));
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(AdminLoginActivity.this, "fadein-to-fadeout");
        //starts the Users LoginActivity
        finish();
    }


    @Override
    public void finish() {
        super.finish();
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(AdminLoginActivity.this, "fadein-to-fadeout");
    }
}

package io.zentechhotelbooker.activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Admin;
import io.zentechhotelbooker.models.Users;

public class AdminSignUpActivity extends AppCompatActivity {

    //an instance of the Firebase Authentication class
    FirebaseAuth mAuth;

    @SuppressWarnings("unused")
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;

    Admin admin;

    // Animation class
    private Animation shake;

    //instance variables of the various views
    EditText editTextEmail;
    EditText editTextUsername;
    EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);

        // getting reference to the view objects
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);

        nestedScrollView =  findViewById(R.id.nestedScrollView);

        mAuth = FirebaseAuth.getInstance();

        progressBar = findViewById(R.id.progressBar);

        shake = AnimationUtils.loadAnimation(this,R.anim.anim_shake);

    }

    public void onSignUpButtonClick(View view) {

        //get the text from the EditText
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        /*checks the field to make sure they are not
         ** empty before user logs in and of accurate number of characters
         */
        if(email.isEmpty()){
            editTextEmail.clearAnimation();
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(R.string.error_empty_email));
            editTextEmail.requestFocus();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.clearAnimation();
            editTextEmail.startAnimation(shake);
            editTextEmail.setError(getString(R.string.email_invalid));
            editTextEmail.requestFocus();
            return;
        }
        else if(username.isEmpty()){
            editTextUsername.clearAnimation();
            editTextUsername.startAnimation(shake);
            editTextUsername.setError(getString(R.string.error_empty_username));
            editTextUsername.requestFocus();
            return;
        }
        else if(password.isEmpty()){
            editTextPassword.clearAnimation();
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(R.string.error_empty_password));
            editTextPassword.requestFocus();
            return;
        }
        else if(password.length() < 6 ){
            editTextPassword.clearAnimation();
            editTextPassword.startAnimation(shake);
            editTextPassword.setError(getString(R.string.error_password_length));
            editTextPassword.requestFocus();
            return;
        }

        else{ signUpAdmin(); }

    }

    private void signUpAdmin() {

        progressBar.setVisibility(View.VISIBLE);

        //get text from the EditText fields
        final String email = editTextEmail.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        // register user details into firebase database
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser currentAdmin = mAuth.getCurrentUser();

                            //getting the instances
                            assert currentAdmin != null;

                            final Admin admin = new Admin();

                            admin.setUid(currentAdmin.getUid());
                            admin.setEmail(email);
                            admin.setUsername(username);

                            FirebaseDatabase.getInstance().getReference("Admin")
                                    .child(currentAdmin.getUid())
                                    .setValue(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        // method call to save Username and profile Image
                                        saveUserInfo();

                                        // displays a success message
                                        Snackbar.make(nestedScrollView,
                                                "Sign Up Successful, Mr Admin",
                                                Snackbar.LENGTH_LONG).show();

                                        // clear TextFields
                                        clearTextFields();
                                    }
                                    else{

                                        // displays an error message
                                        Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();

                                    }

                                    // dismisses the dialog
                                    progressBar.setVisibility(View.GONE);

                                }
                            });

                            // signs user out of the system
                            mAuth.signOut();

                        }
                        else {

                            // dismisses the dialog
                            progressBar.setVisibility(View.GONE);

                            // displays an error message
                            Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();

                            // signs user out of the system
                            mAuth.signOut();
                        }
                    }
                });

    }

    // method to save username and profile image
    private void saveUserInfo(){

        String username = editTextUsername.getText().toString().trim();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null){
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build();

            // updates user info with the passed username and image
            user.updateProfile(userProfileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                // hides the progressBar
                                progressBar.setVisibility(View.GONE);
                            }
                            else{
                                // hides the progressBar
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }

    }

    //clear text from the textfields
    public void clearTextFields(){
        editTextEmail.setText(null);
        editTextUsername.setText(null);
        editTextPassword.setText(null);
    }
}

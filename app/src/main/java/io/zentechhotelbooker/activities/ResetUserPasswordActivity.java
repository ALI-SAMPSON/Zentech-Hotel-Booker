package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import io.zentechhotelbooker.R;

public class ResetUserPasswordActivity extends AppCompatActivity {

    EditText editTextEmail;

    CoordinatorLayout coordinatorLayout;

    ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_user_password);

        // getting reference to the view objects
        editTextEmail = findViewById(R.id.email);

        progressBar = findViewById(R.id.progressBar);

        coordinatorLayout = findViewById(R.id.coordinatorLayout);

        mAuth = FirebaseAuth.getInstance();

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    //  on Click method is reset password button
    public void resetPasswordButton(View view) {
        // getting text from the fields
        String email = editTextEmail.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError(getString(R.string.error_empty_email));
            editTextEmail.requestFocus();
            return;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError(getString(R.string.email_registered));
            editTextEmail.requestFocus();
            return;
        }
        else{ resetPassword(); }

    }

    // method to reset password
    private void resetPassword(){

        // displays the progressBar
        progressBar.setVisibility(View.VISIBLE);

        // getting text from the fields
        String email = editTextEmail.getText().toString().trim();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            // displays a prompt message
                            Toast.makeText(getApplicationContext(),getString(R.string.instruct_reset_password),Toast.LENGTH_LONG).show();
                        }
                        else{
                            // displays an error  message
                            Snackbar.make(coordinatorLayout,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                        }

                        // hides the progressBar
                        progressBar.setVisibility(View.GONE);
                    }
                });

    }

    // method to go back
    public void goBackButton(View view) {
        // finishes the current activity and open the resetPassword Activity
        ResetUserPasswordActivity.this.finish();
        startActivity(new Intent(ResetUserPasswordActivity.this, UserLoginActivity.class));
    }
}

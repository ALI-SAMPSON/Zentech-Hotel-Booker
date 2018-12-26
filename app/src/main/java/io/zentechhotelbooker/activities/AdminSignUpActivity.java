package io.zentechhotelbooker.activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Admin;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

public class AdminSignUpActivity extends AppCompatActivity {

    LinearLayoutCompat linearLayoutCompat;

    // variable to store Decryption algorithm name
    String AES = "AES";

    // variable to store encrypted password
    String encryptedPassword;

    @SuppressWarnings("unused")
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;

    Admin admin;

    DatabaseReference adminRef;

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

        linearLayoutCompat = findViewById(R.id.linearLayoutCompat);

        // instance of the admin class
        admin = new Admin();

        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        progressBar = findViewById(R.id.progressBar);

        shake = AnimationUtils.loadAnimation(this,R.anim.anim_shake);

    }

    // on click listener for signUp Button
    public void onSignUpButtonClick(View view) {

        //get the text from the EditText
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        /*checks the field to make sure they are not
         ** empty before user logs in and of accurate number of characters
         */
        if(TextUtils.isEmpty(email)){
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
        else if(TextUtils.isEmpty(username)){
            editTextUsername.clearAnimation();
            editTextUsername.startAnimation(shake);
            editTextUsername.setError(getString(R.string.error_empty_username));
            editTextUsername.requestFocus();
            return;
        }
        else if(TextUtils.isEmpty(password)){
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

    // Method to handle user login
    public void signUpAdmin(){

        // shows the progressBar
        progressBar.setVisibility(View.VISIBLE);

        //gets text from the editTExt fields
        final String _username = editTextUsername.getText().toString().trim();
        final String _email = editTextEmail.getText().toString().trim();
        final String _password = editTextPassword.getText().toString().trim();

        try {
            encryptedPassword = encryptPassword(_password,_email);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String uid = adminRef.push().getKey();

        admin.setUid(uid);
        admin.setUsername(_username);
        admin.setEmail(_email);
        admin.setPassword(encryptedPassword);

        adminRef.child(uid).setValue(admin).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    // display a success message if login is successful
                    Snackbar.make(nestedScrollView,getString(R.string.sign_up_successful), Snackbar.LENGTH_LONG).show();

                    // clears field
                    clearTextFields();

                }
                else{

                    // display an error message if login is not successful
                    Snackbar.make(nestedScrollView, task.getException().getMessage(), Snackbar.LENGTH_LONG).show();

                }

                // sets visibility to gone
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    // method to encrypt admin password on sign up
    private String encryptPassword(String password, String email) throws Exception{
        SecretKeySpec key = generateKey(email);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal = c.doFinal(password.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }

    private String decryptPassword(String password, String email) throws Exception {
        SecretKeySpec key  = generateKey(email);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodedValue = Base64.decode(password,Base64.DEFAULT);
        byte[] decVal = c.doFinal(decodedValue);
        String decryptedValue = new String(decVal);
        return decryptedValue;

    }

    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0 , bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec keySpec = new SecretKeySpec(key,"AES");
        return keySpec;
    }


    //clear text from the textfields
    public void clearTextFields(){
        editTextUsername.setText(null);
        editTextEmail.setText(null);
        editTextPassword.setText(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // finishes activity
        finish();
    }
}

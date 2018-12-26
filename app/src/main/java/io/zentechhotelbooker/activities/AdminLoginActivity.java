package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Base64;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.SavedSharePreference;
import io.zentechhotelbooker.models.Admin;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

public class AdminLoginActivity extends AppCompatActivity {

    LinearLayoutCompat linearLayoutCompat;

    // variable to store Decryption algorithm name
    String AES = "AES";

    // variable to store encrypted password
    String decryptedPassword;

    private EditText editTextEmail;
    private EditText editTextPassword;

    private NestedScrollView nestedScrollView;

    //an instance of the ProgressBar Class
    private ProgressBar progressBar;

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

        linearLayoutCompat = findViewById(R.id.linearLayoutCompat);

        //get reference to the EditText fields defined in the xml file
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        progressBar = findViewById(R.id.progressBar);

        nestedScrollView = findViewById(R.id.nestedScrollView);

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
    private void loginAdmin() {

        progressBar.setVisibility(View.VISIBLE);

        final String email = editTextEmail.getText().toString();
        final String password = editTextPassword.getText().toString();

        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    Admin admin = snapshot.getValue(Admin.class);

                    assert admin != null;
                    String adminEmail = admin.getEmail();
                    String encryptedPassword = admin.getPassword();

                    // decrypt password
                    try {
                        decryptedPassword = decryptPassword(encryptedPassword,email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // compares decrypted password to the current password entered and
                    if(password.equals(decryptedPassword) && email.equals(adminEmail)){

                        // dismiss the dialog
                        progressBar.setVisibility(View.GONE);

                        // storing email in shared preference
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(AdminLoginActivity.this).edit();
                        editor.putString("email", email);
                        editor.putString("uid", admin.getUid());
                        editor.apply();

                        // setting uid and email to getter method in sharedPreferences
                        SavedSharePreference.setEmail(AdminLoginActivity.this, email);
                        SavedSharePreference.setUid(AdminLoginActivity.this, admin.getUid());

                        // display a successful login message
                        Toast.makeText(AdminLoginActivity.this,getString(R.string.login_successful),Toast.LENGTH_SHORT).show();

                        // clear the text fields
                        clearBothTextFields();

                        // start the home activity
                        startActivity(new Intent(AdminLoginActivity.this,AdminDashBoardActivity.class));

                        // Add a custom animation ot the activity
                        CustomIntent.customType(AdminLoginActivity.this,"fadein-to-fadeout");

                        // finishes this activity(prevents user from going back to this activity when back button is pressed)
                        finish();

                    }
                    else{

                        progressBar.setVisibility(View.GONE);
                        // display a message if there is an error
                        //Snackbar.make(relativeLayout,"Incorrect Email or password. Please Try Again",Snackbar.LENGTH_LONG).show();
                        Toast.makeText(AdminLoginActivity.this,"Incorrect email or password. Please Try Again",Toast.LENGTH_LONG).show();

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display error message
                Snackbar.make(nestedScrollView,databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    // method to encrypt password
    private String encryptPassword(String password, String email) throws Exception{
        SecretKeySpec key = generateKey(email);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal = c.doFinal(password.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }

    // method to decrypt password
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
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

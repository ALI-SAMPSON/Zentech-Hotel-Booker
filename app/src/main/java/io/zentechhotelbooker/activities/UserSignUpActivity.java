package io.zentechhotelbooker.activities;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

public class UserSignUpActivity extends AppCompatActivity {

    //an instance of the Firebase Authentication class
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    @SuppressWarnings("unused")
    private ProgressBar progressBar1;
    private NestedScrollView nestedScrollView;

    //an instance of the User class

    //instance of the ProgressDialog class
    ProgressDialog progressDialog;

    Users users;

    //instance variables of the various views
    private EditText editTextEmail;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextMobileNumber;

    private AppCompatSpinner appCompatSpinnerGender;
    private ArrayAdapter<CharSequence> arrayAdapterGender;

    private AppCompatButton appCompatButton;

    public static final int REQUEST_CODE = 1;

    Uri uriProfileImage;

    String profileImageUrl;

    private CircleImageView circleImageView;

    // Animation class
    private Animation shake;

    private Button btn_login;

    private String CHANNEL_ID = "notification_channel_id";

    private int notificationId = 0;

    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // removes status bar and to make background fit Screen
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_up);

        // getting reference to the view objects
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber);

        //spinner or drop down view and its arrayAdapter instantiation
        appCompatSpinnerGender = findViewById(R.id.spinnerGender);
        arrayAdapterGender = ArrayAdapter.createFromResource(this,R.array.gender,R.layout.spinner_item);
        arrayAdapterGender.setDropDownViewResource(R.layout.spinner_dropdown_item);
        appCompatSpinnerGender.setAdapter(arrayAdapterGender);

        btn_login = findViewById(R.id.buttonSignUp);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        circleImageView = findViewById(R.id.circularImageView);

        progressBar = findViewById(R.id.progressBar);

        progressBar1 = findViewById(R.id.progressBar1);

        progressDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_DARK);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Signing Up");
        progressDialog.setMessage("please wait...");

        mAuth = FirebaseAuth.getInstance();

        users = new Users();

        shake = AnimationUtils.loadAnimation(this,R.anim.anim_shake);

    }


    @Override
    public void finish() {
        super.finish();
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(UserSignUpActivity.this,"fadein-to-fadeout");
    }

    //method for the sign Up button
    public void onSignUpButtonClick(View view){

        //get the text from the EditText
        String email = editTextEmail.getText().toString().trim();
        String username = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String mobile_number = editTextMobileNumber.getText().toString().trim();

        /*checks the field to make sure they are not
         ** empty before user signs up and of accurate number of characters
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
        else if(mobile_number.isEmpty()){
            editTextMobileNumber.clearAnimation();
            editTextMobileNumber.startAnimation(shake);
            editTextMobileNumber.setError(getString(R.string.error_empty_phone));
            editTextMobileNumber.requestFocus();
            return;
        }
        else if(mobile_number.length() < 10){
            editTextMobileNumber.clearAnimation();
            editTextMobileNumber.startAnimation(shake);
            editTextMobileNumber.setError(getString(R.string.phone_invalid));
            editTextMobileNumber.requestFocus();
            return;
        }
        else if(circleImageView.getDrawable() == null){
            YoYo.with(Techniques.RotateIn).playOn(circleImageView);
            Toast.makeText(getApplicationContext(),"Please upload your profile photo by clicking on the icon above",Toast.LENGTH_LONG).show();
        }
        else{ signUpUser(); }

    }

    //sign Up method
    public void signUpUser(){

        final FirebaseUser user = mAuth.getCurrentUser();

        // displays the dialog
        //progressBar1.setVisibility(View.VISIBLE);
        progressDialog.show();

        //get text from the EditText fields
        final String email = editTextEmail.getText().toString().trim();
        final String username = editTextUsername.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        final String mobile_number = editTextMobileNumber.getText().toString().trim();
        final String gender = appCompatSpinnerGender.getSelectedItem().toString().trim();

        // register user details into firebase database
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            //getting the instances
                            assert currentUser != null;

                            final Users users = new Users();

                            users.setUid(currentUser.getUid());
                            users.setEmail(email);
                            users.setUsername(username);
                            users.setGender(gender);
                            users.setImageUrl(profileImageUrl);
                            users.setMobile_number(mobile_number);
                            users.setSearch(username.toLowerCase());

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(currentUser.getUid())
                                    .setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        // method call to save Username and profile Image
                                        saveUserInfo();

                                        // Sends a notification to the user after signing up successfully
                                        // Creating an explicit intent for the activity in the app
                                        Intent intent = new Intent(UserSignUpActivity.this, UserLoginActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(UserSignUpActivity.this, 0, intent, 0);

                                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(UserSignUpActivity.this, CHANNEL_ID)
                                                .setSmallIcon(R.mipmap.app_icon_round)
                                                .setContentTitle(getString(R.string.app_name))
                                                .setContentText("Sign Up Successful. A verification link has been\n" +
                                                        " sent to " + mAuth.getCurrentUser().getEmail() + "." +
                                                        " Please visit your inbox to verify\n" +
                                                        " our email address. Thanks for joining us!")
                                                .setStyle(new NotificationCompat.BigTextStyle()
                                                        .bigText("Sign Up Successful. A verification link has been\n" +
                                                                " sent to " + mAuth.getCurrentUser().getEmail() + "." +
                                                                " Please visit your inbox to verify\n" +
                                                                " our email address. Thanks for joining us!"))
                                                // Set the intent that will fire when the user taps the notification
                                                .setWhen(System.currentTimeMillis())
                                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                                .setContentIntent(pendingIntent)
                                                .setAutoCancel(true);

                                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(UserSignUpActivity.this);
                                        notificationManager.notify(notificationId,mBuilder.build());

                                        // Call to the method to send Verification Email
                                        sendVerificationEmail();

                                        // displays a success message
                                        Snackbar.make(nestedScrollView,
                                                getString(R.string.text_sign_up_and_verification_sent),
                                                Snackbar.LENGTH_LONG).show();

                                        // clear TextFields
                                        clearTextFields();
                                    }
                                    else{

                                        // displays an error message
                                        Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();

                                    }

                                    // dismisses the dialog
                                    //progressBar1.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                }
                            });

                        }
                        else {

                            // dismisses the dialog
                            //progressBar1.setVisibility(View.GONE);
                            progressDialog.dismiss();

                            // displays an error message
                            Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                        }
                    }
                });

    }


    // Method to send verification link to the user after sign Up
    public void sendVerificationEmail(){

        FirebaseUser user = mAuth.getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    // signs user out
                    mAuth.signOut();

                }

                else{

                    // signs user out
                    mAuth.signOut();

                    // display an error message
                    Snackbar.make(nestedScrollView,task.getException().getMessage(),Snackbar.LENGTH_LONG).show();

                }
            }
        });

    }


    //link to the User login page
    public void onLoginLinkButtonClick(View view){

        //starts the LoginActivity when user clicks the button
        startActivity(new Intent(UserSignUpActivity.this, UserLoginActivity.class));
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(UserSignUpActivity.this,"fadein-to-fadeout");
        // finishes the activity
        finish();

    }

    //clear text from the textfields
    public void clearTextFields(){
        editTextEmail.setText(null);
        editTextUsername.setText(null);
        editTextPassword.setText(null);
        editTextMobileNumber.setText(null);
    }


    // method to open user gallery
    private void openGallery(){
        Intent pickIntent = new Intent();
        pickIntent.setType("image/*");
        pickIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickIntent,"Select Profile Picture"),REQUEST_CODE);
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(UserSignUpActivity.this,"fadein-to-fadeout");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data.getData() != null){
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uriProfileImage);
                circleImageView.setImageBitmap(bitmap);
                // method to upload image
                uploadImage();
            }
            catch (Exception e){
                Snackbar.make(nestedScrollView,e.getMessage(),Snackbar.LENGTH_LONG).show();
            }
            circleImageView.setImageURI(uriProfileImage);
        }
    }

    // method to upload image
    private void uploadImage(){

        final StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("Users Profile Pictures/" + System.currentTimeMillis() + ".jpg");

        if(uriProfileImage != null){
            // displays the progressBar
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // dismisses the progress Bar and stores the image Url of the image in a String variable
                            progressBar.setVisibility(View.GONE);
                            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    profileImageUrl = downloadUrl.toString();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // dismisses the progress Bar and displays an error message
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(nestedScrollView,e.getMessage(),Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }

    // method to save username and profile image
    private void saveUserInfo(){

        final String username = editTextUsername.getText().toString().trim();

        final FirebaseUser user = mAuth.getCurrentUser();

        if(user != null || profileImageUrl != null){
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            // updates user info with the passed username and image
            user.updateProfile(userProfileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                userRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                                // updating these fields if task is successful
                                HashMap<String, Object> updateInfo = new HashMap<>();
                                updateInfo.put("imageUrl",profileImageUrl);
                                updateInfo.put("username", username);
                                userRef.updateChildren(updateInfo);

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

    // on Click Listener for circular ImageView
    public void onCircularImageViewClick(View view){
        openGallery();
    }


}

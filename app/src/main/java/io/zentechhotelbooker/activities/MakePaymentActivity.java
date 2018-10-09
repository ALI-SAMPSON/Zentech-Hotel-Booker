package io.zentechhotelbooker.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.bulksms.Sender;
import io.zentechhotelbooker.models.Payments;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

public class MakePaymentActivity extends AppCompatActivity {

    //class variables for various views
    ProgressBar progressBar;

    Payments payments;

    Users users;

    FirebaseAuth mAuth;

    private TextView tv_room_number;

    private EditText editTextUsername;
    private EditText editTextRoomType;
    private EditText editTextPrice;
    private EditText editTextMomoNumber;

    private AppCompatSpinner spinnerPaymentMethod;
    private ArrayAdapter<CharSequence> arrayAdapterPaymentMethod;

    DatabaseReference paymentRef;

    DatabaseReference usersRef;

    NestedScrollView nestedScrollView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.make_payment));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //getting reference to the editText fields
        editTextRoomType = findViewById(R.id.editTextRoomType);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextMomoNumber = findViewById(R.id.editTextMomoNumber);

        tv_room_number = findViewById(R.id.tv_room_number);

        //spinner or drop down view and its arrayAdapter instantiation
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        arrayAdapterPaymentMethod = ArrayAdapter.createFromResource(this,R.array.payment_method,R.layout.spinner_item);
        arrayAdapterPaymentMethod.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(arrayAdapterPaymentMethod);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        progressBar = findViewById(R.id.progressBar);

        payments = new Payments();

        users = new Users();

        mAuth = FirebaseAuth.getInstance();

        paymentRef = FirebaseDatabase.getInstance().getReference("Payments");

        editTextRoomType.setText(getIntent().getStringExtra("room_type"));
        editTextPrice.setText(getIntent().getStringExtra("room_price"));

        tv_room_number.setText(getIntent().getStringExtra("room_number"));
        tv_room_number.setVisibility(View.GONE);

        final String user_image = getIntent().getStringExtra("user_image");

        onEditTextClick();
    }

    //sets error message on the editTextViews when user clicks on to edit
    public void onEditTextClick(){

        //error strings
        final String error_username = "Username field cannot be edited";
        final String error_room_type = "Room Type field cannot be edited";
        final String error_room_price = "Price field cannot be edited";

        editTextUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add a custom animation
                YoYo.with(Techniques.Shake).playOn(editTextUsername);
                editTextUsername.setError(error_username);
                Toast.makeText(MakePaymentActivity.this,error_username,Toast.LENGTH_LONG).show();
            }
        });

        editTextRoomType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add a custom animation
                YoYo.with(Techniques.Shake).playOn(editTextRoomType);
                editTextRoomType.setError(error_room_type);
                Toast.makeText(MakePaymentActivity.this,error_room_type,Toast.LENGTH_LONG).show();
            }
        });

        editTextPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add a custom animation
                YoYo.with(Techniques.Shake).playOn(editTextPrice);
                editTextPrice.setError(error_room_price);
                Toast.makeText(MakePaymentActivity.this,error_room_price,Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        // checks if there the is user logged in
        if(mAuth.getCurrentUser() != null){
            // gets an instance of the user
            FirebaseUser user = mAuth.getCurrentUser();
            // sets the username text to the user's name
            editTextUsername.setText(user.getDisplayName());
        }
    }

    //method for handling make payments
    public void onMakePaymentButtonClick(View view){

        //gets input from the fields
        String mobile_number = editTextMomoNumber.getText().toString().trim();

        //checks if the fields are not empty
        if(mobile_number.isEmpty()){
            // add a custom animation
            YoYo.with(Techniques.Shake).playOn(editTextMomoNumber);
            editTextMomoNumber.setError(getString(R.string.error_empty_momo_number));
            editTextMomoNumber.requestFocus();
            return;
        }
        else if(mobile_number.length() != 10 ){
            // add a custom animation
            YoYo.with(Techniques.Shake).playOn(editTextMomoNumber);
            editTextMomoNumber.setError(getString(R.string.phone_invalid));
            editTextMomoNumber.requestFocus();
            return;
            //Snackbar.make(nestedScrollView,"Momo number are required fields",Snackbar.LENGTH_LONG).show();
        }
        else{
            //method call
            makePayment();
        }

    }

    //method for making the payments
    public void makePayment(){

        final String user_image = getIntent().getStringExtra("user_image");

        // displays the progressBar
        progressBar.setVisibility(View.VISIBLE);

        //gets text from the user
        final String user_name = editTextUsername.getText().toString().trim();
        final String room_type = editTextRoomType.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();
        String mobile_number = editTextMomoNumber.getText().toString().trim();
        String payment_method  =  spinnerPaymentMethod.getSelectedItem().toString().trim();

        final String room_number = tv_room_number.getText().toString().trim();

        //sets the values from the EditText Fields to those in the database
        //payments.setUid(mAuth.getCurrentUser().getUid());
        payments.setUser_name(user_name);
        payments.setRoom_number(room_number);
        payments.setRoom_type(room_type);
        payments.setPrice(price);
        payments.setMobile_number(mobile_number);
        payments.setPayment_method(payment_method);
        payments.setImageUrl(user_image);

        //code to the check if room has been booked already
        paymentRef.child(room_number).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //gets a snapshot of the data in the database
                if (dataSnapshot.exists()) {
                    final Payments payments = dataSnapshot.getValue(Payments.class);
                    //checks if room has been booked
                    if (room_number.equals(payments.getRoom_number())) {

                        // hides the progressBar
                        progressBar.setVisibility(View.GONE);

                        //clearBothTextFields(); //call to this method
                        Snackbar.make(nestedScrollView,
                                " Room is already booked by another user. Please try another room!",
                                Snackbar.LENGTH_LONG).show();

                        //return;

                    }

                }

                //else if room is not booked then allow payment to be made
                else{
                    //sets the values to the database by using the username as the child
                    paymentRef.child(room_number).setValue(payments).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();

                                String _imageUrl = user.getPhotoUrl().toString();

                                payments.setImageUrl(_imageUrl);

                                //Toast.makeText(MakePaymentActivity.this,"Payment made successfully",Toast.LENGTH_LONG).show();
                                Snackbar.make(nestedScrollView, " Payment made successfully ", Snackbar.LENGTH_LONG).show();

                                //sends a notification to the user of voting successfully
                                Intent intent = new Intent(MakePaymentActivity.this, MakePaymentActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(MakePaymentActivity.this, 0, intent, 0);
                                Notification notification = new Notification.Builder(MakePaymentActivity.this)
                                        .setSmallIcon(R.mipmap.app_icon_round)
                                        .setContentTitle(getString(R.string.app_name))
                                        .setContentText(user_name + ", you have successfully made payment for a " + room_type + " room ")
                                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                        .setContentIntent(pendingIntent).getNotification();
                                notification.flags = Notification.FLAG_AUTO_CANCEL;
                                NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                                nm.notify(0, notification);

                                // Method call to the class to send SMS
                                sendSMS();

                            }
                            else {

                                //Toast.makeText(MakePaymentActivity.this,"Payment made successfully",Toast.LENGTH_LONG).show();
                                Snackbar.make(nestedScrollView, task.getException().toString(), Snackbar.LENGTH_LONG).show();
                            }
                            // hides the progressBar
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // display an error message
                Snackbar.make(nestedScrollView, databaseError.getMessage(),Snackbar.LENGTH_LONG).show();

            }
        });

    }

    // Method to call the Sender class to
    private void sendSMS(){

        try {

            //gets text or input from the user
            final String user_name = editTextUsername.getText().toString().trim();
            final String room_type = editTextRoomType.getText().toString().trim();
            String price = editTextPrice.getText().toString().trim();
            String mobile_number = editTextMomoNumber.getText().toString().trim();
            String payment_method  =  spinnerPaymentMethod.getSelectedItem().toString().trim();

            String my_number = "0209062445";

            // variable to hold the message to send
            String message = user_name + " has successfully made payment for a " + room_type + " room. ";

            // Below example is for sending Plain text
            Sender s = new Sender("rslr.connectbind.com",
                    2345, "marketin",
                    "zent-marketing",
                    message,
                    "1",
                    "0",
                    my_number,
                    getString(R.string.app_name));

            // submitmessage using an object of the Sender Class
            s.submitMessage();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            //sends user to the Home Activity
            case android.R.id.home:

                // finishes the activity
                finish();

                // starts the home activity
                startActivity(new Intent(MakePaymentActivity.this,HomeActivity.class));

                // Add a custom animation to the activity
                CustomIntent.customType(MakePaymentActivity.this,"fadein-to-fadeout");

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // method to clear TextViews when user clicks on
    public void onClearButtonClick(View view) {
        // clears the fields
        editTextMomoNumber.setText(null);

    }

    @Override
    public void finish() {
        super.finish();
        // Add a custom animation to the activity
        CustomIntent.customType(MakePaymentActivity.this,"bottom-to-up");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // finish the activity
        finish();

        // starts the activity
        startActivity(new Intent(MakePaymentActivity.this,HomeActivity.class));

        // Add a custom animation to the activity
        CustomIntent.customType(MakePaymentActivity.this,"fadein-to-fadeout");

    }
}

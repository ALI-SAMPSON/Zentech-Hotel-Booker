package io.zentechhotelbooker.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Payments;
import io.zentechhotelbooker.models.Users;

public class MakePaymentActivity extends AppCompatActivity {

    //class variables for various views
    ProgressDialog progressDialog;

    Payments payments;

    Users users;

    private EditText editTextUsername;
    private EditText editTextRoomNumber;
    private EditText editTextPrice;
    private EditText editTextMobileNumber;

    private AppCompatSpinner spinnerPaymentMethod;
    private ArrayAdapter<CharSequence> arrayAdapterPaymentMethod;

    DatabaseReference paymentRef;

    DatabaseReference usersRef;

    RelativeLayout relativeLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MAKE PAYMENT");

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //getting reference to the editText fields
        editTextRoomNumber = findViewById(R.id.editTextRoomNumber);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber);

        //spinner or drop down view and its arrayAdapter instantiation
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        arrayAdapterPaymentMethod = ArrayAdapter.createFromResource(this,R.array.payment_method,R.layout.spinner_item);
        arrayAdapterPaymentMethod.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(arrayAdapterPaymentMethod);

        relativeLayout = findViewById(R.id.relativeLayout);

        payments = new Payments();

        users = new Users();

        //String key = getIntent().getExtras().get("key").toString();

        //usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(key);

        paymentRef = FirebaseDatabase.getInstance().getReference().child("Payments");


        //editTextUsername.setText(getIntent().getStringExtra("user_name"));
        editTextRoomNumber.setText(getIntent().getStringExtra("room_number"));
        editTextPrice.setText(getIntent().getStringExtra("price"));

        onEditTextClick();
    }

    //sets error message on the editTextViews when user clicks on to edit
    public void onEditTextClick(){

        //error strings
        final String error_room_number = "Room number field cannot be edited";
        final String error_price = "Price field cannot be edited";

        editTextRoomNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextRoomNumber.setError(error_room_number);
            }
        });

        editTextPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextPrice.setError(error_price);
            }
        });
    }

    //method for handling make payments
    public void onMakePaymentButtonClick(View view){

        //gets input from the fields
        String user_name = editTextUsername.getText().toString().trim();
        String mobile_number = editTextMobileNumber.getText().toString().trim();

        //error strings
        String error_username = "Username is a required field...Please enter valid information to proceed with your payment";
        String error_mobile_number = "Mobile Number is a required field...Please enter valid information to proceed with your payment";

        //checks if the fields are not empty
        if(user_name.equals("")){
            editTextUsername.setError(error_username);
        }
        else if(mobile_number.equals("")){
            editTextMobileNumber.setError(error_mobile_number);
        }
        else if(user_name.equals("") && mobile_number.equals("")){
            //Toast.makeText(MakePaymentActivity.this,"Both username and password are required fields",Toast.LENGTH_LONG).show();
            Snackbar.make(relativeLayout,"Both username and password are required fields",Snackbar.LENGTH_LONG).show();
        }
        else{
            makePayment(); //method call
        }

    }

    //method for making the payments
    public void makePayment(){

        //instance of progressDialog and it's instantiation
        progressDialog = ProgressDialog.show(MakePaymentActivity.this,"",null,true,true);
        progressDialog.setMessage("Please wait...");

        //gets text from the user
        String user_name = editTextUsername.getText().toString().trim();
        final String room_number = editTextRoomNumber.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();
        String mobile_number = editTextMobileNumber.getText().toString().trim();
        String payment_method  =  spinnerPaymentMethod.getSelectedItem().toString().trim();

        //sets the values from the EditText Fields to those in the database
        payments.setUser_name(user_name);
        payments.setRoom_number(room_number);
        payments.setPrice(price);
        payments.setMobile_number(mobile_number);
        payments.setPayment_method(payment_method);

        //code to the check if room has been booked already
        paymentRef.child(room_number).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //gets a snapshot of the data in the database
                if (dataSnapshot.exists()) {
                    Payments payments = dataSnapshot.getValue(Payments.class);
                    //checks if room has been booked
                    if (room_number.equals(payments.getRoom_number())) {
                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                timer.cancel();
                            }
                        }, 10000);

                        //clearBothTextFields(); //call to this method
                        Snackbar.make(relativeLayout, " Room is already booked by " + payments.getUser_name(), Snackbar.LENGTH_LONG).show();
                    }
                }

                //else if room is not booked then allow payment to be made
                else{
                    //sets the values to the database by using the username as the child
                    paymentRef.child(room_number).setValue(payments).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                final Timer timer = new Timer();
                                timer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        timer.cancel();
                                    }
                                }, 5000);
                                //Toast.makeText(MakePaymentActivity.this,"Payment made successfully",Toast.LENGTH_LONG).show();
                                Snackbar.make(relativeLayout, " Payment made successfully ", Snackbar.LENGTH_LONG).show();

                                //sends a notification to the user of voting successfully
                                Intent intent = new Intent(MakePaymentActivity.this, MakePaymentActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                PendingIntent pendingIntent = PendingIntent.getActivity(MakePaymentActivity.this, 0, intent, 0);
                                Notification notification = new Notification.Builder(MakePaymentActivity.this)
                                        .setSmallIcon(R.mipmap.app_icon_round)
                                        .setContentTitle("Yoka Logde")
                                        .setContentText(" You have successfully made payment for room " + room_number)
                                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                        .setContentIntent(pendingIntent).getNotification();
                                notification.flags = Notification.FLAG_AUTO_CANCEL;
                                NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                                nm.notify(0, notification);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            final Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    timer.cancel();
                                }
                            }, 5000);
                            //Toast.makeText(MakePaymentActivity.this,"Payment made successfully",Toast.LENGTH_LONG).show();
                            Snackbar.make(relativeLayout, e.getStackTrace().toString(), Snackbar.LENGTH_LONG).show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MakePaymentActivity.this, databaseError.toException().toString(),Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            //sends user to the Home Activity
            case android.R.id.home:
                startActivity(new Intent(MakePaymentActivity.this,HomeActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

package io.zentechhotelbooker.activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.bulksms.Sender;
import io.zentechhotelbooker.models.Admin;
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

    ProgressDialog progressDialog;

    ExpandableLayout expandableLayout1;
    ExpandableLayout expandableLayout2;

    // notification variables
    private String CHANNEL_ID = "notification_channel_id";

    private int notificationId = 0;

    EditText cardNumber;
    EditText holderName;
    EditText cardMonth;
    EditText cardYear;
    EditText cardCvc;


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

        tv_room_number = findViewById(R.id.tv_room_number);

        //getting reference to the editText fields
        editTextRoomType = findViewById(R.id.editTextRoomType);
        editTextPrice = findViewById(R.id.editTextPrice);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextMomoNumber = findViewById(R.id.editTextMomoNumber);

        //spinner or drop down view and its arrayAdapter instantiation
        spinnerPaymentMethod = findViewById(R.id.spinnerPaymentMethod);
        arrayAdapterPaymentMethod = ArrayAdapter.createFromResource(this,R.array.payment_method,R.layout.spinner_item);
        arrayAdapterPaymentMethod.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerPaymentMethod.setAdapter(arrayAdapterPaymentMethod);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        progressBar = findViewById(R.id.progressBar);

        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_DARK);
        // setting the style of the progress Dialog
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("please wait...");

        payments = new Payments();

        users = new Users();

        mAuth = FirebaseAuth.getInstance();

        paymentRef = FirebaseDatabase.getInstance().getReference("Payments");

        // gets String from the intents and sets it to the textViews
        tv_room_number.setText(getIntent().getStringExtra("room_number"));
        tv_room_number.setVisibility(View.GONE);
        editTextRoomType.setText(getIntent().getStringExtra("room_type"));
        editTextPrice.setText(getIntent().getStringExtra("room_price"));

        final String user_image = getIntent().getStringExtra("user_image");

        onEditTextClick();

        // line to enable sending of sms
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
        progressDialog.show();

        //gets text from the user
        final String user_name = editTextUsername.getText().toString().trim();
        final String room_type = editTextRoomType.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();
        String mobile_number = editTextMomoNumber.getText().toString().trim();
        String payment_method  =  spinnerPaymentMethod.getSelectedItem().toString().trim();

        // getting string extras from previous activity
        Intent intent = getIntent();
        String breakfast = intent.getStringExtra("breakfast_food");
        String lunch  = intent.getStringExtra("lunch_food");
        String supper = intent.getStringExtra("supper_food");

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
        payments.setBreakfastServed(breakfast);
        payments.setLunchServed(lunch);
        payments.setSupperServed(supper);
        payments.setSearch(user_name.toLowerCase());

        //code to the check if room has been booked already
        paymentRef.child(room_number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //gets a snapshot of the data in the database
                if (dataSnapshot.exists()) {

                    //final Payments payments = dataSnapshot.getValue(Payments.class);

                    // hides the progressBar
                    progressDialog.dismiss();

                    // displays a warning message
                    Snackbar.make(nestedScrollView,
                            " Room is already booked by another user. Please try another room!",
                            Snackbar.LENGTH_LONG).show();


                }

                //else if room is not booked then allow payment to be made
                else{
                    //sets the values to the database by using the username as the child
                    paymentRef.child(room_number)
                            .setValue(payments)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = mAuth.getCurrentUser();

                                String _imageUrl = user.getPhotoUrl().toString();

                                payments.setImageUrl(_imageUrl);

                                //Toast.makeText(MakePaymentActivity.this,"Payment made successfully",Toast.LENGTH_LONG).show();
                                Snackbar.make(nestedScrollView, " Payment made successfully ", Snackbar.LENGTH_LONG).show();

                                //sends a notification to the user of making payments successfully
                                Intent intent = new Intent(MakePaymentActivity.this, MakePaymentActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                PendingIntent pendingIntent = PendingIntent.getActivity(MakePaymentActivity.this, 0, intent, 0);

                                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MakePaymentActivity.this, CHANNEL_ID)
                                        .setSmallIcon(R.mipmap.app_icon_round)
                                        .setContentTitle(getString(R.string.app_name))
                                        .setContentText(user_name + ", you have successfully made payment for a " + room_type + " room ")
                                        .setStyle(new NotificationCompat.BigTextStyle()
                                                .bigText(user_name + ", you have successfully made payment for a " + room_type + " room "))
                                        // Set the intent that will fire when the user taps the notification
                                        .setWhen(System.currentTimeMillis())
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                        .setContentIntent(pendingIntent)
                                        .setAutoCancel(true);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(MakePaymentActivity.this);
                                notificationManager.notify(notificationId,mBuilder.build());

                                // Method call to the class to send SMS
                                //sendSMS();

                            }
                            else {

                                //Toast.makeText(MakePaymentActivity.this,"Payment made successfully",Toast.LENGTH_LONG).show();
                                Snackbar.make(nestedScrollView, task.getException().toString(), Snackbar.LENGTH_LONG).show();

                            }
                            // hides the progressDialog
                            progressDialog.dismiss();

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

    // Method to send sms to admin
    // after user books a room
    private void sendSMS(){

            //gets text or input from the user
            FirebaseUser user = mAuth.getCurrentUser();

            final String user_name = user.getDisplayName();

            //final String user_name = editTextUsername.getText().toString().trim();
            String room_type = editTextRoomType.getText().toString().trim();
            String price = editTextPrice.getText().toString().trim();
            String mobile_number = editTextMomoNumber.getText().toString().trim();
            String payment_method  =  spinnerPaymentMethod.getSelectedItem().toString().trim();

            String username = "zent-marketing";
            // password that is to be used along with username

            String password = "marketin";
            // Message content that is to be transmitted

            String message = user_name + " has successfully made payment for a " + room_type + ".";

            /*String message = user_name + " has successfully made payment for a " + room_type + " room "
                    + " at " + price + " using " + payment_method + ".";*/
            /**
             * What type of the message that is to be sent
             * <ul>
             * <li>0:means plain text</li>
             * <li>1:means flash</li>
             * <li>2:means Unicode (Message content should be in Hex)</li>
             * <li>6:means Unicode Flash (Message content should be in Hex)</li>
             * </ul>
             */
            String type = "0";
            /**
             * Require DLR or not
             * <ul>
             * <li>0:means DLR is not Required</li>
             * <li>1:means DLR is Required</li>
             * </ul>
             */
            String dlr = "1";
            /**
             * Destinations to which message is to be sent For submitting more than one

             * destination at once destinations should be comma separated Like
             * 91999000123,91999000124
             */
            String destination = "233245134112";

            // Sender Id to be used for submitting the message
            String source = "ZENTECH GH";

            // To what server you need to connect to for submission
            final String server = "rslr.connectbind.com";

            // Port that is to be used like 8080 or 8000
            int port = 2345;

            try {
                // Url that will be called to submit the message
                URL sendUrl = new URL("http://" + server + ":" + "/bulksms/bulksms?");
                HttpURLConnection httpConnection = (HttpURLConnection) sendUrl
                        .openConnection();
                // This method sets the method type to POST so that
                // will be send as a POST request
                httpConnection.setRequestMethod("POST");
                // This method is set as true wince we intend to send
                // input to the server
                httpConnection.setDoInput(true);
                // This method implies that we intend to receive data from server.
                httpConnection.setDoOutput(true);
                // Implies do not use cached data
                httpConnection.setUseCaches(false);
                // Data that will be sent over the stream to the server.
                DataOutputStream dataStreamToServer = new DataOutputStream( httpConnection.getOutputStream());
                dataStreamToServer.writeBytes("username="
                        + URLEncoder.encode(username, "UTF-8") + "&password="
                        + URLEncoder.encode(password, "UTF-8") + "&type="
                        + URLEncoder.encode(type, "UTF-8") + "&dlr="
                        + URLEncoder.encode(dlr, "UTF-8") + "&destination="
                        + URLEncoder.encode(destination, "UTF-8") + "&source="
                        + URLEncoder.encode(source, "UTF-8") + "&message="
                        + URLEncoder.encode(message, "UTF-8"));
                dataStreamToServer.flush();
                dataStreamToServer.close();
                // Here take the output value of the server.
                BufferedReader dataStreamFromUrl = new BufferedReader( new InputStreamReader(httpConnection.getInputStream()));
                String dataFromUrl = "", dataBuffer = "";
                // Writing information from the stream to the buffer
                while ((dataBuffer = dataStreamFromUrl.readLine()) != null) {
                    dataFromUrl += dataBuffer;
                }
                /**
                 * Now dataFromUrl variable contains the Response received from the
                 * server so we can parse the response and process it accordingly.
                 */
                dataStreamFromUrl.close();
                System.out.println("Response: " + dataFromUrl);
                //Toast.makeText(context, dataFromUrl, Toast.LENGTH_SHORT).show();
            }
            catch (Exception ex) {
                // catches any error that occurs and outputs to the user
                Toast.makeText(MakePaymentActivity.this,ex.getMessage(),Toast.LENGTH_LONG).show();
            }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            //sends user to the Home Activity
            case android.R.id.home:
                // starts the home activity
                startActivity(new Intent(MakePaymentActivity.this,HomeActivity.class));
                // Add a custom animation to the activity
                CustomIntent.customType(MakePaymentActivity.this,"fadein-to-fadeout");
                // finishes the activity
                finish();
                break;
            case R.id.menu_share:
                // open the sharing Intent
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String sharingBody = "https://www.zentechgh.com/";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Accra City Hotel");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,sharingBody);
                startActivity(Intent.createChooser(sharingIntent,"Share with"));
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
        CustomIntent.customType(MakePaymentActivity.this,"right-to-left");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // starts the activity
        startActivity(new Intent(MakePaymentActivity.this,HomeActivity.class));

        // Add a custom animation to the activity
        CustomIntent.customType(MakePaymentActivity.this,"fadein-to-fadeout");

        // finishes the activity
        finish();

    }

    //OnClickListener for Visa Card
    public void onVisaCardBtnClick(View view) {

        expandableLayout1 =  findViewById(R.id.expandableLayout1);
        expandableLayout1.toggle();

        /*
        cardNumber =  findViewById(R.id.editTextCardNumber);
        holderName = findViewById(R.id.editTextHolderName);
        cardMonth =  findViewById(R.id.editTextMonth);
        cardYear =  findViewById(R.id.editTextYear);
        cardCvc =  findViewById(R.id.editTextCvc);


        String _cardNumber = cardNumber.getText().toString().trim();
        String _holderName = holderName.getText().toString().trim();
        String _cardMonth = cardMonth.getText().toString().trim();
        String _cardYear = cardYear.getText().toString().trim();
        String _cardCvc = cardCvc.getText().toString().trim();

        if(TextUtils.isEmpty(_cardNumber)){
            cardNumber.setError(getString(R.string.error_card_number));
        }
        if(TextUtils.isEmpty(_holderName)){
            holderName.setError(getString(R.string.error_card_holder));
        }
    */


    }

    //OnClickListener for Master Card
    public void onMasterCardBtnClick(View view) {
        expandableLayout1 =  findViewById(R.id.expandableLayout1);
        expandableLayout1.toggle();
    }


}

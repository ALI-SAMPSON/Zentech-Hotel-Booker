package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Payments;

public class CheckPaymentActivity extends AppCompatActivity {

    FirebaseListAdapter firebaseListAdapter;

    FirebaseDatabase paymentdB;

    DatabaseReference paymentRef;

    ListView listView;

    Payments payments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_payment);

        //checks if the support actionbar is not null
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.check_payments));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        payments = new Payments();

        listView = findViewById(R.id.payment_listView);

        populatePayments(); ///call to the method

    }

    //method to populate the listView with payments
    public void populatePayments(){

        Query query = FirebaseDatabase.getInstance().getReference().child("Payments");

        FirebaseListOptions<Payments> options = new FirebaseListOptions.Builder<Payments>()
                .setLayout(R.layout.payment_list_item)
                .setQuery(query,Payments.class)
                .build();

        firebaseListAdapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {

                //TextView and imageView to populate the data from the database
                TextView username = v.findViewById(R.id.user_name);
                TextView room_number = v.findViewById(R.id.room_number);
                TextView price = v.findViewById(R.id.price);
                TextView mobile_number = v.findViewById(R.id.mobile_number);

                //typecasting the payments to the Object model
                Payments payments = (Payments) model;

                //setting the Text of the various textViews to the payment info in the database;
                username.setText(" Username : " + payments.getUser_name().toString());
                room_number.setText(" Room Number : " + payments.getRoom_number().toString());
                price.setText(" Price : GHS " + payments.getPrice().toString());
                mobile_number.setText(" Mobile Number : " + payments.getMobile_number().toString());


            }
        };

        listView.setAdapter(firebaseListAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseListAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //navigates to the aboutUs activity
            case R.id.about_us:
                startActivity(new Intent(CheckPaymentActivity.this,AboutUsAdminActivity.class));
                break;
            //navigates to the AdminDashboard activity
            case android.R.id.home:
                startActivity(new Intent(CheckPaymentActivity.this,AdminDashBoardActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

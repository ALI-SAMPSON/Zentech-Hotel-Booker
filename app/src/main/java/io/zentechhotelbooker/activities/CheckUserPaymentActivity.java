package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechhotelbooker.R;
import io.zentechhotelbooker.adapters.RecyclerViewAdapterUserPayment;
import io.zentechhotelbooker.models.Payments;
import io.zentechhotelbooker.models.Users;

public class CheckUserPaymentActivity extends AppCompatActivity {

    FirebaseListAdapter firebaseListAdapter;

    ListView payment_listView;

    Payments payments;

    Users users;

    FirebaseAuth mAuth;

    // Recycler View Objects
    DatabaseReference mDatabaseRef;

    RecyclerView recyclerView;

    RecyclerViewAdapterUserPayment recyclerViewAdapterUserPayment;

    List<Payments> user_payment_list;

    ProgressBar progressBar;

    // Creating a ValueEvent Listener.
    private ValueEventListener mDBListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user_payment);

        //checks if the support actionbar is not null
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.my_payments));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();

        //mDatabaseRef = FirebaseDatabase.getInstance().getReference("Payment").child(payments.getRoom_number());

        //payment_listView = findViewById(R.id.payment_listView);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new GridLayoutManager(CheckUserPaymentActivity.this,2));

        user_payment_list = new ArrayList<>();

        recyclerViewAdapterUserPayment = new RecyclerViewAdapterUserPayment(CheckUserPaymentActivity.this,user_payment_list);

        recyclerView.setAdapter(recyclerViewAdapterUserPayment);

        payments = new Payments();

        // Assign activity this to progress bar.
        progressBar = findViewById(R.id.progressBar);

        //displayPayments();

        //displayUserPayments();


    }

    private void displayUserPayments(){

        progressBar.setVisibility(View.VISIBLE);

        final FirebaseUser user = mAuth.getCurrentUser();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Payments");

        Query query = mDatabaseRef.child("03").orderByChild("user_name");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    if (payments.getUser_name().equals(user.getDisplayName())) {
                        Payments payments = dataSnapshot.getValue(Payments.class);

                        user_payment_list.add(payments);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



       /*mDBListener =  mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // clears the list on data change
                user_payment_list.clear();

               for(DataSnapshot snapshot : dataSnapshot.getChildren()) {

                   Payments user_payments = snapshot.getValue(Payments.class);

                   user_payment_list.add(user_payments);

               }
                recyclerViewAdapterUserPayment.notifyDataSetChanged();

                // dismiss the progressBar
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Hiding the progress Bar
                progressBar.setVisibility(View.GONE);

                // displays an error message from the database
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    */

    }


    //method to populate payments into listView
    public void displayPayments() {

        FirebaseUser user = mAuth.getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference("Payments");

        if(payments.getUser_name().equals(user.getDisplayName())){

        FirebaseListOptions<Payments> options = new FirebaseListOptions.Builder<Payments>()
                .setLayout(R.layout.payment_list_item)
                .setQuery(query, Payments.class)
                .build();

        firebaseListAdapter = new FirebaseListAdapter(options) {
            @Override
            protected void populateView(View v, Object model, int position) {

                //TextView and imageView to populate the data from the database
                TextView username = v.findViewById(R.id.user_name);
                TextView room_number = v.findViewById(R.id.room_number);
                TextView price = v.findViewById(R.id.room_price);
                TextView mobile_number = v.findViewById(R.id.mobile_number);
                CircleImageView circleImageView = v.findViewById(R.id.user_image);

                //typecasting the payments to the Object model
                Payments payments = (Payments) model;

                //setting the Text of the various textViews to the payment info in the database;
                username.setText(" Username : " + payments.getUser_name());
                room_number.setText(" Room Type : " + payments.getRoom_type());
                price.setText(" Price : GHC " + payments.getPrice());
                mobile_number.setText(" Mobile Money Number : " + payments.getMobile_number());
                Glide.with(CheckUserPaymentActivity.this).load(payments.getImageUrl()).into(circleImageView);

            }
        };
    }

        payment_listView.setAdapter(firebaseListAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //firebaseListAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //firebaseListAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // removes the Value Listener when activity is destroy
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // goes back to Home Activity
                startActivity(new Intent(CheckUserPaymentActivity.this, HomeActivity.class));
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

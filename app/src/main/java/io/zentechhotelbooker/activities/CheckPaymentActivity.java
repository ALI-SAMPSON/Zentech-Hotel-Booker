package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechhotelbooker.R;
import io.zentechhotelbooker.adapters.RecyclerViewAdapterPayment;
import io.zentechhotelbooker.models.Payments;
import io.zentechhotelbooker.models.Rooms;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

public class CheckPaymentActivity extends AppCompatActivity {

    FirebaseListAdapter firebaseListAdapter;

    ListView listView;

    Payments payments;

    Users users;

    FirebaseAuth mAuth;

    FirebaseUser user;

    // Recycler View Objects
    DatabaseReference mDatabaseRef;

    RecyclerView recyclerView;

    RecyclerViewAdapterPayment recyclerViewAdapterPayment;

    List<Payments> paymentsList;

    ProgressBar progressBar;

    // Creating a ValueEvent Listener.
    private ValueEventListener mDBListener;

    private static String Payment_path = "Payments";

    // material searchView
    MaterialSearchView searchView;

    ArrayList<Payments> searchList;

    // Creating DataReference
    DatabaseReference dBRef;

    // ImageView and TextView for error checking
    ImageView errorImage;
    TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_payment);

        // Creating an object of the toolbar
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //checks if the support actionbar is not null
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.check_payments));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        errorImage = findViewById(R.id.error_icon);
        errorText = findViewById(R.id.tv_error);

        // Assign id to RecyclerView.
        recyclerView = findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new GridLayoutManager(CheckPaymentActivity.this, 2));

        paymentsList = new ArrayList<>();

        searchList = new ArrayList<>();

        recyclerViewAdapterPayment = new RecyclerViewAdapterPayment(CheckPaymentActivity.this,paymentsList);

        recyclerView.setAdapter(recyclerViewAdapterPayment);

        // Assign activity this to progress bar.
        progressBar = findViewById(R.id.progressBar);

        payments = new Payments();

        users = new Users();

        //listView = findViewById(R.id.payment_listView);

        // getting an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

        user = mAuth.getCurrentUser();

        // Method call
        showPayment();

        //displayPayments(); ///call to the method

    }

    // method to populate payments into RecyclerView
    public void showPayment(){

        // displays the progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Payments");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // clears the list on data change
                paymentsList.clear();

                for(DataSnapshot paymentSnapshot : dataSnapshot.getChildren()){

                    Payments payments = paymentSnapshot.getValue(Payments.class);

                    paymentsList.add(payments);

                    // checks if there is no payment made
                    /*if(!dataSnapshot.exists() || paymentSnapshot.getValue(Payments.class) == null){
                        // set the visibility of the views to visible
                        errorImage.setVisibility(View.VISIBLE);
                        errorText.setVisibility(View.VISIBLE);
                    }
                    */

                }

                recyclerViewAdapterPayment.notifyDataSetChanged();

                // dismiss the progressBar
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress bar.
                progressBar.setVisibility(View.GONE);

                // displays a DatabaseError exception if error occurs
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }

    //method to populate payments into listView
    public void displayPayments(){

        //final FirebaseUser user = mAuth.getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference().child("Payments");

        FirebaseListOptions<Payments> options = new FirebaseListOptions.Builder<Payments>()
                .setLayout(R.layout.recyclerview_payment_items)
                .setQuery(query,Payments.class)
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
                Glide.with(CheckPaymentActivity.this).load(payments.getImageUrl()).into(circleImageView);

            }
        };

        listView.setAdapter(firebaseListAdapter);

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
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.menu_search);

        searchView = findViewById(R.id.search_view);
        searchView.setMenuItem(item);
        searchView.setSubmitOnClick(true);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // test if searchView is not empty
                if(!query.isEmpty()){
                    searchForRoom(query);
                    searchView.clearFocus();
                }

                //else
                else{
                    searchForRoom("");
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // test if searchView is not empty
                if(!query.isEmpty()){
                    searchForRoom(query);
                }
                else{
                    searchForRoom("");
                }
                return true;
            }
        });

        return true;
    }

    private void searchForRoom(String s){

        dBRef = FirebaseDatabase.getInstance().getReference(Payment_path);

        Query query = dBRef.orderByChild("user_name")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()){

                    // clears the searchList
                    searchList.clear();

                    for(DataSnapshot paymentSnapshot : dataSnapshot.getChildren()){

                        final Payments payments = paymentSnapshot.getValue(Payments.class);

                        //adds the rooms searched to the arrayList
                        searchList.add(payments);

                    }

                    RecyclerViewAdapterPayment mAdapterSearchPayment =
                            new RecyclerViewAdapterPayment(CheckPaymentActivity.this, searchList);
                    recyclerView.setAdapter(mAdapterSearchPayment);
                    mAdapterSearchPayment.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // display message if error occurs
                Toast.makeText(CheckPaymentActivity.this,
                        databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //navigates to the AdminDashboard activity
            case android.R.id.home:
                startActivity(new Intent(CheckPaymentActivity.this,AdminDashBoardActivity.class));
                // Add a up-to-bottom animation to the activity
                CustomIntent.customType(CheckPaymentActivity.this,"fadein-to-fadeout");
                // finishes the activity
                finish();
                break;
            case R.id.menu_welcome:
                Toast.makeText(CheckPaymentActivity.this,
                        getString(R.string.welcome_text),
                        Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //navigates to the AdminDashboard activity
        startActivity(new Intent(CheckPaymentActivity.this,AdminDashBoardActivity.class));
        // Add a up-to-bottom animation to the activity
        CustomIntent.customType(CheckPaymentActivity.this,"fadein-to-fadeout");
        // finishes the activity
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        // Add a up-to-bottom animation to the activity
        CustomIntent.customType(CheckPaymentActivity.this,"up-to-bottom");
    }
}

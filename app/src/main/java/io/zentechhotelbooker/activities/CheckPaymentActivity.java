package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.graphics.PorterDuff;
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

    Toolbar toolbar;

    TextView tv_no_rooms;

    Payments payments;

    Users users;

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

    // Creating DataReference
    DatabaseReference dBRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_payment);

        // Creating an object of the toolbar
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //checks if the support actionbar is not null
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.check_payments));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // textView to indicate there are no rooms added yet
        tv_no_rooms = findViewById(R.id.tv_no_rooms);

        // getting reference to progress bar.
        progressBar = findViewById(R.id.progressBar);
        // change the color of the progressBar
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.MULTIPLY);

        payments = new Payments();

        users = new Users();

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(Payment_path);

        // Assign id to RecyclerView.
        recyclerView = findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new GridLayoutManager(CheckPaymentActivity.this, 2));

        paymentsList = new ArrayList<>();

        // creating object of the RecyclerView Adapter class and setting it to the adapter of the recyclerView
        recyclerViewAdapterPayment = new RecyclerViewAdapterPayment(CheckPaymentActivity.this,paymentsList);

        recyclerView.setAdapter(recyclerViewAdapterPayment);

        // Method call
        displayPaymentMade();

    }

    // method to populate payments into RecyclerView
    private void  displayPaymentMade(){

        // displays the progress bar
        progressBar.setVisibility(View.VISIBLE);

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // clears the list on data change
                paymentsList.clear();

                for(DataSnapshot paymentSnapshot : dataSnapshot.getChildren()){

                    Payments payments = paymentSnapshot.getValue(Payments.class);

                    // set visibility to gone
                    tv_no_rooms.setVisibility(View.GONE);
                    // sets visibility to Visible if rooms are added to DB
                    recyclerView.setVisibility(View.VISIBLE);

                    // adding payments to list
                    paymentsList.add(payments);

                }

                // checks if no payment is made yet
                if(!dataSnapshot.exists()){
                    // hides recyclerView and makes textView visible
                    recyclerView.setVisibility(View.GONE);
                    tv_no_rooms.setVisibility(View.VISIBLE);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // removes the dBListener
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
                    searchForRoom(query.toLowerCase());
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
                    searchForRoom(query.toLowerCase());
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

        Query query = mDatabaseRef.orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    // clears the searchList
                    paymentsList.clear();

                    for(DataSnapshot paymentSnapshot : dataSnapshot.getChildren()){

                        Payments payments = paymentSnapshot.getValue(Payments.class);

                        //adds the rooms searched to the arrayList
                        paymentsList.add(payments);

                    }

                    recyclerViewAdapterPayment.notifyDataSetChanged();

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
        CustomIntent.customType(CheckPaymentActivity.this,"up-to-bottom");
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

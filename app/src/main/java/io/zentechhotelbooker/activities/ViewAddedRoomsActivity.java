package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.adapters.RecyclerViewAdapterAdmin;
import io.zentechhotelbooker.models.Rooms;

public class ViewAddedRoomsActivity extends AppCompatActivity {

    // Creating DatabaseReference.
    DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerViewAdapterAdmin
    RecyclerViewAdapterAdmin recyclerViewAdapterAdmin;

    // Creating List of Rooms class.
    List<Rooms> roomsList = new ArrayList<>();

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_added_rooms);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("VIEW AND DELETE ROOMS");
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Assign id to RecyclerView.
        recyclerView = findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new GridLayoutManager(ViewAddedRoomsActivity.this,2));

        // Assign activity this to progress bar.
        progressBar = findViewById(R.id.progressBar);

        //viewRooms();

        // Calling the method to display rooms in the firebase database
        loadUploadedRoomDetails();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Calling the method to delete rooms in the firebase database
        //deleteRoom();

    }

    // method to delete a selected room from database
    /*private void deleteRoom(){

        // Creating an object of the RecyclerAdapter
        recyclerViewAdapterAdmin = new RecyclerViewAdapterAdmin(getApplicationContext(),roomsList);


    }
    */

    // Method to load rooms uploaded into database
    private void loadUploadedRoomDetails(){

        // displays the progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference(AddRoomsActivity.Database_Path);

        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Rooms rooms = postSnapshot.getValue(Rooms.class);

                    roomsList.add(rooms);
                }

                // Creating an object of the RecyclerAdapter
                recyclerViewAdapterAdmin = new RecyclerViewAdapterAdmin(ViewAddedRoomsActivity.this,roomsList);

                recyclerView.setAdapter(recyclerViewAdapterAdmin);

                // Hiding the progress bar.
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.about_us:
                //start the AboutUsAdmin Activity
                startActivity(new Intent(ViewAddedRoomsActivity.this,AboutUsAdminActivity.class));
                break;

            case android.R.id.home:
                //goes back to the AdminDashboard
                startActivity(new Intent(ViewAddedRoomsActivity.this,AdminDashBoardActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

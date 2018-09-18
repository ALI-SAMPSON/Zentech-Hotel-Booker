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

public class DeleteRoomsActivity extends AppCompatActivity {

    // Creating DatabaseReference.
    DatabaseReference mDatabaseRef;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating a ValueEvent Listener.
    private ValueEventListener mDBListener;

    // Creating RecyclerViewAdapterAdmin
    RecyclerViewAdapterAdmin recyclerViewAdapterAdmin;

    // Creating List of Rooms class.
    List<Rooms> roomsList;

    // Creating a progressBar
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_rooms);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.delete_rooms));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Assign id to RecyclerView.
        recyclerView = findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new GridLayoutManager(DeleteRoomsActivity.this,2));

        roomsList = new ArrayList<>();

        // Creating an object of the RecyclerAdapter
        recyclerViewAdapterAdmin = new RecyclerViewAdapterAdmin(DeleteRoomsActivity.this,roomsList);

        recyclerView.setAdapter(recyclerViewAdapterAdmin);

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

    // Method to load rooms uploaded into database
    private void loadUploadedRoomDetails(){

        // displays the progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        mDatabaseRef = FirebaseDatabase.getInstance().getReference(AddRoomsActivity.Database_Path);

        // Adding Add Value Event Listener to databaseReference.
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // clears the list on data change
                roomsList.clear();

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    // gets a snapshot of the rooms in the Rooms class
                    Rooms rooms = postSnapshot.getValue(Rooms.class);
                    // get the unique key stored int the Rooms class
                    rooms.setKey(postSnapshot.getKey());
                    // adds the rooms to list of rooms(RoomList)
                    roomsList.add(rooms);
                }

                // refreshes the recyclerview after data change
                recyclerViewAdapterAdmin.notifyDataSetChanged();

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
    protected void onDestroy() {
        super.onDestroy();
        // removes the ValueEventListener anytime the activity is destroyed
        mDatabaseRef.removeEventListener(mDBListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                //goes back to the AdminDashboard
                startActivity(new Intent(DeleteRoomsActivity.this,AdminDashBoardActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

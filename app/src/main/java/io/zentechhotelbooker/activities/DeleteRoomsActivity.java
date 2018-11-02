package io.zentechhotelbooker.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.adapters.RecyclerViewAdapterAdmin;
import io.zentechhotelbooker.models.Rooms;
import maes.tech.intentanim.CustomIntent;

public class DeleteRoomsActivity extends AppCompatActivity implements RecyclerViewAdapterAdmin.onItemClickListener {

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

    FirebaseStorage mStorage;

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

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(recyclerViewAdapterAdmin);

        recyclerViewAdapterAdmin.setOnItemClickListener(DeleteRoomsActivity.this);

        // Assign activity this to progress bar.
        progressBar = findViewById(R.id.progressBar);

        // Calling the method to display rooms in the firebase database
        loadUploadedRoomDetails();

        // creating an instance of a Firebase Storage
        mStorage = FirebaseStorage.getInstance();

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

                // Adds a bottom-to-up animations to the activity
                CustomIntent.customType(DeleteRoomsActivity.this,"fadein-to-fadeout");

                // finishes the activity
                finish();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //goes back to the AdminDashboard
        startActivity(new Intent(DeleteRoomsActivity.this,AdminDashBoardActivity.class));

        // Adds a bottom-to-up animations to the activity
        CustomIntent.customType(DeleteRoomsActivity.this,"fadein-to-fadeout");

        // finishes the activity
        finish();
    }

    // Methods implemented in the recyclerView for this activity
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this," long click on a room to view options ",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancelClick(int position) {
        //Toast.makeText(this," View Rooms is clicked at position : " + position,Toast.LENGTH_SHORT).show();
    }

    // Method to delete Rooms from the system
    @Override
    public void onDeleteClick(final int position) {
        // getting the position of each room and its details
        final Rooms rooms = roomsList.get(position);
        // Create an an alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Sets a Title and a Message on it
        builder.setTitle("Delete Room");
        builder.setMessage("Are you sure you want to delete this room");
        builder.setCancelable(false);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                progressBar.setVisibility(View.VISIBLE);

                final Rooms selectedRoom = roomsList.get(position);
                final String selectedKey = selectedRoom.getKey();

                // getting the reference of the room
                StorageReference roomImageRef = mStorage.getReferenceFromUrl(selectedRoom.getRoomImage_url());
                roomImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //remove value from FirebaseDatabase
                        mDatabaseRef.child(selectedKey).removeValue();

                        // dismiss the progress bar
                        progressBar.setVisibility(View.GONE);

                        // File deleted successfully message
                        Toast.makeText(DeleteRoomsActivity.this," Room deleted Successfully ",Toast.LENGTH_LONG).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // dismiss the progress bar
                        progressBar.setVisibility(View.GONE);

                        // File deleted successfully message
                        Toast.makeText(DeleteRoomsActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss the alert Dialog
                dialogInterface.dismiss();
            }
        });

        // Creates a new AlertDialog and displays it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void finish() {
        super.finish();
        // Adds a bottom-to-up animations to the activity
        CustomIntent.customType(DeleteRoomsActivity.this,"bottom-to-up");
    }
}

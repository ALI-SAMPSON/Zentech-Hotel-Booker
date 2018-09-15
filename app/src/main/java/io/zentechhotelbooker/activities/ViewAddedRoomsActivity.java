package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechhotelbooker.R;
import io.zentechhotelbooker.adapters.RecyclerViewAdapter;
import io.zentechhotelbooker.models.Rooms;

public class ViewAddedRoomsActivity extends AppCompatActivity {

    // Creating DatabaseReference.
    DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerViewAdapter
    RecyclerViewAdapter recyclerViewAdapter;

    // Creating List of ImageUploadInfo class.
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

        // Calling the method to loadUploadedRooms in firebase database
        loadUploadedRoomDetails();
    }


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

                recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),roomsList);

                recyclerView.setAdapter(recyclerViewAdapter);

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

    //methods for getting available rooms from db and populating on the listView
    public void viewRooms(){

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

package io.zentechhotelbooker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechhotelbooker.R;
import io.zentechhotelbooker.adapters.RecyclerViewAdapterUser;
import io.zentechhotelbooker.models.Rooms;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mToggle;

    private NavigationView navigationView;

    private FirebaseAuth mAuth;

    private Users users;

    // Creating DataReference
    DatabaseReference databaseReference;

    // Creating RecyclerView
    RecyclerView recyclerView;

    // Creating RecyclerViewAdapterAdmin
    RecyclerViewAdapterUser recyclerViewAdapterUser;

    // Creating a ValueEvent Listener.
    private ValueEventListener mDBListener;

    // Creating List of Rooms class.
    List<Rooms> roomsList;

    // Creating progressBar
    ProgressBar progressBar;

    // object to inflate the views of the navigation drawer
    private CircleImageView circleImageView;
    private TextView username;
    private TextView email;

    // boolean variable to check for doubleBackPress to exit app
    private boolean doublePressBackToExitApp = false;

    /* boolean variable to launch Alert Dialog
     upon successful login into the application*/
    private  static boolean isFirstRun = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mDrawerLayout = findViewById(R.id.drawer);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        circleImageView = navigationView.getHeaderView(0).findViewById(R.id.circularImageView);
        username = navigationView.getHeaderView(0).findViewById(R.id.username);
        email = navigationView.getHeaderView(0).findViewById(R.id.email);

        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.home));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        // Assign id to RecyclerView.
        recyclerView = findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Sets the Layout of the Recycler View and uses a spanCount of 2
        recyclerView.setLayoutManager(new GridLayoutManager(HomeActivity.this,2));

        roomsList = new ArrayList<>();

        recyclerViewAdapterUser = new RecyclerViewAdapterUser(HomeActivity.this,roomsList);

        recyclerView.setAdapter(recyclerViewAdapterUser);

        // Assign id to ProgressBar
        progressBar = findViewById(R.id.progressBar);

        // displays the progressBsr
        progressBar.setVisibility(View.VISIBLE);

        users = new Users();

        // Getting instance of the FirbaseAuth class
        mAuth = FirebaseAuth.getInstance();

        // Calling method to display a welcome message
        displayWelcomeMessage();

    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() == null){

            // finishes activity
            finish();

            // starts the login activity currently logged in user is null(no logged in user)
            startActivity(new Intent(this,UserLoginActivity.class));

            // Adds a fadein-fadeout animations to the activity
            CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");
        }
        else if(mAuth.getCurrentUser() != null){

            // starts the home activity if user is already logged in
            progressBar.setVisibility(View.GONE);

            // call to the method to load user details into their respective views
            loadUserInfo();

            // call to the method to load rooms from Firbase Database
            loadUploadedRoomDetails();

        }

    }

    // Method to display a welcome  message to user when he or she logs in
    private void displayWelcomeMessage() {

        // get current logged in user
        FirebaseUser user = mAuth.getCurrentUser();

        /* getting username of the currently
         Logged In user and storing in a string */
        String username = user.getDisplayName();

        // checks if user is not equal to null
        if (user != null) {

        if (isFirstRun) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                AlertDialog.Builder builder = new
                        AlertDialog.Builder(HomeActivity.this,
                        android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle(" Welcome, " + username);
                builder.setMessage(getString(R.string.welcome_message));
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.app_icon_round);

                builder.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // closes the Alert dialog
                        dialogInterface.dismiss();
                    }
                });
                // Creates and displays the alert Dialog
                AlertDialog alert = builder.create();
                alert.show();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle(" Welcome, " + username);
                builder.setMessage(getString(R.string.welcome_message));
                builder.setCancelable(false);
                builder.setIcon(R.mipmap.app_icon_round);

                builder.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // closes the Alert dialog
                        dialogInterface.dismiss();
                    }
                });
                // Creates and displays the alert Dialog
                AlertDialog alert = builder.create();
                //builder.show();
                alert.show();
            }
        }
     }
        // sets it to false
        isFirstRun = false;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // dismisses any instance of user
        databaseReference.removeEventListener(mDBListener);
    }

    private void loadUserInfo(){

        // get current logged in user
        FirebaseUser user = mAuth.getCurrentUser();

        // getting the username and image of current logged in user
        String _photoUrl = user.getPhotoUrl().toString();
        String _username = user.getDisplayName();
        String _email = user.getEmail();

        // checks if current user is not null
        if(user != null){
            // checks if the photo of the current user is not null
            if(user.getPhotoUrl() != null ){
                Glide.with(HomeActivity.this)
                        .load(_photoUrl)
                        .into(circleImageView);
            }
            // checks if the username of the current user is not null
            if(user.getDisplayName() != null){
                username.setText(" Username : " + _username);
            }
            // checks if the email of the current user is not null
            if(user.getEmail() != null){
                email.setText(" Email : " + _email);
            }

        }

    }

    private void loadUploadedRoomDetails(){

        // displays the progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        databaseReference = FirebaseDatabase.getInstance().getReference(AddRoomsActivity.Database_Path);

        // Adding Add Value Event Listener to databaseReference.
        mDBListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // clears the list on data change
                roomsList.clear();

                // getting snapshot of rooms available in the database
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Rooms rooms = postSnapshot.getValue(Rooms.class);
                    // adding the rooms to the List of rooms
                    roomsList.add(rooms);
                }

                // refreshes the recyclerview after data change
                recyclerViewAdapterUser.notifyDataSetChanged();

                // Hiding the progress bar.
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_user,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.menu_about_us:
                // finishes the activity
                finish();
                // open AboutUsUserActivity activity
                startActivity(new Intent(HomeActivity.this,AboutUsUserActivity.class));
                // Add fadein-to-fadeout animation to the activity
                CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
                break;
            case R.id.menu_exit:
                // call to the method to exit the application
                exitApplication();
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_home:
                // open this activity
                break;
            case R.id.menu_profile:
                // open UpdateUserProfileActivity activity
                startActivity(new Intent(HomeActivity.this,UpdateUserProfileActivity.class));
                // Add fadein-to-fadeout animation to the activity
                CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
                break;
            /*case R.id.menu_payment:
                // open CheckUserPaymentActivity activity
                startActivity(new Intent(HomeActivity.this,CheckUserPaymentActivity.class));
                break;*/
            /*case R.id.menu_booked_rooms:
                // open this activity
                break;*/
            case R.id.menu_contact:
                // open ContactUsUserActivity activity
                startActivity(new Intent(HomeActivity.this,ContactUsUserActivity.class));
                // Add fadein-to-fadeout animation to the activity
                CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
                break;
            case R.id.menu_sign_out:
                // log user out of the system
                logout();
                break;
                default:
                    break;
        }

        // closes the navigation drawer to the left of the screen
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // method to close the app and kill all processes
    private void exitApplication(){

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,
                android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Exit Application");
        builder.setMessage("Are you sure you want to exit application?");
        builder.setCancelable(false);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // create and displays the alert dialog
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {

        if(doublePressBackToExitApp){
            super.onBackPressed();
            return;
        }
        doublePressBackToExitApp = true;
        // display a toast message to user
        Toast.makeText(HomeActivity.this,getString(R.string.exit_app_message),Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        },2000);

    }

    // method to log user out of the system
    private void logout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,
                android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(getString(R.string.logout));
        builder.setMessage(getString(R.string.logout_msg));

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // logs current user out of the system
                mAuth.signOut();
                // starts the UserLoginActivity
                startActivity(new Intent(HomeActivity.this,UserLoginActivity.class));
                // Add fadein-to-fadeout animation to the activity
                CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
                // finishes the activity
                finish();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // creates and shows the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void finish() {
        super.finish();
        // Add fadein-to-fadeout animation to the activity
        CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
    }
}

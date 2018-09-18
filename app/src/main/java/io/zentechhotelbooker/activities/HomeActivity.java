package io.zentechhotelbooker.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
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
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechhotelbooker.R;
import io.zentechhotelbooker.adapters.RecyclerViewAdapterAdmin;
import io.zentechhotelbooker.adapters.RecyclerViewAdapterUser;
import io.zentechhotelbooker.models.Rooms;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mToggle;

    private NavigationView navigationView;

    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;

    // Creating DataReference
    DatabaseReference databaseReference;

    // Creating RecyclerView
    RecyclerView recyclerView;

    // Creating RecyclerViewAdapterAdmin
    RecyclerViewAdapterUser recyclerViewAdapterUser;

    // Creating List of Rooms class.
    List<Rooms> roomsList = new ArrayList<>();

    // Creating progressBar
    ProgressBar progressBar;

    // object to inflate the views of the navigation drawer
    private CircleImageView circleImageView;
    private TextView username;
    private TextView email;

    // Creating Alert Dialog
    //AlertDialog.Builder builder;

    AlertDialog alertDialog;


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

        // Assign id to ProgressBar
        progressBar = findViewById(R.id.progressBar);

        // displays the progressBsr
        progressBar.setVisibility(View.VISIBLE);

        // Getting instance of the FirbaseAuth class
        mAuth = FirebaseAuth.getInstance();

        // call to the method to load user details into their respective views
        loadUserInfo();

        // call to the method to load rooms from Firbase Database
        loadUploadedRoomDetails();

        // method call to welcomeMessage Method
        //displayWelcomeMessage();

    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() == null){
            HomeActivity.this.finish();
            // starts the login activity currently logged in user is null(no logged in user)
            startActivity(new Intent(this,UserLoginActivity.class));
        }
        else if(mAuth.getCurrentUser() != null){
            // starts the home activity if user is already logged in
            progressBar.setVisibility(View.GONE);

            // method call to welcomeMessage Method
            //displayWelcomeMessage();
        }


    }

    // Method to display a welcome  message to user when he or she logs in
    private void displayWelcomeMessage(){

        FirebaseUser user = mAuth.getCurrentUser();

        // Creating Alert Dialog
        //AlertDialog.Builder builder;

        if(user != null ) {

            // Checks if the API Version of the phone is 21
            // and above and set the theme accordingly
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,
                        android.R.style.Theme_Material_Dialog_Alert);
                builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle(" Welcome, " + user.getDisplayName());
                builder.setMessage(getString(R.string.welcome_message));
                builder.setCancelable(false);
                builder.setIcon(R.drawable.app_logo);

                builder.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // closes the Alert dialog
                        dialogInterface.dismiss();
                    }
                });
                // Creates and displays the alert Dialog
                alertDialog = builder.create();
                alertDialog.show();
            }
            else {

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle(" Welcome, " + user.getDisplayName());
                builder.setMessage(getString(R.string.welcome_message));
                builder.setCancelable(false);
                builder.setIcon(R.drawable.app_logo);

                builder.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // closes the Alert dialog
                        dialogInterface.dismiss();
                    }
                });
                // Creates and displays the alert Dialog
                alertDialog = builder.create();
                alertDialog.show();
            }
        }
        else {
            //AlertDialog.Builder builder;
            // Checks if the API Version of the phone is 21
            // and above and set the theme accordingly
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,
                        android.R.style.Theme_Material_Dialog_Alert);
                builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle(" Welcome, username");
                builder.setMessage(getString(R.string.welcome_message));
                builder.setCancelable(false);
                builder.setIcon(R.drawable.app_logo);

                builder.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // closes the Alert dialog
                        dialogInterface.dismiss();
                    }
                });
                // Creates and displays the alert Dialog
                alertDialog = builder.create();
                alertDialog.show();
            }
            else {

                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle(" Welcome, username");
                builder.setMessage(getString(R.string.welcome_message));
                builder.setCancelable(false);
                builder.setIcon(R.drawable.app_logo);

                builder.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // closes the Alert dialog
                        dialogInterface.dismiss();
                    }
                });
                // Creates and displays the alert Dialog
                alertDialog = builder.create();
                alertDialog.show();
             }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // dismisses any instance of Alert Dialog
        //alertDialog.dismiss();
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
                Glide.with(this)
                        .load(_photoUrl)
                        .into(circleImageView);
            }
            // checks if the username of the current user is not null
            if(user.getDisplayName() != null){
                username.setText("Username : " + _username);
            }
            // checks if the email of the current user is not null
            if(user.getEmail() != null){
                email.setText("Email : " + _email);
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
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // getting snapshot of rooms available in the database
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){

                    Rooms rooms = postSnapshot.getValue(Rooms.class);
                    // adding the rooms to the List of rooms
                    roomsList.add(rooms);
                }

                recyclerViewAdapterUser = new RecyclerViewAdapterUser(HomeActivity.this,roomsList);

                recyclerView.setAdapter(recyclerViewAdapterUser);

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
                // open AboutUsUserActivity activity
                startActivity(new Intent(HomeActivity.this,AboutUsUserActivity.class));
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
                break;
            case R.id.menu_payment:
                // open this activity
                break;
            /*case R.id.menu_booked_rooms:
                // open this activity
                break;*/
            case R.id.menu_contact:
                // open ContactUsUserActivity activity
                startActivity(new Intent(HomeActivity.this,ContactUsUserActivity.class));
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

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Exit Application?");
        alertDialogBuilder
                .setMessage("Are you sure you want to exit application?")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        dialogInterface.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // method to log user out of the system
    private void logout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.logout));
        builder.setMessage(getString(R.string.logout_msg));

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //progressDialog.dismiss();
                        timer.cancel();
                    }
                },10000);
                // logs current user out of the system
                mAuth.signOut();
                HomeActivity.this.finish();
                startActivity(new Intent(HomeActivity.this,UserLoginActivity.class));
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}

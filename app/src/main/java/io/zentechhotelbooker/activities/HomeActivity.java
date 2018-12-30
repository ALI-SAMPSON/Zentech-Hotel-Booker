package io.zentechhotelbooker.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
// search bar imported libraries
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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
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
import io.zentechhotelbooker.adapters.RecyclerViewAdapterUser;
import io.zentechhotelbooker.models.Rooms;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;


public class HomeActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private NavigationView navigationView;

    private FirebaseAuth mAuth;

    private Users users;

    private Rooms rooms;

    // Creating DataReference
    DatabaseReference databaseReference;

    DatabaseReference roomRef;

    DatabaseReference userInfoRef;

    // Creating RecyclerView
    RecyclerView recyclerView;

    // Creating RecyclerViewAdapterManageRooms
    RecyclerViewAdapterUser recyclerViewAdapterUser;

    // Creating a ValueEvent Listener.
    private ValueEventListener mDBListener;

    // Creating List of Rooms class.
    List<Rooms> roomsList;

    ArrayList<Rooms> searchList;

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

    Toolbar toolbar;

    // material searchView
    MaterialSearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);

        mDrawerLayout = findViewById(R.id.drawer);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        circleImageView = navigationView.getHeaderView(0).findViewById(R.id.circularImageView);
        username = navigationView.getHeaderView(0).findViewById(R.id.username);
        email = navigationView.getHeaderView(0).findViewById(R.id.email);

        // Checks for available support action bar
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.home));
            getSupportActionBar().setElevation(5.0f);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Adding a custom animation
                YoYo.with(Techniques.RubberBand).playOn(circleImageView);
            }
        });

        // Adding an OnClickListener
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Adding a custom animation
                YoYo.with(Techniques.Shake).playOn(username);
            }
        });

        // Adding an OnClickListener
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Adding a custom animation
                YoYo.with(Techniques.Shake).playOn(email);
            }
        });

        // Assign id to RecyclerView.
        recyclerView = findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Sets the Layout of the Recycler View and uses a spanCount of 2
        recyclerView.setLayoutManager(new GridLayoutManager(HomeActivity.this,2));

        searchList = new ArrayList<>();

        roomsList = new ArrayList<>();

        recyclerViewAdapterUser = new RecyclerViewAdapterUser(HomeActivity.this,roomsList);

        recyclerView.setAdapter(recyclerViewAdapterUser);

        // Assign id to ProgressBar
        progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        // displays the progressBsr
        progressBar.setVisibility(View.VISIBLE);

        users = new Users();

        rooms = new Rooms();

        // Getting instance of the FirbaseAuth class
        mAuth = FirebaseAuth.getInstance();

        roomRef = FirebaseDatabase.getInstance().getReference("Rooms");

        // Calling method to display a welcome message
        displayWelcomeMessage();

        // call to the method to load user details into their respective views
        loadUserInfo();

        // call to the method to load rooms from Firbase Database
        loadUploadedRoomDetails();

    }

    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() == null){

            // starts the login activity currently logged in user is null(no logged in user)
            startActivity(new Intent(this,UserLoginActivity.class));

            // Adds a fadein-fadeout animations to the activity
            CustomIntent.customType(HomeActivity.this, "fadein-to-fadeout");

            // finishes activity
            finish();
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
        final FirebaseUser user = mAuth.getCurrentUser();

        // getting the username and image of current logged in user
        assert user != null;
        //final String _photoUrl = user.getPhotoUrl().toString();
        final String _username = user.getDisplayName();
        final String _email = user.getEmail();

        userInfoRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        userInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Users users = dataSnapshot.getValue(Users.class);

                // checks if current user is not null
                if(user != null){

                    username.setText(" Username : " + _username);
                    email.setText(" Email : " + _email);

                    // checks if the photo of the current user is not null
                    assert users != null;
                    if(users.getImageUrl() == null){
                        circleImageView.setImageResource(R.drawable.avatar_placeholder);
                    }
                    else{
                        Glide.with(getApplicationContext()).load(users.getImageUrl()).into(circleImageView);
                    }

                }
                else{
                    username.setText(" Username : " + _username);
                    email.setText(" Email : " + _email);
                    circleImageView.setImageResource(R.drawable.avatar_placeholder);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // displays an error message from the database
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });



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

        MenuInflater Inflater = getMenuInflater();
        Inflater.inflate(R.menu.menu_user,menu);
        MenuItem item = menu.findItem(R.id.menu_search);

        // Material SearchView
        searchView = findViewById(R.id.search_view);
        searchView.setMenuItem(item);
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setEllipsize(true);
        searchView.setSubmitOnClick(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // if searchView is not empty
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
                // if searchView is not empty
                if(!query.isEmpty()){
                    searchForRoom(query.toLowerCase());
                }

                //else
                else{
                    searchForRoom("");
                }

                return true;
            }
        });

        return true;
    }

    // method to perform search in database for room type
    private void searchForRoom(String s)
    {
        // gets reference to the database
        databaseReference = FirebaseDatabase.getInstance().getReference(AddRoomsActivity.Database_Path);

        // creates a query to initiate the search
        Query query =  databaseReference.orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()){

                    //clears the arrayList
                    searchList.clear();

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        final Rooms rooms = snapshot.getValue(Rooms.class);

                        //adds the rooms searched to the arrayList
                        searchList.add(rooms);

                    }

                    RecyclerViewAdapterUser recyclerViewAdapterUserSearch =
                            new RecyclerViewAdapterUser(HomeActivity.this,searchList);
                    recyclerView.setAdapter(recyclerViewAdapterUserSearch);
                    recyclerViewAdapterUserSearch.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // display message if error occurs
                Toast.makeText(HomeActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    @TargetApi(11)
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.menu_search:
                // call method
               return true;
            /*case android.R.id.home:
                onBackPressed();
                return true;*/
            case R.id.menu_about_us:
                // method call
                openAboutUs();
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
                signOut();
                break;
                default:
                    break;
        }

        // closes the navigation drawer to the left of the screen
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Method to Open About Us page
    private void openAboutUs(){
        // open AboutUsUserActivity activity
        startActivity(new Intent(HomeActivity.this,AboutUsUserActivity.class));
        // Add left-to-right animation to the activity
        CustomIntent.customType(HomeActivity.this,"left-to-right");
        // finishes the activity
        finish();
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
        //checks if searchView is opened
        if(searchView.isSearchOpen()){
            searchView.closeSearch();
        }

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
    private void  signOut(){

        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this,
                android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle(getString(R.string.sign_out));
        builder.setMessage(getString(R.string.sign_out_msg));

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // logs current user out of the system
                mAuth.signOut();

                // finish the activity
                finish();

                // starts the UserLoginActivity
                startActivity(new Intent(HomeActivity.this,UserLoginActivity.class));

                // Add fadein-to-fadeout animation to the activity
                CustomIntent.customType(HomeActivity.this,"fadein-to-fadeout");
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

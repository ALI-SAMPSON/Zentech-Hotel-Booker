package io.zentechhotelbooker.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.adapters.RecyclerViewAdapterManageUsers;
import io.zentechhotelbooker.models.Admin;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

public class ManageUsersActivity extends AppCompatActivity implements RecyclerViewAdapterManageUsers.onItemClickListener {

    Toolbar toolbar;

    TextView tv_no_users;

    // variable to store Decryption algorithm name
    String AES = "AES";

    // variable to store encrypted password
    String decryptedPassword;

    // string to get intentExtras
    String admin_id;

    Intent intent;

    Admin admin;

    //db reference ot the Admin model
    DatabaseReference adminRef;

    //db reference to rooms model
    DatabaseReference mDatabaseRef;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating a ValueEvent Listener.
    private ValueEventListener mDBListener;

    // Creating RecyclerViewAdapterManageRooms
    RecyclerViewAdapterManageUsers recyclerViewAdapterManageUsers;

    // Creating List of Rooms class.
    List<Users> usersList;

    // Creating a progressBar
    ProgressBar progressBar;

    ProgressBar progressBar1;

    FirebaseStorage mStorage;

    // material searchView
    MaterialSearchView searchView;


    public static String Payment_Path = "Payments";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // Creating an object of the toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.manage_users));
            //getSupportActionBar().setElevation(5.0f);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // textView to indicate there are no users added yet
        tv_no_users = findViewById(R.id.tv_no_users);

        admin = new Admin();

        adminRef = FirebaseDatabase.getInstance().getReference("Admin");

        // Setting up Firebase image upload folder path in databaseReference.
        // The path is already defined in MainActivity.
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");

        // Assign id to RecyclerView.
        recyclerView = findViewById(R.id.recyclerView);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        usersList = new ArrayList<>();

        // Creating an object of the RecyclerAdapter to populate the rooms
        recyclerViewAdapterManageUsers = new RecyclerViewAdapterManageUsers(ManageUsersActivity.this,usersList);

        recyclerView.setAdapter(recyclerViewAdapterManageUsers);

        recyclerViewAdapterManageUsers.setOnItemClickListener(ManageUsersActivity.this);

        // getting reference to progress bar.
        progressBar = findViewById(R.id.progressBar);
        // change the color of the progressBar
        progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorBlue), PorterDuff.Mode.MULTIPLY);

        // getting reference to progress bar1.
        progressBar1 =  findViewById(R.id.progressBar1);
        // change the color of the progressBar
        progressBar1.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);

        // creating an instance of a Firebase Storage
        mStorage = FirebaseStorage.getInstance();

        // Calling the method to display rooms in the firebase database
        loadUsers();

    }


    // Method to load rooms uploaded into database
    private void loadUsers(){

        // displays the progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Adding Add Value Event Listener to databaseReference.
        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // clears the list on data change
                usersList.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    // gets a snapshot of the rooms in the Rooms class
                    Users users = postSnapshot.getValue(Users.class);
                    // set visibility to gone
                    tv_no_users.setVisibility(View.GONE);
                    // sets visibility to Visible if rooms are added to DB
                    recyclerView.setVisibility(View.VISIBLE);
                    // get the unique key stored int the Rooms class
                    users.setKey(postSnapshot.getKey());
                    // adds the rooms to list of rooms(RoomList)
                    usersList.add(users);

                }


                // checks if no room is added yet
                if(!dataSnapshot.exists()){
                    // hides recyclerView and makes textView visible
                    recyclerView.setVisibility(View.GONE);
                    tv_no_users.setVisibility(View.VISIBLE);

                }

                // refreshes the recyclerview after data change
                recyclerViewAdapterManageUsers.notifyDataSetChanged();

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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search,menu);
        MenuItem item = menu.findItem(R.id.menu_search);

        // Material SearchView
        searchView = findViewById(R.id.search_view);
        searchView.setMenuItem(item);
        // searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setEllipsize(true);
        searchView.setSubmitOnClick(true);
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

                //else
                else{
                    searchForRoom("");
                }

                return true;
            }
        });

        return true;
    }

    // method to search for a room in the database
    private void searchForRoom(String s){

        Query query = mDatabaseRef.orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                usersList.clear();

                for(DataSnapshot roomSnapshot : dataSnapshot.getChildren()){

                    final Users users = roomSnapshot.getValue(Users.class);

                    //adds the rooms searched to the arrayList
                    usersList.add(users);

                }

                // refreshes the recyclerview after data change
                recyclerViewAdapterManageUsers.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // display message if error occurs
                Toast.makeText(ManageUsersActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:

                //goes back to the AdminDashboard
                startActivity(new Intent(ManageUsersActivity.this,AdminDashBoardActivity.class));

                // Adds a bottom-to-up animations to the activity
                CustomIntent.customType(ManageUsersActivity.this,"fadein-to-fadeout");

                // finishes the activity
                finish();

                break;
            case R.id.menu_welcome:
                Toast.makeText(ManageUsersActivity.this,
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

        //checks if searchView is opened
        if(searchView.isSearchOpen()){
            searchView.closeSearch();
        }
        else{
            //goes back to the AdminDashboard
            startActivity(new Intent(ManageUsersActivity.this,AdminDashBoardActivity.class));

            // Adds a bottom-to-up animations to the activity
            CustomIntent.customType(ManageUsersActivity.this,"up-to-bottom");

            // finishes the activity
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // removes the database Ref for displaying rooms anytime the activity is destroyed
        mDatabaseRef.removeEventListener(mDBListener);

    }

    // Methods implemented in the recyclerView for this activity
    @Override
    public void onItemClick(int position) {
        Toast.makeText(ManageUsersActivity.this," long click on a room to view options ",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCancelClick(int position) {
        //Toast.makeText(this," View Rooms is clicked at position : " + position,Toast.LENGTH_SHORT).show();
    }

    // Method to delete Rooms from the system
    @Override
    public void onDeleteClick(final int position) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView  = inflater.inflate(R.layout.custom_dialog,null);
        dialogBuilder.setView(dialogView);

        // reference to the EditText in the layout file (custom_dialog)
        final EditText editTextPassword = dialogView.findViewById(R.id.editTextPassword);

        dialogBuilder.setTitle("Delete User?");
        dialogBuilder.setMessage("Please enter your password");
        dialogBuilder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // getting text from EditText
                final String password = editTextPassword.getText().toString();

                adminRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                            Admin admin = snapshot.getValue(Admin.class);

                            assert admin != null;
                            // getting admin email and password and storing them in variables
                            String adminEmail = admin.getEmail();
                            String encryptedPassword = admin.getPassword();

                            // getting string email from sharePreference
                            SharedPreferences preferences =
                                    PreferenceManager.getDefaultSharedPreferences(ManageUsersActivity.this);
                            String email = preferences.getString("email","");

                            // decrypt password
                            try {
                                decryptedPassword = decryptPassword(encryptedPassword,email);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if(password.equals(decryptedPassword) && adminEmail.equals(email)){

                                /**
                                 * Code to delete selected room from database
                                 */

                                progressBar1.setVisibility(View.VISIBLE);

                                final Users selectedUser = usersList.get(position);
                                final String selectedKey = selectedUser.getKey();

                                // removing/deleting the image of the room together with the
                                // other details pertaining to a specific room
                                StorageReference roomImageRef = mStorage.getReferenceFromUrl(selectedUser.getImageUrl());

                                roomImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //remove value from FirebaseDatabase
                                        mDatabaseRef.child(selectedKey).removeValue();

                                        // dismiss the progress bar
                                        progressBar1.setVisibility(View.GONE);

                                        // File deleted successfully message
                                        Toast.makeText(ManageUsersActivity.this, " User deleted Successfully ", Toast.LENGTH_LONG).show();
                                    }
                                });

                            }
                            else{
                                progressBar1.setVisibility(View.GONE);
                                // display a message if there is an error
                                //Snackbar.make(relativeLayout,"Incorrect Email or password. Please Try Again",Snackbar.LENGTH_LONG).show();
                                Toast.makeText(ManageUsersActivity.this,"Incorrect password. Please Try Again!",Toast.LENGTH_LONG).show();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // display error message
                        Toast.makeText(ManageUsersActivity.this, databaseError.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });


            }

        });


        dialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss the DialogInterface
                dialogInterface.dismiss();
            }
        });

        AlertDialog alert = dialogBuilder.create();
        alert.show();

    }


    // method to encrypt password
    private String encryptPassword(String password, String email) throws Exception{
        SecretKeySpec key = generateKey(email);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE,key);
        byte[] encVal = c.doFinal(password.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        return encryptedValue;
    }

    // method to decrypt password
    private String decryptPassword(String password, String email) throws Exception {
        SecretKeySpec key  = generateKey(email);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE,key);
        byte[] decodedValue = Base64.decode(password,Base64.DEFAULT);
        byte[] decVal = c.doFinal(decodedValue);
        String decryptedValue = new String(decVal);
        return decryptedValue;

    }

    private SecretKeySpec generateKey(String password) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0 , bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec keySpec = new SecretKeySpec(key,"AES");
        return keySpec;
    }


    @Override
    public void finish() {
        super.finish();
        // Adds a bottom-to-up animations to the activity
        CustomIntent.customType(ManageUsersActivity.this,"fadein-to-fadeout");
    }
}

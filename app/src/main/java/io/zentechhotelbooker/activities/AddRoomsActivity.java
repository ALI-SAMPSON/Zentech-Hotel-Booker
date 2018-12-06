package io.zentechhotelbooker.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Admin;
import io.zentechhotelbooker.models.Payments;
import io.zentechhotelbooker.models.Rooms;
import maes.tech.intentanim.CustomIntent;

public class AddRoomsActivity extends AppCompatActivity {

    //class variables
    private TextView tv_room_exist;
    private EditText editTextRoomNumber;
    private EditText editTextPrice;

    // spinner view for roomType
    private AppCompatSpinner spinnerRoomType;
    private ArrayAdapter<CharSequence> arrayAdapterRoomType;

    // spinner view for breakfast
    private AppCompatSpinner spinnerBreakfast;
    private ArrayAdapter<CharSequence> arrayAdapterBreakfast;

    // spinner view for lunch
    private AppCompatSpinner spinnerLunch;
    private ArrayAdapter<CharSequence> arrayAdapterLunch;

    // spinner view for supper
    private AppCompatSpinner spinnerSupper;
    private ArrayAdapter<CharSequence> arrayAdapterSupper;

    private FirebaseDatabase roomdB;
    private DatabaseReference roomRef;

    private ImageView roomImage;
    private static final int image_request_code = 2;

    private static final int multiple_images_request_code = 3;

    // Folder path for Firebase Storage.
    String Storage_Path = "Hotel Room Images/";

    // Root Database Name for Firebase Database.
    public static String Database_Path = "Rooms";

    // Creating URI for room images
    Uri FilePathUri;

    String imageUrl;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;

    private Rooms rooms;

    private Payments payments;

    private DatabaseReference paymentRef;

    private DatabaseReference roomsRef;

    // Creating DataReference
    DatabaseReference reference;

    Admin admin;

    private FirebaseAuth mAuth;

    private FirebaseUser currentAdmin;

    private ProgressBar progressBar;

    private ProgressDialog progressDialog;

    private ScrollView scrollView;

    private LinearLayout spinnerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rooms);

        //checks if there is a toolbar, if yes it set the Home Button on it
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("ADD ROOMS");
            getSupportActionBar().setElevation(5.0f);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        admin = new Admin();

        mAuth = FirebaseAuth.getInstance();

        currentAdmin = mAuth.getCurrentUser();

        roomImage = findViewById(R.id.imageView);
        tv_room_exist = findViewById(R.id.tv_room_exist);
        editTextRoomNumber = findViewById(R.id.editTextRoomNumber);
        editTextPrice = findViewById(R.id.editTextPrice);

        // view referencing and adapter initialization
        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        arrayAdapterRoomType = ArrayAdapter.createFromResource(this,R.array.room_type,R.layout.spinner_item_add_room);
        arrayAdapterRoomType.setDropDownViewResource(R.layout.spinner_dropdown_item_add_room);
        spinnerRoomType.setAdapter(arrayAdapterRoomType);

        // view referencing and adapter initialization
        spinnerBreakfast = findViewById(R.id.spinnerBreakfast);
        arrayAdapterBreakfast = ArrayAdapter.createFromResource(this,R.array.food_type,R.layout.spinner_item_add_room);
        arrayAdapterBreakfast.setDropDownViewResource(R.layout.spinner_dropdown_item_add_room);
        spinnerBreakfast.setAdapter(arrayAdapterBreakfast);

        // view referencing and adapter initialization
        spinnerLunch = findViewById(R.id.spinnerLunch);
        arrayAdapterLunch = ArrayAdapter.createFromResource(this,R.array.food_type,R.layout.spinner_item_add_room);
        arrayAdapterLunch.setDropDownViewResource(R.layout.spinner_dropdown_item_add_room);
        spinnerLunch.setAdapter(arrayAdapterLunch);

        // view referencing and adapter initialization
        spinnerSupper = findViewById(R.id.spinnerSupper);
        arrayAdapterSupper = ArrayAdapter.createFromResource(this,R.array.food_type,R.layout.spinner_item_add_room);
        arrayAdapterSupper.setDropDownViewResource(R.layout.spinner_dropdown_item_add_room);
        spinnerSupper.setAdapter(arrayAdapterSupper);

        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        paymentRef = FirebaseDatabase.getInstance().getReference("Payments");

        roomsRef = FirebaseDatabase.getInstance().getReference("Rooms");

        rooms = new Rooms();

        payments = new Payments();

        progressBar = findViewById(R.id.progressBar);

        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Adding Room");
        progressDialog.setMessage("please wait...");

        scrollView = findViewById(R.id.scrollView);

        spinnerLayout = findViewById(R.id.spinnerLayout);

        //checkIfRoomNumberExist();

    }


    //method to handle the select image from gallery when user click the circularImageView
    public void onSelectImage(View view){

        // add animation to the view
        YoYo.with(Techniques.RubberBand).playOn(roomImage);

        // Creating intent.
        Intent imageIntent = new Intent();
        // Setting intent type as image to select image from phone storage.
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imageIntent,"Please Select Image"),image_request_code);
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(AddRoomsActivity.this, "fadein-to-fadeout");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == image_request_code && resultCode == RESULT_OK && data != null && data.getData() != null){

            FilePathUri = data.getData();

            try {
                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),FilePathUri);
                // Setting up bitmap selected image into ImageView.
                roomImage.setImageBitmap(bitmap);
            }
            catch (Exception e){
                Snackbar.make(scrollView,e.getMessage(),Snackbar.LENGTH_LONG).show();
            }

        }

    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri){

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    //onclick listener for the Add Button
    public void onAddRoomButtonClick(View view){

        //get input from the editText fields
        String room_number = editTextRoomNumber.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();

        if(room_number.isEmpty()){

            // Adds an animation to shake the view
            YoYo.with(Techniques.FadeInDown).playOn(roomImage);

            // Adds an animation to shake the view
            YoYo.with(Techniques.Shake).playOn(editTextPrice);

            // Adds an animation to shake the view
            YoYo.with(Techniques.Shake).playOn(editTextRoomNumber);

            editTextRoomNumber.setError(getString(R.string.error_text_room_number));
            editTextRoomNumber.requestFocus();
            return;
        }
        else if(price.isEmpty()){
            // Adds an animation to shake the view
            YoYo.with(Techniques.Shake).playOn(editTextPrice);
            // sets error message on view
            editTextPrice.setError(getString(R.string.error_empty_price));
            editTextPrice.requestFocus();
            return;
        }
        else{
            // Calling method to upload selected image onto Firebase storage.
            checkIfRoomNumberExist();
        }
    }

    // Method to check if room exist if not it adds it to the system
    public void checkIfRoomNumberExist(){

        final String roomNumber = editTextRoomNumber.getText().toString().trim();

        databaseReference.child(roomNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    // animation to shake view if room already exist
                    YoYo.with(Techniques.Shake).playOn(tv_room_exist);

                    //makes this textView visible
                    tv_room_exist.setVisibility(View.VISIBLE);

                }

                else{
                    // method call
                    confirmUsername();

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(scrollView, databaseError.getMessage(),Snackbar.LENGTH_LONG).show();
            }
        });

    }

    /**A block of code to display and alert Dialog
     ** for admin to confirm his or her password/username
     ** before adding Room
     */
    public void confirmUsername(){

        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView  = inflater.inflate(R.layout.custom_dialog,null);
        dialogBuilder.setView(dialogView);

        // reference to the EditText in the layout file (custom_dialog)
        final EditText editTextUsername = dialogView.findViewById(R.id.editTextUsername);

        dialogBuilder.setTitle("Add Room?");
        dialogBuilder.setMessage("Please enter your unique username");
        dialogBuilder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // getting text from EditText
                final String username = editTextUsername.getText().toString();

                // getting reference to the Admin Table/Node
                reference = FirebaseDatabase.getInstance().getReference("Admin");
                Query query = reference.orderByChild("username").equalTo(username);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // checks if username is correct
                        if(dataSnapshot.exists() && currentAdmin.getDisplayName().equals(username)){

                            // Method call to add Room to database
                            addRoomDetailsToDatabase();

                        }
                        else{
                            Toast.makeText(AddRoomsActivity.this,
                                    getString(R.string.incorrect_username),Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(AddRoomsActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
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

        android.app.AlertDialog alert = dialogBuilder.create();
        alert.show();

    }

    public void addRoomDetailsToDatabase(){

        //makes this textView visible
        tv_room_exist.setVisibility(View.GONE);

       // Checking whether FilePathUri Is empty or not.
        if(FilePathUri != null){

            // display progressBar to show that image is uploading
            progressDialog.show();

            final StorageReference mStorageRef = FirebaseStorage.getInstance()
                    .getReference(Storage_Path + System.currentTimeMillis() +
                            "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            mStorageRef.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            imageUrl = taskSnapshot.getDownloadUrl().toString();
                            rooms.setRoomImage_url(imageUrl);

                            //get input from the editText views and spinner views
                            String room_number = editTextRoomNumber.getText().toString().trim();
                            String room_type   = spinnerRoomType.getSelectedItem().toString().trim();
                            String breakfast = spinnerBreakfast.getSelectedItem().toString().trim();
                            String lunch = spinnerLunch.getSelectedItem().toString().trim();
                            String supper = spinnerSupper.getSelectedItem().toString().trim();
                            String room_price = editTextPrice.getText().toString().trim();

                            // setting fields to the object og the class Rooms
                            //rooms.setRoomImage_url(imageUrl);
                            rooms.setRoomNumber(room_number);
                            rooms.setRoomType(room_type);
                            rooms.setBreakfastServed(breakfast);
                            rooms.setLunchServed(lunch);
                            rooms.setSupperServed(supper);
                            rooms.setRoomPrice(room_price);
                            rooms.setSearch(room_type.toLowerCase());

                            // Getting image unique key of the node.
                            final String roomKey = databaseReference.push().getKey();

                            // Adding image upload id s child element into databaseReference.
                            databaseReference .child(rooms.getRoomNumber()).setValue(rooms);

                            // displays the progressDialog
                            progressDialog.dismiss();

                            // Showing success message.
                            Snackbar.make(scrollView,getString(R.string.room_added_successful),Snackbar.LENGTH_LONG).show();

                            //makes this textView visible
                            tv_room_exist.setVisibility(View.GONE);

                            // Method Call
                            clearTextFields();

                        }
                    })
                    // If something goes wrong
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception error message.
                            Snackbar.make(scrollView,e.getMessage(),Snackbar.LENGTH_LONG).show();

                        }
                    });

        }

        else {
            // Showing Alert message.
            Snackbar.make(scrollView, getString(R.string.error_adding_room),Snackbar.LENGTH_LONG).show();
        }

    }


    // Body of Code to check if Room already exist in database
    public void checkIfRoomNumberExistOnLaunch(){

        final String roomNumber = editTextRoomNumber.getText().toString().trim();

        databaseReference.child(roomNumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    // animation to shake view if room already exist
                    YoYo.with(Techniques.Shake).playOn(tv_room_exist);

                    //makes this textView visible
                    tv_room_exist.setVisibility(View.VISIBLE);

                    // display a SnackBar with error message
                    Snackbar.make(scrollView,getString(R.string.room_number_exist),Snackbar.LENGTH_SHORT).show();

                }
                else{

                    //makes this textView visible
                    tv_room_exist.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // display a SnackBar with error message
                Snackbar.make(scrollView,databaseError.getMessage(),Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_rooms,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                //send user back to the adminDashboard
                startActivity(new Intent(AddRoomsActivity.this,AdminDashBoardActivity.class));

                // Adds a fadein-fadeout animations to the activity
                CustomIntent.customType(AddRoomsActivity.this, "fadein-to-fadeout");

                // finish activity
                finish();

                break;
            case R.id.menu_welcome:
                Toast.makeText(AddRoomsActivity.this,
                        getString(R.string.welcome_text),
                        Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Method for clearing all textfields after Login Button is Clicked
    public void clearTextFields() {
        //Clears all text from the EditText
        editTextPrice.setText(null);

    }

    public void openContactUs(View view) {

        // starts the about us activity
        startActivity(new Intent(AddRoomsActivity.this,AboutUsAdminActivity.class));

        // Adds an up-to-bottom animation to the activity
        CustomIntent.customType(AddRoomsActivity.this,"up-to-bottom");

        // finishes the current activity
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //send user back to the adminDashboard
        startActivity(new Intent(AddRoomsActivity.this,AdminDashBoardActivity.class));

        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(AddRoomsActivity.this, "fadein-to-fadeout");

        // finish activity
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(AddRoomsActivity.this, "fadein-to-fadeout");
    }

    // onclick Listener for continue button
    public void onContinueButtonClick(View view) {

        //get input from the editText fields
        String room_number = editTextRoomNumber.getText().toString().trim();
        String room_price = editTextPrice.getText().toString().trim();
        String room_type   = spinnerRoomType.getSelectedItem().toString().trim();

        // starts the about us activity
        Intent intentContinue = new Intent(AddRoomsActivity.this,AddRoomImagesActivity.class);
        intentContinue.putExtra("room_number", room_number);
        intentContinue.putExtra("room_price",room_price);
        intentContinue.putExtra("room_type",room_type);
        startActivity(intentContinue);

        // Adds an up-to-bottom animation to the activity
        CustomIntent.customType(AddRoomsActivity.this,"right-to-left");
    }
}

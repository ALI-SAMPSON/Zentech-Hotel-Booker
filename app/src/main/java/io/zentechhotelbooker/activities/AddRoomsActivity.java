package io.zentechhotelbooker.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Rooms;

public class AddRoomsActivity extends AppCompatActivity {

    //class variables
    private EditText editTextRoomNumber;
    private EditText editTextPrice;

    private FirebaseDatabase roomdB;
    private DatabaseReference roomRef;

    private CircleImageView circleImageView;
    private int image_request_code = 2;

    // Folder path for Firebase Storage.
    String Storage_Path = "Hotel Room Images/";

    // Root Database Name for Firebase Database.
    public static String Database_Path = "Rooms";

    // Creating URI
    Uri FilePathUri;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;

    private Rooms rooms;

    private FirebaseAuth mAuth;

    private ProgressBar progressBar;

    private NestedScrollView nestedScrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rooms);

        //checks if there is a toolbar, if yes it set the Home Button on it
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("ADD ROOMS");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        circleImageView = findViewById(R.id.circularImageView);
        editTextRoomNumber = findViewById(R.id.editTextRoomNumber);
        editTextPrice = findViewById(R.id.editTextPrice);

        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        rooms = new Rooms();

        progressBar = findViewById(R.id.progressBar);

        nestedScrollView = findViewById(R.id.nestedScrollView);

        mAuth = FirebaseAuth.getInstance();

    }

    //method to handle the select image from gallery when user click the image Button
    public void onSelectImage(View view){
        // Creating intent.
        Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Setting intent type as image to select image from phone storage.
        imageIntent.setType("image/*");
        imageIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(imageIntent,"Please Select Image"),image_request_code);
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
                circleImageView.setImageBitmap(bitmap);
            }
            catch (Exception e){
                Snackbar.make(nestedScrollView,e.getMessage(),Snackbar.LENGTH_LONG).show();
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

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage(){

        // Checking whether FilePathUri Is empty or not.
        if(FilePathUri != null){

            // display progressBar to show that image is uploading
            progressBar.setVisibility(View.VISIBLE);

            StorageReference storageReference2nd = storageReference
                    .child(Storage_Path + System.currentTimeMillis() +
                            "." + GetFileExtension(FilePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            //get input from the editText fields
                            String room_number = editTextRoomNumber.getText().toString().trim();
                            String price = editTextPrice.getText().toString().trim();
                            String imageDownloadUrl = taskSnapshot.getDownloadUrl().toString();

                            // displays the progressBar
                            progressBar.setVisibility(View.GONE);

                            // setting fields to the object og the class Rooms
                            rooms.setRoom_number(room_number);
                            rooms.setPrice(price);
                            rooms.setRoom_image(imageDownloadUrl);

                            // Getting image upload ID.
                            String ImageUploadID = databaseReference.push().getKey();

                            // Adding image upload id s child element into databaseReference.
                            databaseReference .child(ImageUploadID).setValue(rooms);

                            // Showing success message.
                            Snackbar.make(nestedScrollView,getString(R.string.room_added_successful),Snackbar.LENGTH_LONG).show();

                        }
                    })
                    // If something goes wrong
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Hiding the progressBar.
                            progressBar.setVisibility(View.GONE);

                            // Showing exception erro message.
                            Snackbar.make(nestedScrollView,e.getMessage(),Snackbar.LENGTH_LONG).show();

                        }
                    })
            // On progress change upload time.
            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    // Setting progressDialog Title.
                    progressBar.setVisibility(View.VISIBLE);

                }
            });


        }

        else {
            // Showing Alert message.
            Snackbar.make(nestedScrollView, "Please Select Image or add Room Number or Price",Snackbar.LENGTH_LONG).show();
        }

    }

    //onclick listener for the Add Button
    public void onAddRoomButtonClick(View view){

        //get input from the editText fields
        String room_number = editTextRoomNumber.getText().toString().trim();
        String price = editTextPrice.getText().toString().trim();

        if(room_number.isEmpty()){
            editTextRoomNumber.setError(getString(R.string.error_empty_field));
            editTextRoomNumber.requestFocus();
            return;
        }
        else if(price.isEmpty()){
            editTextPrice.setError(getString(R.string.error_empty_field));
            editTextPrice.requestFocus();
            return;
        }
        else if(room_number.isEmpty() && price.isEmpty()){
            Snackbar.make(nestedScrollView,"Both fields cannot be left blank",Snackbar.LENGTH_LONG).show();
            return;
            //Toast.makeText(AddRoomsActivity.this,"Both fields cannot be left blank",Toast.LENGTH_LONG).show();
        }
        else{
            // Calling method to upload selected image on Firebase storage.
            UploadImageFileToFirebaseStorage();
            //addRoom();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //send user back to the adminDashboard
                AddRoomsActivity.this.finish();
                startActivity(new Intent(AddRoomsActivity.this,AdminDashBoardActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Method for clearing all textfields after Login Button is Clicked
    public void clearTextFields() {
        //Clears all text from the EditText
        editTextRoomNumber.setText(null);
        editTextPrice.setText(null);

    }

}

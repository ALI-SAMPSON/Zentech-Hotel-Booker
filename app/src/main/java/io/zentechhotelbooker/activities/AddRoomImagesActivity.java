package io.zentechhotelbooker.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.adapters.RecyclerViewAdapterRoomImages;
import io.zentechhotelbooker.models.Rooms;
import maes.tech.intentanim.CustomIntent;

public class AddRoomImagesActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;

    private static final int REQUEST_CODE = 1;

    StorageReference mStorage;

    // Folder path for Firebase Storage.
    String Storage_Path = "Multiple Images of Rooms";

    Uri fileUri;

    String roomImageUrl;

    String filename;

    Rooms rooms;

    List<String> fileNameList;

    List<Rooms> roomsList;

    RecyclerView recyclerView;

    RecyclerViewAdapterRoomImages recyclerViewAdapter;

    ProgressDialog progressDialog;

    // string to getIntent from AddRoomActivity
    String room_number;
    String room_price;
    String room_type;

    // Root Database Name for Firebase Database.
    public static String Database_Path = "Rooms";

    DatabaseReference roomRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room_images);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.text_add_room_images));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        constraintLayout = findViewById(R.id.constraintLayout);

        mStorage = FirebaseStorage.getInstance().getReference();

        roomRef = FirebaseDatabase.getInstance().getReference(Database_Path);

        fileNameList = new ArrayList<>();

        roomsList = new ArrayList<>();

        rooms = new Rooms();

        recyclerView = findViewById(R.id.recyclerView);

        recyclerViewAdapter = new RecyclerViewAdapterRoomImages(this,fileNameList);

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(recyclerViewAdapter);

        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
        progressDialog.setMessage(getString(R.string.text_please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Intent intent = getIntent();
        room_number = intent.getStringExtra("room_number");
        room_price = intent.getStringExtra("room_price");
        room_type = intent.getStringExtra("room_type");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                // starts the about us activity
                startActivity(new Intent(AddRoomImagesActivity.this,AddRoomsActivity.class));

                // Adds an up-to-bottom animation to the activity
                CustomIntent.customType(AddRoomImagesActivity.this,"fade-in-fadeout");

                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void selectMultipleImages(View view) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Images"),REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){

            if(data.getClipData() != null){

                int totalSelectedImages = data.getClipData().getItemCount();

                for(int i = 0; i <totalSelectedImages; i++){

                    fileUri = data.getClipData().getItemAt(i).getUri();

                    filename = getFileName(fileUri);

                    fileNameList.add(filename);

                    //String fileUrl = fileUri.toString();

                    // recyclerView Adapter is notified if changes happen
                    recyclerViewAdapter.notifyDataSetChanged();
                }

            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri){

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    // method to get filename of the multiple images uploaded
    public String getFileName(Uri uri){
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            try{
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
        }
        if(result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1){
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void addRoomButtonClick(View view) {

        // displays the progressDialog
        progressDialog.show();

        final StorageReference fileUpload = mStorage
                .child(Storage_Path).child(room_number)
                .child(filename);

        fileUpload.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                fileUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        roomImageUrl = downloadUrl.toString();
                        //rooms.setRoom_image(roomImageUrl);
                    }
                });

                // setting fields to the object og the class Rooms
                rooms.setRoom_image(roomImageUrl);
                rooms.setRoom_number(room_number);
                rooms.setRoom_type(room_type);
                rooms.setPrice(room_price);

                // Getting image upload ID.
                String ImageUploadID = roomRef.push().getKey();

                // Adding image upload id s child element into databaseReference.
                roomRef.child(ImageUploadID).setValue(rooms);

                // dismisses the progressDialog
                progressDialog.dismiss();

                // Showing success message.
                Snackbar.make(constraintLayout,getString(R.string.room_added_successful),Snackbar.LENGTH_LONG).show();

            }

        });

    }
}

package io.zentechhotelbooker.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Rooms;

public class DeleteRoomsActivity extends AppCompatActivity {

    //instance variables of the EditText
    private EditText editTextRoomNumber;
    private EditText editTextPrice;

    private ImageView imageView;

    Rooms rooms;

    TextView key1;

    DatabaseReference roomRef;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_rooms);

        //getting reference to the Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("DELETE ROOMS");

        //checks if there is an actionBar
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        editTextRoomNumber = findViewById(R.id.editTextRoomNumber);
        editTextPrice = findViewById(R.id.editTextPrice);
        imageView = findViewById(R.id.imageView);

        rooms = new Rooms();

        //string to store the key value, that is, the room number
        String key = getIntent().getExtras().get("key").toString();

        //getting reference to the TextView and set its text to key
        //key1 = findViewById(R.id.key);
        //key1.setText(key);

        editTextRoomNumber.setText(getIntent().getStringExtra("room_number"));
        editTextPrice.setText(getIntent().getStringExtra("price"));
        Picasso.with(DeleteRoomsActivity.this).load(rooms.getRoom_image()).into(imageView);

        roomRef = FirebaseDatabase.getInstance().getReference().child("Rooms").child(key);

        onEditTextClick();//call to the method

    }


    //sets error message on the editTextViews when user clicks on to edit
    public void onEditTextClick(){

        //error strings
        final String error_room_number = "Room number field cannot be edited";
        final String error_price = "Price field cannot be edited";

        editTextRoomNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextRoomNumber.setError(error_room_number);
            }
        });

        editTextPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextPrice.setError(error_price);
            }
        });
    }

    //onClickListener for the delete room button
    public void onDeleteRoomButtonClick(View view){

        progressDialog = ProgressDialog.show(DeleteRoomsActivity.this,"",null,true,true);
        progressDialog.setMessage("Please wait...");

        roomRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            timer.cancel();
                        }
                    },5000);
                    Toast.makeText(DeleteRoomsActivity.this,"Room deleted successfully",Toast.LENGTH_LONG).show();
                    DeleteRoomsActivity.this.finish();
                    //Snackbar.make(,"Room deleted successfully",Snackbar.LENGTH_LONG).show();
                }
                else{
                    final Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            timer.cancel();
                        }
                    },5000);
                    Toast.makeText(DeleteRoomsActivity.this,"Room could not be deleted...Please try again!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_admin,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.about_us:
                //starts the aboutUs activity for admin
                startActivity(new Intent(DeleteRoomsActivity.this,AboutUsAdminActivity.class));
                break;
            case android.R.id.home:
                //send user back to the adminDashboard
                startActivity(new Intent(DeleteRoomsActivity.this,ViewAddedRoomsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

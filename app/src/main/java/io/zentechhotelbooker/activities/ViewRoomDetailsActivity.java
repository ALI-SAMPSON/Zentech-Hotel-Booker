package io.zentechhotelbooker.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.fragments.AboutHotelFragment;
import io.zentechhotelbooker.fragments.ImportantInformationFragment;
import io.zentechhotelbooker.fragments.MoreDetailsFragment;
import io.zentechhotelbooker.models.Rooms;
import maes.tech.intentanim.CustomIntent;

public class ViewRoomDetailsActivity extends AppCompatActivity {

    Rooms rooms;

    ImageView room_image;
    TextView tv_room_image_url;
    TextView tv_room_type;
    TextView tv_breakfast;
    TextView tv_lunch;
    TextView tv_supper;
    TextView tv_room_price;

    String user_name;
    String user_image;
    String room_image_url;
    String room_number;
    String room_type;
    String room_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room_details);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.title_room_details));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // getting referencer to the TextView in the Layout resource file
        room_image = findViewById(R.id.room_image);
        tv_room_image_url = findViewById(R.id.tv_room_image_url);
        tv_room_type = findViewById(R.id.tv_room_type);
        tv_breakfast = findViewById(R.id. tv_breakfast);
        tv_lunch = findViewById(R.id.tv_lunch);
        tv_supper = findViewById(R.id.tv_supper);
        tv_room_price = findViewById(R.id.tv_room_price);

        rooms = new Rooms();

        // method call
        GetPassingStringIntents();

    }

    // get String intents
    private void GetPassingStringIntents(){

        // getting StringExtras from any of the cardViews
        // when user clicks on it
        Intent intent = getIntent();

        user_name = intent.getStringExtra("user_name");
        user_image = intent.getStringExtra("user_image");
        room_image_url = intent.getStringExtra("room_image_url");
        room_number = intent.getStringExtra("room_number");
        room_type = intent.getStringExtra("room_type");
        room_price = intent.getStringExtra("room_price");

        // Glide Library to load respective Room Image into imageView
        Glide.with(this)
                .load(room_image_url)
                .into(room_image);

        tv_room_image_url.setText(room_image_url);
        tv_room_type.setText(room_type);
        tv_breakfast.setText(getString(R.string.text_breakfast_included));
        tv_lunch.setText(getString(R.string.text_lunch_included));
        tv_supper.setText(getString(R.string.text_supper_excluded));
        tv_room_price.setText(room_price);

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Glide Library to load respective Room Image into imageView
        Glide.with(this)
                .load(room_image_url)
                .into(room_image);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Glide Library to load respective Room Image into imageView
        Glide.with(this)
                .load(tv_room_image_url.getText().toString())
                .into(room_image);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Glide Library to load respective Room Image into imageView
        Glide.with(this)
                .load(tv_room_image_url.getText().toString())
                .into(room_image);
    }

    public void moreDetailsBtn(View view) {
        // sends user to the AboutHotelFragment
        startActivity(new Intent(ViewRoomDetailsActivity.this, MoreDetailsFragment.class));
        CustomIntent.customType(ViewRoomDetailsActivity.this,"left-to-right");
    }

    public void bookRoomBtn(View view) {

        // Creates an Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewRoomDetailsActivity.this,
                android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Book this Room");
        builder.setMessage("Are you sure you want to book this room and make payment for it?");
        builder.setCancelable(false);

        // set the Positive button for alert dialog
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Creates new Intent
                Intent intentBook = new Intent(ViewRoomDetailsActivity.this, MakePaymentActivity.class);
                intentBook.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // passing data to the payment activity
                intentBook.putExtra("user_name",user_name);
                intentBook.putExtra("room_number", room_number);
                intentBook.putExtra("room_type", room_type);
                intentBook.putExtra("room_price", "GH¢ " + room_price);
                intentBook.putExtra("room_image_url",room_image_url);
                intentBook.putExtra("user_image", user_image);
                // starting the activity
                startActivity(intentBook);
                // Add a custom animation to the activity
                CustomIntent.customType(ViewRoomDetailsActivity.this, "left-to-right");

            }
        });

        // set the Positive button for alert dialog
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss the dialogue interface
                dialogInterface.dismiss();
            }
        });

        //
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void seeMoreRoomsBtn(View view) {
        // sends user to the HomeActivity
        startActivity(new Intent(ViewRoomDetailsActivity.this, HomeActivity.class));
        CustomIntent.customType(ViewRoomDetailsActivity.this,"right-to-left");
    }

    public void aboutHotelBtn(View view) {
        // sends user to the AboutHotelFragment
        startActivity(new Intent(ViewRoomDetailsActivity.this, AboutHotelFragment.class));
        CustomIntent.customType(ViewRoomDetailsActivity.this,"left-to-right");
    }

    public void importantInfoBtn(View view) {
        // sends user to the ImportantInformationFragment
        startActivity(new Intent(ViewRoomDetailsActivity.this, ImportantInformationFragment.class));
        CustomIntent.customType(ViewRoomDetailsActivity.this,"left-to-right");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ViewRoomDetailsActivity.this,HomeActivity.class));

        CustomIntent.customType(ViewRoomDetailsActivity.this,"right-to-left");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                startActivity(new Intent(ViewRoomDetailsActivity.this,HomeActivity.class));

                CustomIntent.customType(ViewRoomDetailsActivity.this,"right-to-left");

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

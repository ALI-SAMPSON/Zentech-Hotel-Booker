package io.zentechhotelbooker.activities;

import android.app.AlertDialog;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.fragments.AboutHotelFragment;
import io.zentechhotelbooker.fragments.ImportantInformationFragment;
import io.zentechhotelbooker.fragments.MoreDetailsFragment;
import io.zentechhotelbooker.models.Rooms;
import maes.tech.intentanim.CustomIntent;

public class ViewRoomDetailsActivity extends AppCompatActivity{

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
    String breakfast_food;
    String lunch_food;
    String supper_food;

    String saved_user_name;
    String saved_image_url;
    String saved_room_type;
    String saved_room_price;
    String saved_breadfast_food;
    String saved_lunch_food;
    String saved_supper_food;

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

        // getting StringExtras from any of the cardViews
        // when user clicks on it in the HomeActivity
        Intent intent = getIntent();
        user_name = intent.getStringExtra("user_name");
        user_image = intent.getStringExtra("user_image");
        room_image_url = intent.getStringExtra("room_image_url");
        room_number = intent.getStringExtra("room_number");
        room_type = intent.getStringExtra("room_type");
        room_price = intent.getStringExtra("room_price");
        breakfast_food = intent.getStringExtra("breakfast");
        lunch_food = intent.getStringExtra("lunch");
        supper_food = intent.getStringExtra("supper");

        rooms = new Rooms();

        // get the savedInstanceState and sets them to the respective views
        /*if(savedInstanceState != null && savedInstanceState.getString("saved_room_image_url") != null){
            saved_image_url = savedInstanceState.getString("saved_room_image_url");
            // Glide Library to load imageUrl from the HomeActivity
            Glide.with(this)
                    .load(saved_image_url)
                    .into(room_image);
        }
        if(savedInstanceState != null && savedInstanceState.getString("saved_room_type") != null){
            saved_room_type = savedInstanceState.getString("saved_room_type");
            tv_room_type.setText(saved_room_type);
        }
        if(savedInstanceState != null && savedInstanceState.getString("saved_room_price") != null){
            saved_room_price = savedInstanceState.getString("saved_room_price");
            tv_room_type.setText(saved_room_price);
        }
        if(savedInstanceState != null && savedInstanceState.getString("saved_breadfast_food") != null){
            saved_breadfast_food = savedInstanceState.getString("saved_breadfast_food");
            tv_room_type.setText(saved_breadfast_food);
        }
        if(savedInstanceState != null && savedInstanceState.getString("saved_lunch_food") != null){
            saved_lunch_food = savedInstanceState.getString("saved_lunch_food");
            tv_room_type.setText(saved_lunch_food);
        }
        if(savedInstanceState != null && savedInstanceState.getString("saved_supper_food") != null){
            saved_supper_food = savedInstanceState.getString("saved_supper_food");
            tv_room_type.setText(saved_supper_food);
        }
        */
        // method call
        settingValues();

    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("saved_room_image_url",room_image_url);
        savedInstanceState.putString("saved_room_type",room_type);
        savedInstanceState.putString("saved_room_price",room_price);
        savedInstanceState.putString("saved_breadfast_food",breakfast_food);
        savedInstanceState.putString("saved_lunch_food",lunch_food);
        savedInstanceState.putString("saved_supper_food",supper_food);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the savedInstanceState and sets them to the respective views
            saved_image_url = savedInstanceState.getString("saved_room_image_url");
            // Glide Library to load imageUrl from the HomeActivity
            Glide.with(this).load(saved_image_url).into(room_image);

        // restores the text and sets it to the textViews
            saved_room_type = savedInstanceState.getString("saved_room_type");
            tv_room_type.setText(saved_room_type);

            saved_room_price = savedInstanceState.getString("saved_room_price");
            tv_room_type.setText(saved_room_price);

            saved_breadfast_food = savedInstanceState.getString("saved_breadfast_food");
            tv_breakfast.setText(saved_breadfast_food);

            saved_lunch_food = savedInstanceState.getString("saved_lunch_food");
            tv_lunch.setText(lunch_food);

            saved_supper_food = savedInstanceState.getString("saved_supper_food");
            tv_supper.setText(saved_supper_food);

    }

    // get String intents
    private void settingValues(){

        // Glide Library to load imageUrl from the HomeActivity
        Glide.with(this)
                .load(room_image_url)
                .into(room_image);

        tv_room_image_url.setText(room_image_url);
        tv_room_type.setText(room_type);
        tv_breakfast.setText(breakfast_food);
        tv_lunch.setText(lunch_food);
        tv_supper.setText(supper_food);
        tv_room_price.setText(room_price);

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
                intentBook.putExtra("room_price",  room_price);
                intentBook.putExtra("breakfast_food", breakfast_food);
                intentBook.putExtra("lunch_food", lunch_food);
                intentBook.putExtra("supper_food",  supper_food);
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
        Intent intentAbout = new Intent(ViewRoomDetailsActivity.this,AboutHotelFragment.class);
        startActivity(intentAbout);
        CustomIntent.customType(ViewRoomDetailsActivity.this,"left-to-right");
    }

    public void importantInfoBtn(View view) {
        // sends user to the ImportantInformationFragment
        // sends user to the AboutHotelFragment
        Intent intentAbout = new Intent(ViewRoomDetailsActivity.this,AboutHotelFragment.class);
        startActivity(intentAbout);
        CustomIntent.customType(ViewRoomDetailsActivity.this,"left-to-right");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ViewRoomDetailsActivity.this,HomeActivity.class));

        CustomIntent.customType(ViewRoomDetailsActivity.this,"right-to-left");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(ViewRoomDetailsActivity.this,HomeActivity.class));
                CustomIntent.customType(ViewRoomDetailsActivity.this,"right-to-left");
                break;

            case R.id.menu_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.setAction(android.content.Intent.ACTION_SEND);
                String sharingBody = "https://www.zentechgh.com/";
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Accra City Hotel");
                sharingIntent.putExtra(Intent.EXTRA_TEXT,sharingBody);
                startActivity(Intent.createChooser(sharingIntent,"Share with"));
                break;

                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }
}
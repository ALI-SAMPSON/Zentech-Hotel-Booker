package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.aakira.expandablelayout.ExpandableLayout;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Rooms;
import maes.tech.intentanim.CustomIntent;

public class ViewRoomDetailsAdminActivity extends AppCompatActivity {

    // class varaiables
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

    // Expandable layouts
    ExpandableLayout expandableLayout1;
    ExpandableLayout expandableLayout2;
    ExpandableLayout expandableLayout3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room_details_admin);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.title_room_details));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        rooms = new Rooms();

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

        // method call
        settingValues();

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

        expandableLayout1 =  findViewById(R.id.expandableLayout1);
        expandableLayout1.toggle(); // toggle expand or close
    }

    public void aboutHotelBtn(View view) {
        expandableLayout2 = findViewById(R.id.expandableLayout2);
        expandableLayout2.toggle(); // toggle expand or close
    }

    public void importantInfoBtn(View view) {
        expandableLayout3 = findViewById(R.id.expandableLayout3);
        expandableLayout3.toggle(); // toggle expand or close
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // navigates to the HomeActivity
        startActivity(new Intent(ViewRoomDetailsAdminActivity.this,DeleteRoomsActivity.class));
        // add a custom animation to the intent
        CustomIntent.customType(ViewRoomDetailsAdminActivity.this,"right-to-left");
        // finish the activity
        finish();
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
                // navigates to the HomeActivity
                startActivity(new Intent(ViewRoomDetailsAdminActivity.this,DeleteRoomsActivity.class));
                // add a custom animation to the intent
                CustomIntent.customType(ViewRoomDetailsAdminActivity.this,"right-to-left");
                // finish the activity
                finish();
                break;

            case R.id.menu_welcome:
                Toast.makeText(ViewRoomDetailsAdminActivity.this,
                        getString(R.string.welcome_text),
                        Toast.LENGTH_LONG).show();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

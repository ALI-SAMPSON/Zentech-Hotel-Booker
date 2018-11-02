package io.zentechhotelbooker.fragments;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.activities.ViewRoomDetailsActivity;
import maes.tech.intentanim.CustomIntent;

public class AboutHotelFragment extends AppCompatActivity{

    TextView tv_hotel_name;
    TextView tv_about_hotel;

    // strings to get intent Extras from the previous activity
    String room_image_url_1;
    String room_type_1;
    String room_price_1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about_hotel);

         if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.title_about_hotel));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // getting reference to the TextViews
        tv_hotel_name = findViewById(R.id.tv_hotel_name);
        tv_about_hotel = findViewById(R.id.tv_about_hotel);

        // gets the setText property and sets the corresponding the text to it
        tv_hotel_name.setText(getString(R.string.hotel_name));
        tv_about_hotel.setText(getString(R.string.about_hotel));

        // getting the image url of the room
        Intent intent = getIntent();
        room_image_url_1 = intent.getStringExtra("room_image_url_1");
        room_type_1 = intent.getStringExtra("room_type_1");
        room_price_1 = intent.getStringExtra("room_price_1");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // opens activity on back button press and passes the StringExtra of the Room Image
                Intent intentHome = new Intent(AboutHotelFragment.this,ViewRoomDetailsActivity.class);
                intentHome.putExtra("room_image_url_1",room_image_url_1);
                intentHome.putExtra("room_type_1",room_type_1);
                intentHome.putExtra("room_price_1",room_price_1);
                startActivity(intentHome);
                CustomIntent.customType(AboutHotelFragment.this, "right-to-left");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // opens activity on back button press and passes the StringExtra of the Room Image
        Intent intentHome = new Intent(AboutHotelFragment.this,ViewRoomDetailsActivity.class);
        intentHome.putExtra("room_image_url_1",room_image_url_1);
        intentHome.putExtra("room_type_1",room_type_1);
        intentHome.putExtra("room_price_1",room_price_1);
        startActivity(intentHome);
        CustomIntent.customType(AboutHotelFragment.this, "right-to-left");
    }

}

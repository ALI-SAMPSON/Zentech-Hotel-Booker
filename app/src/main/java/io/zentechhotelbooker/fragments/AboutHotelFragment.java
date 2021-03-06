package io.zentechhotelbooker.fragments;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.activities.ViewRoomDetailsUserActivity;
import maes.tech.intentanim.CustomIntent;

public class AboutHotelFragment extends AppCompatActivity{

    TextView tv_hotel_name;
    TextView tv_about_hotel;


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


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // opens activity on back button press and passes the StringExtra of the Room Image
                Intent intentHome = new Intent(AboutHotelFragment.this,ViewRoomDetailsUserActivity.class);
                startActivity(intentHome);
                CustomIntent.customType(AboutHotelFragment.this, "right-to-left");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // opens activity on back button press and passes the StringExtra of the Room Image
        Intent intentHome = new Intent(AboutHotelFragment.this,ViewRoomDetailsUserActivity.class);
        startActivity(intentHome);
        CustomIntent.customType(AboutHotelFragment.this, "right-to-left");
    }

}

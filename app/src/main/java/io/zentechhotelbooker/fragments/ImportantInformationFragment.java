package io.zentechhotelbooker.fragments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.activities.ViewRoomDetailsActivity;
import maes.tech.intentanim.CustomIntent;

public class ImportantInformationFragment extends AppCompatActivity {

    // strings to get intent Extras from the previous activity
    String room_image_url_2;
    String room_type_2;
    String room_price_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_important_information);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.text_important_info));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // getting the image url of the room
        Intent intent = getIntent();
        room_image_url_2 = intent.getStringExtra("room_image_url_2");
        room_type_2 = intent.getStringExtra("room_type_2");
        room_price_2 = intent.getStringExtra("room_price_2");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // send user back to this activity
        // opens activity on back button press and passes the StringExtra of the Room Image
        Intent intentHome = new Intent(ImportantInformationFragment.this,ViewRoomDetailsActivity.class);
        intentHome.putExtra("room_image_url_2",room_image_url_2);
        intentHome.putExtra("room_type_2",room_type_2);
        intentHome.putExtra("room_price_2",room_price_2);
        startActivity(intentHome);
        CustomIntent.customType(ImportantInformationFragment.this,"right-to-left");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // send user back to this activity
                // opens activity on back button press and passes the StringExtra of the Room Image
                Intent intentHome = new Intent(ImportantInformationFragment.this,ViewRoomDetailsActivity.class);
                intentHome.putExtra("room_image_url_2",room_image_url_2);
                intentHome.putExtra("room_type_2",room_type_2);
                intentHome.putExtra("room_price_2",room_price_2);
                startActivity(intentHome);
                CustomIntent.customType(ImportantInformationFragment.this,"right-to-left");
                break;
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

}

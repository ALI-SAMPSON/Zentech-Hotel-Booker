package io.zentechhotelbooker.fragments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.activities.ViewRoomDetailsActivity;
import maes.tech.intentanim.CustomIntent;

public class ImportantInformationFragment extends AppCompatActivity {


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

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // send user back to this activity
        // opens activity on back button press and passes the StringExtra of the Room Image
        Intent intentHome = new Intent(ImportantInformationFragment.this,ViewRoomDetailsActivity.class);
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
                startActivity(intentHome);
                CustomIntent.customType(ImportantInformationFragment.this,"right-to-left");
                break;
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

}

package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;

import io.fabric.sdk.android.services.common.DeviceIdentifierProvider;
import io.zentechhotelbooker.R;
import maes.tech.intentanim.CustomIntent;

public class DisplayMoreImagesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_more_images);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.text_more_images));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                // goes back ot the home Page
                startActivity(new Intent(DisplayMoreImagesActivity.this,HomeActivity.class));
                // Add fadein-to-fadeout animation to the activity
                CustomIntent.customType(DisplayMoreImagesActivity.this,"fadein-to-fadeout");
                break;
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }
}

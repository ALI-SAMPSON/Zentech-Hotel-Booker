package io.zentechhotelbooker.fragments;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.activities.HomeActivity;
import io.zentechhotelbooker.activities.ViewRoomDetailsActivity;
import maes.tech.intentanim.CustomIntent;

public class MoreDetailsFragment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details_fragment);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.title_more_details));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(MoreDetailsFragment.this,ViewRoomDetailsActivity.class));

        CustomIntent.customType(MoreDetailsFragment.this,"right-to-left");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                startActivity(new Intent(MoreDetailsFragment.this,ViewRoomDetailsActivity.class));

                CustomIntent.customType(MoreDetailsFragment.this,"right-to-left");

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

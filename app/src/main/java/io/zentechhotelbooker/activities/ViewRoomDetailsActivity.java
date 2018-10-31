package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import io.zentechhotelbooker.R;
import maes.tech.intentanim.CustomIntent;

public class ViewRoomDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_room_details);

        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                startActivity(new Intent(ViewRoomDetailsActivity.this,HomeActivity.class));

                CustomIntent.customType(ViewRoomDetailsActivity.this,"left-to-right");

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

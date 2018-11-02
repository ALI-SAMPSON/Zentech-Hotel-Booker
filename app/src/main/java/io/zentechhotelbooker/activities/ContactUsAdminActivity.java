package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import io.zentechhotelbooker.R;
import maes.tech.intentanim.CustomIntent;

public class ContactUsAdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_admin);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.text_contact));
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(ContactUsAdminActivity.this,AdminDashBoardActivity.class));
                // Adds a bottom-to-up animations to the activity
                CustomIntent.customType(ContactUsAdminActivity.this,"right-to-left");
                // starts the home Activity
                finish();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ContactUsAdminActivity.this,AdminDashBoardActivity.class));
        // Adds a bottom-to-up animations to the activity
        CustomIntent.customType(ContactUsAdminActivity.this,"right-to-left");
        // starts the home Activity
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(ContactUsAdminActivity.this, "up-to-bottom");
    }
}

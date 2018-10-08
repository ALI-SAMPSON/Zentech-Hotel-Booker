package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import io.zentechhotelbooker.R;
import maes.tech.intentanim.CustomIntent;

public class ContactUsUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_user);

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
                // starts the home Activity
                finish();

                startActivity(new Intent(ContactUsUserActivity.this,HomeActivity.class));

                // Add fadein-to-fadeout animation to the activity
                CustomIntent.customType(ContactUsUserActivity.this,"fadein-to-fadeout");

                default:
                    break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        // Add fadein-to-fadeout animation to the activity
        CustomIntent.customType(ContactUsUserActivity.this,"fadein-to-fadeout");
    }
}

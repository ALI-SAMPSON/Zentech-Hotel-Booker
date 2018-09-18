package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import io.zentechhotelbooker.R;

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
                // starts the home Activity
                ContactUsAdminActivity.this.finish();
                startActivity(new Intent(ContactUsAdminActivity.this,AdminDashBoardActivity.class));
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

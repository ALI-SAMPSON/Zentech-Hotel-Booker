package io.zentechhotelbooker.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import io.zentechhotelbooker.R;
import maes.tech.intentanim.CustomIntent;

public class AboutUsUserActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView overview_CardView;
    private CardView mission_CardView;
    private CardView vision_CardView;

    //text to populate the overview, mission and vision of the company
    private TextView sub_Text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_admin);

        //checks if there is a toolbar, if yes it set the Home Button on it
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.text_about));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        overview_CardView = findViewById(R.id.cardViewOverview);
        mission_CardView = findViewById(R.id.cardViewMission);
        vision_CardView = findViewById(R.id.cardViewVision);

        sub_Text = findViewById(R.id.sub_text_1);

        overview_CardView.setOnClickListener(this);
        mission_CardView.setOnClickListener(this);
        vision_CardView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        //sub text for the various buttons
        String text_overview = " Zentech Hotel Booker seeks to avoid inconveniences,\n" +
                "        caused as a result of one going to Hotel or guest\n" +
                "        house for a room and all to realized that the\n" +
                "        rooms are all occupied. With  this system in place\n" +
                "        there is relatively much less stress and frustration.";

        String text_mission = " Providing a more flexible and convenient\n" +
                "        environment for our clients in booking \n" +
                "        Hotel rooms.";

        String text_vision = "  To deliver our best to customers\n" +
                "        in order to satisfy their needs thereby making\n" +
                "        life easier for all.";

        switch (view.getId()){
            case R.id.cardViewOverview:

                // Adds animation to the cardView
                YoYo.with(Techniques.ZoomInDown).playOn(overview_CardView);

                // sets text to this TextView
                sub_Text.setText(text_overview);

                break;
            case R.id.cardViewMission:

                // Adds animation to the cardView
                YoYo.with(Techniques.ZoomInUp).playOn(mission_CardView);

                // sets text to this TextView
                sub_Text.setText(text_mission);

                break;
            case R.id.cardViewVision:

                // Adds animation to the cardView
                YoYo.with(Techniques.ZoomInDown).playOn(vision_CardView);

                // sets text to this TextView
                sub_Text.setText(text_vision);

                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(AboutUsUserActivity.this,HomeActivity.class));
        // Add right-to-left animation to the activity
        CustomIntent.customType(AboutUsUserActivity.this,"right-to-left");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                startActivity(new Intent(AboutUsUserActivity.this,HomeActivity.class));
                // Add right-to-left animation to the activity
                CustomIntent.customType(AboutUsUserActivity.this,"right-to-left");
                // finishes and starts the next activity
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void finish() {
        super.finish();
        // Adds a custom animation to the intent
        CustomIntent.customType(AboutUsUserActivity.this,"right-to-left");
    }
}

package io.zentechhotelbooker.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.Timer;
import java.util.TimerTask;

import io.zentechhotelbooker.R;
import maes.tech.intentanim.CustomIntent;

public class AdminDashBoardActivity extends AppCompatActivity {

    //instances of the CardView
    private CardView cardView1;
    private CardView cardView2;
    private CardView cardView3;
    private CardView cardView4;
    private CardView cardView5;

    // View Objects of the textViews in the cardViews
    private TextView textView_add_rooms;
    private TextView textView_add_rooms_sub_text;
    private TextView textView_delete_rooms;
    private TextView textView_delete_rooms_sub_text;
    private TextView textView_reset_password;
    private TextView textView_reset_password_sub_text;
    private TextView textView_check_payments;
    private TextView textView_check_payments_sub_text;
    private TextView textView_logout;
    private TextView textView_logout_sub_text;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    // Animation class
    private Animation bounce_card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);

        // checks if there is a support actionBar
        // if there is then it adds a title to it.
        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle(getString(R.string.admin_dash_board));
        }

        //getting references to the various cardViews
        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        cardView4 = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);

        //instantiation of the class
        textView_add_rooms = findViewById(R.id.add_rooms);
        textView_add_rooms_sub_text = findViewById(R.id.add_rooms_sub_text);
        textView_delete_rooms = findViewById(R.id.delete_rooms);
        textView_delete_rooms_sub_text = findViewById(R.id.delete_rooms_sub_text);
        textView_reset_password = findViewById(R.id.reset_password);
        textView_reset_password_sub_text =  findViewById(R.id.reset_pass_sub_text);
        textView_check_payments = findViewById(R.id.check_payments);
        textView_check_payments_sub_text = findViewById(R.id.check_payments_sub_text);
        textView_logout =  findViewById(R.id.logout);
        textView_logout_sub_text = findViewById(R.id.logout_sub_text);


        mAuth = FirebaseAuth.getInstance();

        // instantiation of the class
        bounce_card = AnimationUtils.loadAnimation(this,R.anim.anim_fade_in);

        // method call
        animateTexView();

        cardViewMethods();//call to the method


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_about:
                // finishes this activity and open the About Us activity
                finish();
                startActivity(new Intent(AdminDashBoardActivity.this, AboutUsAdminActivity.class));
                // Adds a fadein-fadeout animations to the activity
                CustomIntent.customType(AdminDashBoardActivity.this, "bottom-to-up");
                break;
            case R.id.menu_contact:
                // finishes this activity and open the About Us activity
                AdminDashBoardActivity.this.finish();
                startActivity(new Intent(AdminDashBoardActivity.this, ContactUsAdminActivity.class));
                // Adds a fadein-fadeout animations to the activity
                CustomIntent.customType(AdminDashBoardActivity.this, "up-to-bottom");
                break;
                default:
                    break;
        }
        return super.onOptionsItemSelected(item);
    }

    // method to animate the textViews when activity is launched
    private void animateTexView(){
        // animation to add to TextView in the ADD ROOMS CardView
        YoYo.with(Techniques.FadeInUp).playOn(textView_add_rooms);
        YoYo.with(Techniques.SlideInLeft).playOn(textView_add_rooms_sub_text);

        // animation to add to TextView in the DELETE ROOMS CardView
        YoYo.with(Techniques.ZoomInDown).playOn(textView_delete_rooms);
        YoYo.with(Techniques.SlideInRight).playOn(textView_delete_rooms_sub_text);

        // animation to add to TextView in the RESET PASSWORD CardView
        YoYo.with(Techniques.ZoomInUp).playOn(textView_reset_password);
        YoYo.with(Techniques.SlideInLeft).playOn(textView_reset_password_sub_text);

        // animation to add to TextView in the CHECK PAYMENTS CardView
        YoYo.with(Techniques.ZoomIn).playOn(textView_check_payments);
        YoYo.with(Techniques.SlideInRight).playOn(textView_check_payments_sub_text);

        // animation to add to TextView in the CHECK PAYMENTS CardView
        YoYo.with(Techniques.RotateIn).playOn(textView_logout);
        YoYo.with(Techniques.SlideInUp).playOn(textView_logout_sub_text);
    }

    //method to handle the onClickListeners of the CardViews
    public void cardViewMethods(){

        //onclickListener for carView1
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // add a flip animation to the view using the YoYo Library
                YoYo.with(Techniques.FlipOutX).playOn(cardView1);

                //start the add rooms activity
                startActivity(new Intent(AdminDashBoardActivity.this,AddRoomsActivity.class));

                // Adds a fadein-fadeout animations to the activity
                CustomIntent.customType(AdminDashBoardActivity.this, "fadein-to-fadeout");

            }
        });

        //onclickListener for carView2
        cardView2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // add a flip animation to the view using the Yoyo  Library
                YoYo.with(Techniques.FlipOutX).playOn(cardView2);

                //starts the delete rooms activity
                startActivity(new Intent(AdminDashBoardActivity.this,DeleteRoomsActivity.class));

                // Adds a bottom-to-up animations to the activity
                CustomIntent.customType(AdminDashBoardActivity.this,"bottom-to-up");
            }
        });

        //onclickListener for carView3
        cardView3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // add a flip animation to the view using the YoYo Library
                YoYo.with(Techniques.FlipInX).playOn(cardView3);

                //starts the reset password activity
                startActivity(new Intent(AdminDashBoardActivity.this,ResetAdminPasswordActivity.class));

                // Add a fadein-to-fadeout animation to the activity
                CustomIntent.customType(AdminDashBoardActivity.this,"fadein-to-fadeout");
            }
        });

        //onclickListener for carView4
        cardView4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // add a flip animation to the view using the YoYo Library
                YoYo.with(Techniques.FlipInX).playOn(cardView4);

                //starts the check payment activity
                startActivity(new Intent(AdminDashBoardActivity.this, CheckPaymentActivity.class));

                // Add a up-to-bottom animation to the activity
                CustomIntent.customType(AdminDashBoardActivity.this,"up-to-bottom");
            }
        });

        //onclickListener for carView5
        cardView5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // add a flip animation to the view using the Yoyo
                YoYo.with(Techniques.StandUp).playOn(cardView5);

                //alert Dialog to alert the admin of logging out of the system
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashBoardActivity.this);
                builder.setTitle("Signout");
                builder.setMessage("Are you sure you want to signout of the system");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // signs admin out  of the system
                        mAuth.signOut();

                        // finish activity
                        finish();

                        //logs Admin out of the system and navigate him back to the Login Page
                        startActivity(new Intent(AdminDashBoardActivity.this, AdminLoginActivity.class));

                        // Add a fadein-to-fadeout animation to the activity
                        CustomIntent.customType(AdminDashBoardActivity.this,"fadein-to-fadeout");
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // hides the dialogInterface
                        dialogInterface.dismiss();
                    }
                });

                //create a new alert Dialog and displays it
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        // Adds a fadein-fadeout animations to the activity
        CustomIntent.customType(AdminDashBoardActivity.this, "fadein-to-fadeout");
    }
}

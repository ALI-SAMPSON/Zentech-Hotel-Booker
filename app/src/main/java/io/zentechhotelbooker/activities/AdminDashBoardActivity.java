package io.zentechhotelbooker.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import io.zentechhotelbooker.R;

public class AdminDashBoardActivity extends AppCompatActivity {

    //instances of the CardView
    private CardView cardView1;
    private CardView cardView2;
    private CardView cardView3;
    private CardView cardView4;
    private CardView cardView5;

    private ProgressDialog progressDialog;

    private DatabaseReference currentAdminRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);

        //getting references to the various cardViews
        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        cardView4 = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);

        //instantiation of the class

        currentAdminRef = FirebaseDatabase.getInstance().getReference().child("CurrentAdmin");

        cardViewMethod();//call to the method

    }

    //method to handle the onClickListeners of the CardViews
    public void cardViewMethod(){
        //onclickListener for carView1
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start the add rooms activity
                startActivity(new Intent(AdminDashBoardActivity.this,AddRoomsActivity.class));
            }
        });

        //onclickListener for carView2
        cardView2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //starts the delete rooms activity
                startActivity(new Intent(AdminDashBoardActivity.this,ViewAddedRoomsActivity.class));
            }
        });

        //onclickListener for carView3
        cardView3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //starts the edit credentials activity activity
                startActivity(new Intent(AdminDashBoardActivity.this,ResetAdminPasswordActivity.class));
            }
        });

        //onclickListener for carView4
        cardView4.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(AdminDashBoardActivity.this, CheckPaymentInfoActivity.class));
            }
        });

        //onclickListener for carView5
        cardView5.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //alert Dialog to alert the admin of logging out of the system
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminDashBoardActivity.this);
                builder.setTitle("Signout");
                builder.setMessage("Are you sure you want to exit the system");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressDialog = ProgressDialog.show(AdminDashBoardActivity.this,"",null,true,true);
                        progressDialog.setMessage("Please wait...");
                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                timer.cancel();
                            }
                        },10000);
                        //removes the field when user logs out
                        currentAdminRef.removeValue();
                        //logs Admin out of the system and navigate him back to the Login Page
                        startActivity(new Intent(AdminDashBoardActivity.this, AdminLoginActivity.class));
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                //create a new alert Dialog and displays it
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
    }

}

package io.zentechhotelbooker.adapters;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.activities.HomeActivity;
import io.zentechhotelbooker.activities.MakePaymentActivity;
import io.zentechhotelbooker.activities.ViewRoomDetailsUserActivity;
import io.zentechhotelbooker.models.Reservations;
import io.zentechhotelbooker.models.Rooms;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

import static android.content.Context.NOTIFICATION_SERVICE;

public class RecyclerViewAdapterUser extends RecyclerView.Adapter<RecyclerViewAdapterUser.ViewHolder> {

    private Context mCtx;
    private List<Rooms> roomsList;

    // notification variables
    private String CHANNEL_ID = "notification_channel_id";

    private int notificationId = 0;

    public RecyclerViewAdapterUser(Context mCtx, List<Rooms> roomsList) {
        this.mCtx = mCtx;
        this.roomsList = roomsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_room_items,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        // getting the position of each room and its details
        final Rooms rooms = roomsList.get(position);

        /** getting the room details and setting
         * it to the their respective view objects
         * or views
         */

        // animation to cardView
        YoYo.with(Techniques.ZoomIn).playOn(holder.room_cardView);

        // sets text to the TextViews
        holder.tv_room_type.setText(rooms.getRoomType());
        holder.tv_room_price.setText(" Price : GH¢ " + rooms.getRoomPrice());
        //holder.room_price.setText(" GH¢ " + rooms.getPrice());

        final String user_image_url = holder.user.getPhotoUrl().toString();

        //final String user_image_url = holder.users.getImageUrl();

        // using Glide Library to load images
        Glide.with(mCtx).load(rooms.getRoomImage_url()).into(holder.room_image);

        // checks for booked and non-booked rooms
        holder.paymentRef.child(rooms.getRoomNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    // disables the cardView if room is booked
                    //holder.room_cardView.setEnabled(false);
                    holder.room_cardView.setClickable(false);
                    holder.room_cardView.setFocusable(false);

                    // disables the View Details button if room is booked
                    holder.viewDetails.setEnabled(false);
                    holder.viewDetails.setClickable(false);
                    holder.viewDetails.setFocusable(false);

                    // displays a text with caption "Booked" to user
                    holder.tv_room_booked.setVisibility(View.VISIBLE);
                    holder.tv_room_reserved.setVisibility(View.GONE);

                }

                // if room is not booked check in reservation table
                else if(!dataSnapshot.exists()){

                    // getting uid of the user
                    final String user_name = holder.user.getDisplayName();
                    final String room_number = rooms.getRoomNumber();

                    holder.reservations.setUser_name(user_name);
                    holder.reservations.setRoom_number(room_number);

                    // checks for reserved and non-reserved rooms
                    holder.reservationRef.child(user_name)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.exists() && dataSnapshot.hasChild(room_number)){

                                        // sets visibility to true if room is reserved successfully
                                        holder.tv_room_reserved.setVisibility(View.VISIBLE);
                                        holder.tv_room_booked.setVisibility(View.GONE);

                                    }
                                    else{
                                        // sets visibility to gone
                                        holder.tv_room_reserved.setVisibility(View.GONE);
                                        holder.tv_room_booked.setVisibility(View.GONE);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // display an error message
                                    Toast.makeText(mCtx,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                                }
                            });


                }
                else{
                    // dismisses the textView
                    holder.tv_room_booked.setVisibility(View.GONE);
                    holder.tv_room_reserved.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // display an error message
                Toast.makeText(mCtx,databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });


        // checks if rooms are booked or not when user clicks on the cardView
        holder.room_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //code to the check if room has been booked already
                holder.paymentRef.child(rooms.getRoomNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //gets a snapshot of the data in the database
                        if (dataSnapshot.exists()) {

                            // disables the cardView if room is booked
                            holder.room_cardView.setEnabled(false);
                            holder.room_cardView.setClickable(false);
                            holder.room_cardView.setFocusable(false);

                            // disables the View Details button if room is booked
                            holder.viewDetails.setEnabled(false);
                            holder.viewDetails.setClickable(false);
                            holder.viewDetails.setFocusable(false);

                            holder.tv_room_booked.setVisibility(View.VISIBLE);

                            // animation to cardView
                            YoYo.with(Techniques.Shake).playOn(holder.room_cardView);

                            // displays a warning message
                            Toast.makeText(mCtx, "Room is already booked", Toast.LENGTH_LONG).show();
                        }
                        else{

                             // Creates an Alert Dialog
                           final AlertDialog.Builder builder = new AlertDialog.Builder(mCtx,
                                   android.R.style.Theme_Material_Dialog_Alert);
                           builder.setTitle("...Welcome...");
                           builder.setMessage("Please select one of the operations to proceed or click cancel to quit");
                           builder.setCancelable(false);

                           // set the Positive button for alert dialog
                           builder.setPositiveButton("BOOK NOW", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {

                                   // Creates new Intent
                                   Intent intentBook = new Intent(mCtx, MakePaymentActivity.class);
                                   intentBook.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                   // passing data to the payment activity
                                   intentBook.putExtra("user_name",holder.user.getDisplayName());
                                   intentBook.putExtra("room_number", rooms.getRoomNumber());
                                   intentBook.putExtra("room_type", rooms.getRoomType());
                                   intentBook.putExtra("room_price",  rooms.getRoomPrice());
                                   intentBook.putExtra("breakfast_food", rooms.getBreakfastServed());
                                   intentBook.putExtra("lunch_food", rooms.getLunchServed());
                                   intentBook.putExtra("supper_food",  rooms.getSupperServed());
                                   intentBook.putExtra("room_image_url",rooms.getRoomImage_url());
                                   intentBook.putExtra("user_image", user_image_url);
                                   // starting the activity
                                   mCtx.startActivity(intentBook);
                                   // Add a custom animation to the activity
                                   CustomIntent.customType(mCtx, "left-to-right");

                               }
                           });

                            // set the Positive button for alert dialog
                            builder.setNegativeButton("RESERVE NOW", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    // getting uid of the user
                                    final String uid  = holder.user.getUid();
                                    final String user_name = holder.user.getDisplayName();
                                    final String mobile_number = holder.users.getMobile_number();
                                    final String room_image_url = rooms.getRoomImage_url();
                                    final String room_number = rooms.getRoomNumber();
                                    final String room_type = rooms.getRoomType();
                                    final String room_price = rooms.getRoomPrice();

                                    holder.reservations.setUid(uid);
                                    holder.reservations.setUser_name(user_name);
                                    holder.reservations.setUser_image_url(user_image_url);
                                    holder.reservations.setMobile_number(mobile_number);
                                    holder.reservations.setRoom_image_url(room_image_url);
                                    holder.reservations.setRoom_number(room_number);
                                    holder.reservations.setRoom_type(room_type);
                                    holder.reservations.setRoom_price(room_price);

                                    holder.reservationRef.child(user_name)
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                        if(dataSnapshot.exists() && dataSnapshot.hasChild(room_number)){

                                                                Toast.makeText(mCtx, "Oops!, " + user_name +
                                                                                " you have already made reservation for this room "
                                                                        , Toast.LENGTH_LONG).show();

                                                                return;

                                                        }
                                                        else if(dataSnapshot.exists() && !dataSnapshot.hasChild(room_number)){

                                                        holder.reservationRef.child(user_name).child(room_number)
                                                                .setValue(holder.reservations)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            // display a success message
                                                                            Toast.makeText(mCtx, R.string.room_reserved, Toast.LENGTH_LONG).show();
                                                                            //builder.setNegativeButton("Reserve Now",)
                                                                            // sets visibility to true if room is reserved successfully
                                                                            holder.tv_room_reserved.setVisibility(View.VISIBLE);
                                                                            holder.tv_room_booked.setVisibility(View.GONE);

                                                                        }
                                                                        else{
                                                                            Toast.makeText(mCtx, task.getException().getMessage(),
                                                                                    Toast.LENGTH_LONG).show();
                                                                            // set visibility to off on both
                                                                            holder.tv_room_reserved.setVisibility(View.GONE);
                                                                            holder.tv_room_booked.setVisibility(View.GONE);
                                                                        }
                                                                    }
                                                                });

                                                         }
                                                        else {

                                                            holder.reservationRef.child(user_name).child(room_number)
                                                                    .setValue(holder.reservations)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                // display a success message
                                                                                Toast.makeText(mCtx, R.string.room_reserved, Toast.LENGTH_LONG).show();
                                                                                // sets visibility to true if room is reserved successfully
                                                                                holder.tv_room_reserved.setVisibility(View.VISIBLE);
                                                                                holder.tv_room_booked.setVisibility(View.GONE);
                                                                            }
                                                                            else{
                                                                                    Toast.makeText(mCtx, task.getException().getMessage(),
                                                                                            Toast.LENGTH_LONG).show();
                                                                                // set visibility to off on both
                                                                                holder.tv_room_reserved.setVisibility(View.GONE);
                                                                                holder.tv_room_booked.setVisibility(View.GONE);
                                                                            }
                                                                        }
                                                                    });
                                                            }

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    Toast.makeText(mCtx,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            });


                                    // dismiss the dialogue interface
                                    dialogInterface.dismiss();

                                }
                            });

                           builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   // dismiss the dialogue interface
                                   dialogInterface.dismiss();
                               }
                           });


                           //
                           AlertDialog alertDialog = builder.create();
                           alertDialog.show();

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // display an error message
                        Toast.makeText(mCtx,databaseError.getMessage(),Toast.LENGTH_LONG).show();

                    }
                });


            }
        });



       // OnClickListener for More Details Button
       holder.viewDetails.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               // Creates new Intent and passes the strings to the intent to be opened
               Intent intentDetails = new Intent(mCtx, ViewRoomDetailsUserActivity.class);
               intentDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
               // passing data to the payment activity
               intentDetails.putExtra("user_name",holder.user.getDisplayName());
               intentDetails.putExtra("room_number", rooms.getRoomNumber());
               intentDetails.putExtra("room_type", rooms.getRoomType());
               intentDetails.putExtra("breakfast","Breakfast : " + rooms.getBreakfastServed());
               intentDetails.putExtra("lunch","Lunch : " + rooms.getLunchServed());
               intentDetails.putExtra("supper","Supper : " + rooms.getSupperServed());
               intentDetails.putExtra("room_price", "GH¢ " + rooms.getRoomPrice());
               intentDetails.putExtra("room_image_url",rooms.getRoomImage_url());
               intentDetails.putExtra("user_image", user_image_url);
               // starting the activity
               mCtx.startActivity(intentDetails);
               // Add a custom animation to the activity
               CustomIntent.customType(mCtx, "fadein-to-fadeout");

           }
       });


    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

     public class ViewHolder extends RecyclerView.ViewHolder{

        // Creating objects of the views
        CardView room_cardView;

        ImageView room_image;
        // Booked or reserved room indicators
        TextView tv_room_booked;
        TextView tv_room_reserved;
        TextView tv_room_type;
        TextView tv_room_price;

        Button viewDetails;

        FirebaseAuth mAuth;

        FirebaseUser user;

        Reservations reservations;

        DatabaseReference reservationRef;

        Users users;

        DatabaseReference paymentRef;

        public ViewHolder(View itemView) {
            super(itemView);

            // Assigning ids to the objects
            room_cardView = itemView.findViewById(R.id.room_cardView);
            room_image = itemView.findViewById(R.id.room_image);
            tv_room_booked = itemView.findViewById(R.id.tv_room_booked);
            tv_room_reserved = itemView.findViewById(R.id.tv_room_reserved);
            tv_room_type = itemView.findViewById(R.id.tv_room_type);
            tv_room_price = itemView.findViewById(R.id.tv_room_price);

            viewDetails = itemView.findViewById(R.id.btn_view);

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            paymentRef = FirebaseDatabase.getInstance().getReference("Payments");

            users = new Users();

            reservations = new Reservations();

            reservationRef = FirebaseDatabase.getInstance().getReference("Reservations");

        }

    }

}

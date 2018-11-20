package io.zentechhotelbooker.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.activities.MakePaymentActivity;
import io.zentechhotelbooker.activities.ViewRoomDetailsUserActivity;
import io.zentechhotelbooker.models.Rooms;
import maes.tech.intentanim.CustomIntent;

public class RecyclerViewAdapterUser extends RecyclerView.Adapter<RecyclerViewAdapterUser.ViewHolder> {

    private Context mCtx;
    private List<Rooms> roomsList;

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

        final String user_image = holder.user.getPhotoUrl().toString();

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

                }
                else{
                    // dismisses the textView
                    holder.tv_room_booked.setVisibility(View.GONE);
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
                           AlertDialog.Builder builder = new AlertDialog.Builder(mCtx,
                                   android.R.style.Theme_Material_Dialog_Alert);
                           builder.setTitle("Continue...");
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
                                   intentBook.putExtra("user_image", user_image);
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
                                    // dismiss the dialogue interface
                                    dialogInterface.dismiss();
                                    Toast.makeText(mCtx,R.string.room_reserved,Toast.LENGTH_LONG).show();
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
                                    intentBook.putExtra("user_image", user_image);
                                    // starting the activity
                                    mCtx.startActivity(intentBook);

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
               intentDetails.putExtra("user_image", user_image);
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
        TextView tv_room_booked;
        TextView tv_room_type;
        TextView tv_room_price;

        Button viewDetails;

        FirebaseAuth mAuth;

        FirebaseUser user;

        DatabaseReference paymentRef;


        public ViewHolder(View itemView) {
            super(itemView);

            // Assigning ids to the objects
            room_cardView = itemView.findViewById(R.id.room_cardView);
            room_image = itemView.findViewById(R.id.room_image);
            tv_room_booked = itemView.findViewById(R.id.tv_room_booked);
            tv_room_type = itemView.findViewById(R.id.tv_room_type);
            tv_room_price = itemView.findViewById(R.id.tv_room_price);

            viewDetails = itemView.findViewById(R.id.btn_view);

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            paymentRef = FirebaseDatabase.getInstance().getReference("Payments");

        }

    }

}

package io.zentechhotelbooker.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import io.zentechhotelbooker.activities.DisplayMoreImagesActivity;
import io.zentechhotelbooker.activities.MakePaymentActivity;
import io.zentechhotelbooker.activities.ViewRoomDetailsActivity;
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

                            holder.tv_room_booked.setVisibility(View.VISIBLE);

                            // animation to cardView
                            YoYo.with(Techniques.Shake).playOn(holder.room_cardView);

                            // displays a warning message
                            Toast.makeText(mCtx, "Room is already booked", Toast.LENGTH_LONG).show();
                        }
                        else {

                            // Creates new Intent and passes the strings to the intent to be opened
                            Intent intent = new Intent(mCtx, ViewRoomDetailsActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            // passing data to the payment activity
                            intent.putExtra("user_name",holder.user.getDisplayName());
                            intent.putExtra("room_number", rooms.getRoomNumber());
                            intent.putExtra("room_type", rooms.getRoomType());
                            intent.putExtra("breakfast","Breakfast : " + rooms.getBreakfastServed());
                            intent.putExtra("lunch","Lunch : " + rooms.getLunchServed());
                            intent.putExtra("supper","Supper : " + rooms.getSupperServed());
                            intent.putExtra("room_price", "GH¢ " + rooms.getRoomPrice());
                            intent.putExtra("room_image_url",rooms.getRoomImage_url());
                            intent.putExtra("user_image", user_image);
                            // starting the activity
                            mCtx.startActivity(intent);
                            // Add a custom animation to the activity
                            CustomIntent.customType(mCtx,"left-to-right");

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

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            paymentRef = FirebaseDatabase.getInstance().getReference("Payments");

        }

    }

}

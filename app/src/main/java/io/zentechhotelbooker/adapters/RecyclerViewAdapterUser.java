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
import io.zentechhotelbooker.models.Rooms;
import maes.tech.intentanim.CustomIntent;

public class RecyclerViewAdapterUser extends RecyclerView.Adapter<RecyclerViewAdapterUser.ViewHolder> {

    private Context mCtx;
    private List<Rooms> roomsList;

    // private member variable
    private onItemClickListener mListener;

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
        holder.tv_room_type.setText(rooms.getRoom_type());
        holder.tv_room_price.setText(" Price : GH¢ " + rooms.getPrice());
        //holder.room_price.setText(" GH¢ " + rooms.getPrice());

        final String user_image = holder.user.getPhotoUrl().toString();

        // using Glide Library to load images
        Glide.with(mCtx).load(rooms.getRoom_image()).into(holder.room_image);

        // checks for booked and non-booked rooms
        holder.paymentRef.child(rooms.getRoom_number()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                holder.paymentRef.child(rooms.getRoom_number()).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            // Creates an Alert Dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(mCtx,
                                    android.R.style.Theme_Material_Dialog_Alert);
                            builder.setTitle("Book this Room");
                            builder.setMessage("Are you sure you want to book this room and make payment for it?");
                            builder.setCancelable(false);

                            // set the Positive button for alert dialog
                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    // Creates new Intent
                                    Intent intent = new Intent(mCtx, MakePaymentActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    // passing data to the payment activity
                                    intent.putExtra("room_number", rooms.getRoom_number());
                                    intent.putExtra("room_type", rooms.getRoom_type());
                                    intent.putExtra("room_price", "GH¢ " + rooms.getPrice());
                                    intent.putExtra("user_image", user_image);
                                    // starting the activity
                                    mCtx.startActivity(intent);
                                    // Add a custom animation to the activity
                                    CustomIntent.customType(mCtx, "right-to-left");

                                }
                            });

                            // set the Positive button for alert dialog
                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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

        // OnclickListener for button to view more images of a rooms
        holder.btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // opens the Display more Images Activity
                Intent intent = new Intent(mCtx,DisplayMoreImagesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("images",rooms.getRoom_image());
                mCtx.startActivity(intent);
                // Add a custom animation to the activity
                CustomIntent.customType(mCtx, "right-to-left");
            }
        });

    }

    // method to call the alert Dialog
    /*public void showAlertDialog() {
        // Creates an Alert Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Book this Room");
        builder.setMessage("Are you sure you want to book this room and make payment for it?");
        builder.setCancelable(false);

        // set the Positive button for alert dialog
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Creates new Intent
                Intent intent = new Intent(mCtx, MakePaymentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // passing data to the payment activity
                intent.putExtra("room_number", rooms.getRoom_number());
                intent.putExtra("room_type", rooms.getRoom_type());
                intent.putExtra("room_price", "GH¢ " + rooms.getPrice());
                intent.putExtra("user_image", user_image);
                // starting the activity
                mCtx.startActivity(intent);
                // Add a custom animation to the activity
                CustomIntent.customType(mCtx, "right-to-left");

            }
        });

        // set the Positive button for alert dialog
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
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
    */

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

     public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        // Creating objects of the views
        CardView room_cardView;

        ImageView room_image;
        TextView tv_room_booked;
        TextView tv_room_type;
        TextView tv_room_price;
        Button btn_view;

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
            btn_view = itemView.findViewById(R.id.button_view);

            mAuth = FirebaseAuth.getInstance();
            user = mAuth.getCurrentUser();
            paymentRef = FirebaseDatabase.getInstance().getReference("Payments");

            // OnClickListeners for the ContextMenu
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //contextMenu.setHeaderTitle("Select Action");
            MenuItem viewRoomImages = contextMenu.add(Menu.NONE, 1, 1, "View More Images");
            MenuItem cancel = contextMenu.add(Menu.NONE, 2, 2, "Cancel");


            viewRoomImages.setOnMenuItemClickListener(this);
            cancel.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {

                    // Check menuItem Clicked and respond accordingly
                    switch (menuItem.getItemId()) {
                        case 1:
                            mListener.viewMoreImagesClick(position);
                            return true;
                        case 2:
                            mListener.onCancelClick(position);
                            return true;
                    }

                }
            }
            return false;
        }
    }

    // Interface for ContextMenu
    public interface onItemClickListener {

        // Method to handle default clicks
        void onItemClick(int position);

        // Method to handle ViewRoom (dismiss ContextMenu) Clicks
        void onCancelClick(int position);

        // Method to handle deletion of item
        void viewMoreImagesClick(int position);

    }

    // OnItemClickListener implementing the interface
    public void setOnItemClickListener(onItemClickListener listener) {
        mListener = listener;
    }

}

package io.zentechhotelbooker.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.activities.ViewRoomDetailsAdminActivity;
import io.zentechhotelbooker.models.Reservations;
import io.zentechhotelbooker.models.Rooms;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

public class RecyclerViewAdapterManageRooms extends RecyclerView.Adapter<RecyclerViewAdapterManageRooms.ViewHolder> {

    // context variable and a list of the Rooms
     Context mCtx;
     List<Rooms> roomsList;

     // private member variable
     private onItemClickListener mListener;

    public RecyclerViewAdapterManageRooms(Context mCtx, List<Rooms> roomsList ){
        this.mCtx = mCtx;
        this.roomsList = roomsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_room_items,parent,false);

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
        YoYo.with(Techniques.BounceInDown).playOn(holder.room_cardView);

        // getting the input from the database and setting them to the textViews
        holder.room_type.setText(rooms.getRoomType());
        holder.room_price.setText(" Price : GH¢" + rooms.getRoomPrice());

        //Loading image into Glide using the glide library.
        Glide.with(mCtx).load(rooms.getRoomImage_url()).into(holder.room_image);


        // checks for booked and non-booked rooms
        holder.paymentRef.child(rooms.getRoomNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){
                    // disables the cardView if room is booked
                    holder.room_cardView.setEnabled(false);
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
                    //final String user_name = holder.user.getDisplayName();
                    final String room_number = rooms.getRoomNumber();

                    //holder.reservations.setUser_name(user_name);
                    //holder.reservations.setRoom_number(room_number);

                    // checks for reserved and non-reserved rooms
                    holder.reservationRef.orderByChild("room_number")
                            .equalTo(room_number)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    // sets visibility to true if room is reserved successfully
                                    holder.tv_room_reserved.setVisibility(View.VISIBLE);
                                    holder.tv_room_booked.setVisibility(View.GONE);

                                     /*if (dataSnapshot.hasChild(room_number)) {
                                            // sets visibility to true if room is reserved successfully
                                            holder.tv_room_reserved.setVisibility(View.VISIBLE);
                                            holder.tv_room_booked.setVisibility(View.GONE);

                                        } else {
                                            // sets visibility to gone
                                            holder.tv_room_reserved.setVisibility(View.GONE);
                                            holder.tv_room_booked.setVisibility(View.GONE);
                                        }
                                        */

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

                            holder.tv_room_booked.setVisibility(View.VISIBLE);

                            // animation to cardView
                            YoYo.with(Techniques.Shake).playOn(holder.room_cardView);

                            // displays a warning message
                            Toast.makeText(mCtx, "Room is already booked", Toast.LENGTH_LONG).show();
                        }
                        else{
                            // dismisses the textView
                            holder.tv_room_booked.setVisibility(View.GONE);
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
                Intent intentDetails = new Intent(mCtx, ViewRoomDetailsAdminActivity.class);
                intentDetails.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                // passing data to the payment activity
                intentDetails.putExtra("room_number", rooms.getRoomNumber());
                intentDetails.putExtra("room_type", rooms.getRoomType());
                intentDetails.putExtra("breakfast","Breakfast : " + rooms.getBreakfastServed());
                intentDetails.putExtra("lunch","Lunch : " + rooms.getLunchServed());
                intentDetails.putExtra("supper","Supper : " + rooms.getSupperServed());
                intentDetails.putExtra("room_price", "GH¢ " + rooms.getRoomPrice());
                intentDetails.putExtra("room_image_url",rooms.getRoomImage_url());
                // starting the activity
                mCtx.startActivity(intentDetails);
                // Add a custom animation to the activity
                CustomIntent.customType(mCtx, "fadein-to-fadeout");

            }
        });


    }

    // class to return the size of the rooms added to the database
    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        // Creating object of the classes
        TextView tv_room_booked;
        TextView tv_room_reserved;
        CardView room_cardView;
        ImageView room_image;
        TextView room_type;
        TextView room_price;

        Button viewDetails;

        ProgressBar progressBar;

        Reservations reservations;

        DatabaseReference reservationRef;

        Users users;

        DatabaseReference paymentRef;

        // constructor
        public ViewHolder(View itemView){
            super(itemView);

            // settings ids to those in the recyclerview_list.xml file

            room_cardView = itemView.findViewById(R.id.room_cardView);

            room_image = itemView.findViewById(R.id.room_image);
            room_type = itemView.findViewById(R.id.tv_room_type);
            room_price = itemView.findViewById(R.id.tv_room_price);
            tv_room_booked = itemView.findViewById(R.id.tv_room_booked);
            tv_room_reserved = itemView.findViewById(R.id.tv_room_reserved);
            viewDetails = itemView.findViewById(R.id.btn_view);

            progressBar = itemView.findViewById(R.id.progressBar);

            //mRoomRef = FirebaseDatabase.getInstance().getReference("Rooms");

            users = new Users();

            reservations = new Reservations();

            reservationRef = FirebaseDatabase.getInstance().getReference("Reservations");

            paymentRef = FirebaseDatabase.getInstance().getReference("Payments");

            // OnClickListeners for the ContextMenu
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onClick(View view) {
            if(mListener != null){
                int position  = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //contextMenu.setHeaderTitle("Select Action");
            MenuItem deleteRooms = contextMenu.add(Menu.NONE,1,1,"Delete Room");
            MenuItem cancel = contextMenu.add(Menu.NONE,2,2, "Cancel");


            deleteRooms.setOnMenuItemClickListener(this);
            cancel.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(mListener != null){
                int position  = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){

                    // Check menuItem Clicked and respond accordingly
                    switch (menuItem.getItemId()){
                        case 1:
                            mListener.onDeleteClick(position);
                            return true;
                        case 2 :
                            mListener.onCancelClick(position);
                            return true;
                    }

                }
            }
            return false;
        }
    }

    // Interface for ContextMenu
    public interface onItemClickListener{

        // Method to handle default clicks
        void onItemClick(int position);

        // Method to handle ViewRoom (dismiss ContextMenu) Clicks
        void onCancelClick(int position);

        // Method to handle deletion of item
        void onDeleteClick(int position);

    }

    // OnItemClickListener implementing the interface
    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

}

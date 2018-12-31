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
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.zentechhotelbooker.R;
import io.zentechhotelbooker.activities.ViewRoomDetailsAdminActivity;
import io.zentechhotelbooker.models.Users;
import maes.tech.intentanim.CustomIntent;

public class RecyclerViewAdapterManageUsers extends RecyclerView.Adapter<RecyclerViewAdapterManageUsers.ViewHolder> {

    // context variable and a list of the Rooms
    Context mCtx;
    List<Users> usersList;

    // private member variable
    private onItemClickListener mListener;

    public RecyclerViewAdapterManageUsers(Context mCtx, List<Users> usersList ){
        this.mCtx = mCtx;
        this.usersList = usersList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_users_items,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        // getting the position of each room and its details
        final Users users = usersList.get(position);

        /** getting the room details and setting
         * it to the their respective view objects
         * or views
         */
        // animation to cardView
        YoYo.with(Techniques.ZoomOutUp).playOn(holder.itemView);

        // getting the input from the database and setting them to the textViews
        holder.username.setText(" Username : " + users.getUsername());
        holder.gender.setText(" Gender : " + users.getGender());
        holder.phone.setText(" Phone : " + users.getMobile_number());

        if (users.getImageUrl() == null) {
            holder.profile_image.setImageResource(R.drawable.ic_person_white);
        }
        else {
        //Loading image into Glide using the glide library.
         Glide.with(mCtx).load(users.getImageUrl()).into(holder.profile_image);

        }


    }

    // class to return the size of the rooms added to the database
    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        // Creating object of the classes
        // Creating objects of the views
        TextView tv_room_booked;
        CircleImageView profile_image;
        TextView username;
        TextView gender;
        TextView phone;

        Button viewDetails;

        ProgressBar progressBar;

        //DatabaseReference  mRoomRef;

        DatabaseReference usersdBRef;

        // constructor
        public ViewHolder(View itemView){
            super(itemView);

            // settings ids to those in the recyclerview_list.xml file

            profile_image = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.tv_username);
            gender = itemView.findViewById(R.id.tv_gender);
            phone = itemView.findViewById(R.id.tv_phone);

            progressBar = itemView.findViewById(R.id.progressBar);

            usersdBRef = FirebaseDatabase.getInstance().getReference("Users");

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
            MenuItem deleteUser = contextMenu.add(Menu.NONE,1,1,"Delete User");
            MenuItem cancel = contextMenu.add(Menu.NONE,2,2, "Cancel");


            deleteUser.setOnMenuItemClickListener(this);
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

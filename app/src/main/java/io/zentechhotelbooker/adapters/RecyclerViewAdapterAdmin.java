package io.zentechhotelbooker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Rooms;

public class RecyclerViewAdapterAdmin extends RecyclerView.Adapter<RecyclerViewAdapterAdmin.ViewHolder> {

    // context variable and a list of the Rooms
     Context mCtx;
     List<Rooms> roomsList;

     // private member variable
     private onItemClickListener mListener;

    public RecyclerViewAdapterAdmin(Context mCtx, List<Rooms> roomsList ){
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
        YoYo.with(Techniques.BounceInDown).playOn(holder.cardView);

        // getting the input from the database and setting them to the textViews
        holder.room_type.setText(rooms.getRoomType());
        holder.room_price.setText(" Price : GH¢" + rooms.getRoomPrice());

        //Loading image into Glide using the glide library.
        Glide.with(mCtx).load(rooms.getRoomImage_url()).into(holder.room_image);

    }

    // class to return the size of the rooms added to the database
    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        // Creating object of the classes
        RecyclerView recyclerView;
        CardView cardView;
        ImageView room_image;
        TextView room_type;
        TextView room_price;

        ProgressBar progressBar;

        DatabaseReference  mDatabaseRef;

        FirebaseStorage mStorage;

        // constructor
        public ViewHolder(View itemView){
            super(itemView);

            // settings ids to those in the recyclerview_list.xml file

            recyclerView = itemView.findViewById(R.id.recyclerView);

            cardView = itemView.findViewById(R.id.room_cardView);

            room_image = itemView.findViewById(R.id.room_image);
            room_type = itemView.findViewById(R.id.tv_room_type);
            room_price = itemView.findViewById(R.id.tv_room_price);

            progressBar = itemView.findViewById(R.id.progressBar);

            mDatabaseRef = FirebaseDatabase.getInstance().getReference("Rooms");

            // creating an instance of a Firebase Storage
            mStorage = FirebaseStorage.getInstance();

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

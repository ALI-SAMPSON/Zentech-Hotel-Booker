package io.zentechhotelbooker.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.zip.Inflater;

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Rooms;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    // context variable and a list of the Rooms
     Context mCtx;
     List<Rooms> roomsList;

    public RecyclerViewAdapter(Context mCtx, List<Rooms> roomsList ){
        this.mCtx = mCtx;
        this.roomsList = roomsList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_items,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // getting the position of each room and its details
        Rooms rooms = roomsList.get(position);

        /** getting the room details and setting
         * it to the their respective view objects
         * or views
         */
        holder.room_number.setText(rooms.getRoom_number());
        holder.room_price.setText(rooms.getPrice());

        //Loading image into Glide using the glide library.
        Glide.with(mCtx).load(rooms.getRoom_image()).into(holder.room_image);

    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView room_image;
        TextView room_number;
        TextView room_price;

        public ViewHolder(View itemView){

            super(itemView);

            room_image = itemView.findViewById(R.id.room_image);
            room_number = itemView.findViewById(R.id.room_number);
            room_price = itemView.findViewById(R.id.room_price);


        }

    }

}

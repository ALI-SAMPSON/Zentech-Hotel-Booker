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

import io.zentechhotelbooker.R;
import io.zentechhotelbooker.models.Rooms;

public class RecyclerViewAdapterRoomImages extends RecyclerView.Adapter<RecyclerViewAdapterRoomImages.ViewHolder> {

    Context mCtx;
    List<String> fileNameList;

    public RecyclerViewAdapterRoomImages(Context mCtx, List<String> fileNameList){
        this.mCtx = mCtx;
        this.fileNameList = fileNameList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_add_mul_room_images,parent,false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // getting the position of each room and its details
        final String fileName = fileNameList.get(position);

        // loads the information into the various views
        holder.roomNumbers.setText(fileName);
        //holder.roomNumbers.setText(rooms.getRoom_number());
        //holder.roomTypes.setText(rooms.getRoom_type());
        //Glide.with(mCtx).load(rooms.getRoom_image()).into(holder.room_images);

    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView room_images;
        TextView roomNumbers;
        TextView roomTypes;


        public ViewHolder(View itemView) {
            super(itemView);

            room_images = itemView.findViewById(R.id.room_image);
            roomNumbers = itemView.findViewById(R.id.room_number);
            roomTypes = itemView.findViewById(R.id.room_type);
        }
    }

}
